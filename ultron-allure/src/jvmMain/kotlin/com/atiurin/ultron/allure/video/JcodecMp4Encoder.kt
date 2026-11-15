package com.atiurin.ultron.allure.video

import org.jcodec.codecs.h264.H264Encoder
import org.jcodec.codecs.h264.encode.RateControl
import org.jcodec.codecs.h264.io.model.SliceType
import org.jcodec.common.Codec
import org.jcodec.common.VideoCodecMeta
import org.jcodec.common.io.ByteBufferSeekableByteChannel
import org.jcodec.common.io.NIOUtils
import org.jcodec.common.model.ColorSpace
import org.jcodec.common.model.Packet
import org.jcodec.common.model.Picture
import org.jcodec.common.model.Rect
import org.jcodec.common.model.Size
import org.jcodec.containers.mp4.muxer.MP4Muxer
import org.jcodec.scale.RgbToYuv420j
import java.nio.ByteBuffer

/*
 * JCodec 0.2.5 signatures this encoder relies on, read from the jar (reflection over jcodec-0.2.5.jar), not
 * from memory: the 6def6d attempt was written against `TrackType` and `addTrackForCompressed`, neither exists
 * in 0.2.5, and neither is used here.
 *
 *   MP4Muxer.createMP4MuxerToChannel(SeekableByteChannel): MP4Muxer
 *   MP4Muxer.addVideoTrack(Codec, VideoCodecMeta): MuxerTrack          // MuxerTrack.addFrame(Packet)
 *   MP4Muxer.finish()
 *   VideoCodecMeta.createSimpleVideoCodecMeta(Size, ColorSpace): VideoCodecMeta
 *   Packet.createPacket(ByteBuffer data, long pts, int timescale, long duration, long frameNo,
 *                       Packet.FrameType, TapeTimecode): Packet
 *   H264Encoder.createH264Encoder(): H264Encoder                       // setKeyInterval(int)
 *   H264Encoder(RateControl)                                           // the constructor takes the rate control
 *   H264Encoder.encodeFrame(Picture, ByteBuffer out): VideoEncoder.EncodedFrame  // getData(), isKeyFrame()
 *   H264Encoder.estimateBufferSize(Picture): int
 *   RateControl { accept(int): int; initialQpDelta(): int; startPicture(Size, int, SliceType): int }
 *   H264FixedRateControl(int) is a bitrate control, not a fixed QP: there is no constant-QP class in 0.2.5,
 *   so FixedQp below implements the public RateControl interface.
 *   Picture.create(w, h, ColorSpace) / createCropped(w, h, ColorSpace, Rect) / createPicture(w, h, byte[][], ColorSpace)
 *   RgbToYuv420j().transform(Picture rgb, Picture yuv)                 // samples are signed, value - 128
 *   ByteBufferSeekableByteChannel.writeToByteBuffer(ByteBuffer) / getContents()
 *   The H.264 track of MP4Muxer takes Annex B packets as the encoder writes them: it cuts SPS/PPS out into
 *   `avcC` and rewrites start codes to 4-byte lengths itself, so no H264Utils call happens here (converting
 *   before the muxer would do it twice). JcodecMp4EncoderTest checks `avcC` and the NAL lengths of every sample.
 */

/** H.264 in mp4 for the desktop target: JCodec encoder and muxer, in memory. */
internal class JcodecMp4Encoder : VideoEncoder {
    override suspend fun encode(frames: List<ApngFrame>, options: VideoOptions): EncodedVideo {
        require(frames.isNotEmpty()) { "no frames to encode" }
        val timeline = frameTimeline(frames)
        val first = decodePng(frames.first().png)
        // The container is the even size of the first frame; the encoder works on a multiple of 16 and crops to it.
        val width = evenSize(first.width)
        val height = evenSize(first.height)
        val paddedWidth = (width + 15) / 16 * 16
        val paddedHeight = (height + 15) / 16 * 16

        val channel = ByteBufferSeekableByteChannel.writeToByteBuffer(ByteBuffer.allocate(INITIAL_CAPACITY))
        val muxer = MP4Muxer.createMP4MuxerToChannel(channel)
        val track = muxer.addVideoTrack(Codec.H264, VideoCodecMeta.createSimpleVideoCodecMeta(Size(width, height), ColorSpace.YUV420J))
        val encoder = H264Encoder(FixedQp(qpFor(options.quality))).apply { keyInterval = KEY_INTERVAL }
        val transform = RgbToYuv420j()

        frames.forEachIndexed { index, frame ->
            val raw = fitTo(if (index == 0) first else decodePng(frame.png), paddedWidth, paddedHeight)
            val yuv = Picture.createCropped(paddedWidth, paddedHeight, ColorSpace.YUV420J, Rect(0, 0, width, height))
            transform.transform(rgbPicture(raw), yuv)

            val encoded = encoder.encodeFrame(yuv, ByteBuffer.allocate(encoder.estimateBufferSize(yuv)))
            val data = encoded.data
            val type = if (encoded.isKeyFrame) Packet.FrameType.KEY else Packet.FrameType.INTER
            track.addFrame(Packet.createPacket(data, timeline[index].ptsMs, Mp4Muxer.TIMESCALE, timeline[index].durationMs.toLong(), index.toLong(), type, null))
        }
        muxer.finish()
        return EncodedVideo(NIOUtils.toArray(channel.contents), "mp4", "video/mp4")
    }

    /** JCodec wants RGB bytes shifted to signed (value - 128); the frame is BGRA. */
    private fun rgbPicture(frame: RawFrame): Picture {
        val rgb = ByteArray(frame.width * frame.height * 3)
        val px = frame.pixels
        for (i in 0 until frame.width * frame.height) {
            rgb[i * 3] = (px[i * 4 + 2].toInt() and 0xFF).minus(128).toByte()
            rgb[i * 3 + 1] = (px[i * 4 + 1].toInt() and 0xFF).minus(128).toByte()
            rgb[i * 3 + 2] = (px[i * 4].toInt() and 0xFF).minus(128).toByte()
        }
        return Picture.createPicture(frame.width, frame.height, arrayOf(rgb), ColorSpace.RGB)
    }

    private fun qpFor(quality: VideoQuality): Int = when (quality) {
        VideoQuality.LOW -> 34
        VideoQuality.MEDIUM -> 28
        VideoQuality.HIGH -> 22
    }

    /** Constant quantiser for every macroblock: the size follows the content, the quality follows [VideoQuality]. */
    private class FixedQp(private val qp: Int) : RateControl {
        override fun accept(bits: Int): Int = 0
        override fun initialQpDelta(): Int = 0
        override fun startPicture(sz: Size, maxSize: Int, sliceType: SliceType): Int = qp
    }

    private companion object {
        const val KEY_INTERVAL = 10
        const val INITIAL_CAPACITY = 1 shl 20
    }
}
