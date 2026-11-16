@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package com.atiurin.ultron.allure.video

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.plus
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.AVFoundation.AVAssetWriter
import platform.AVFoundation.AVAssetWriterInput
import platform.AVFoundation.AVAssetWriterInputPixelBufferAdaptor
import platform.AVFoundation.AVAssetWriterStatusCompleted
import platform.AVFoundation.AVAssetWriterStatusFailed
import platform.AVFoundation.AVFileTypeMPEG4
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVVideoAverageBitRateKey
import platform.AVFoundation.AVVideoCodecKey
import platform.AVFoundation.AVVideoCodecTypeH264
import platform.AVFoundation.AVVideoCompressionPropertiesKey
import platform.AVFoundation.AVVideoHeightKey
import platform.AVFoundation.AVVideoProfileLevelH264BaselineAutoLevel
import platform.AVFoundation.AVVideoProfileLevelKey
import platform.AVFoundation.AVVideoWidthKey
import platform.CoreMedia.CMTimeMake
import platform.CoreVideo.CVPixelBufferCreate
import platform.CoreVideo.CVPixelBufferGetBaseAddress
import platform.CoreVideo.CVPixelBufferGetBytesPerRow
import platform.CoreVideo.CVPixelBufferLockBaseAddress
import platform.CoreVideo.CVPixelBufferRefVar
import platform.CoreVideo.CVPixelBufferRelease
import platform.CoreVideo.CVPixelBufferUnlockBaseAddress
import platform.CoreVideo.kCVPixelFormatType_32BGRA
import platform.CoreVideo.kCVReturnSuccess
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.Foundation.NSThread
import platform.Foundation.dataWithContentsOfURL
import platform.posix.memcpy

/**
 * mp4 through the system H.264 encoder (VideoToolbox) on iOS and macOS. `AVAssetWriter` writes to a URL only, so the
 * video goes to a temporary file that is read back and deleted. Frames come from skiko as BGRA, row by row into
 * `CVPixelBuffer`s whose rows are padded (`bytesPerRow`). Baseline profile: plays everywhere, the frames are screens.
 *
 * The session ends at the recorded total (`endSessionAtSourceTime`), but the writer is free to give the last sample
 * any duration, so the last frame is appended once more one millisecond before the end: the picture then stays on
 * screen for its own delay, and the file has one sample more than frames (the spec names this exception). If VideoToolbox is unavailable (CI without
 * a GPU session) the writer fails and the caller falls back to APNG.
 */
internal class AvAssetWriterMp4Encoder : VideoEncoder {
    override suspend fun encode(frames: List<ApngFrame>, options: VideoOptions): EncodedVideo {
        require(frames.isNotEmpty()) { "no frames to encode" }
        val timeline = frameTimeline(frames)
        val first = padToEven(decodePng(frames.first().png))
        val width = first.width
        val height = first.height

        val path = NSTemporaryDirectory() + "ultron-video-" + NSUUID().UUIDString + ".mp4"
        val url = NSURL.fileURLWithPath(path)
        try {
            val writer = AVAssetWriter(uRL = url, fileType = AVFileTypeMPEG4!!, error = null)
            val settings: Map<Any?, Any?> = mapOf(
                AVVideoCodecKey to AVVideoCodecTypeH264,
                AVVideoWidthKey to width,
                AVVideoHeightKey to height,
                AVVideoCompressionPropertiesKey to mapOf<Any?, Any?>(
                    AVVideoAverageBitRateKey to bitRate(options.quality),
                    AVVideoProfileLevelKey to AVVideoProfileLevelH264BaselineAutoLevel,
                ),
            )
            check(writer.canApplyOutputSettings(settings, AVMediaTypeVideo)) { "the system encoder rejects H.264 ${width}x$height" }
            val input = AVAssetWriterInput(mediaType = AVMediaTypeVideo!!, outputSettings = settings)
            input.expectsMediaDataInRealTime = false
            val adaptor = AVAssetWriterInputPixelBufferAdaptor(assetWriterInput = input, sourcePixelBufferAttributes = null)
            writer.addInput(input)
            check(writer.startWriting()) { "AVAssetWriter did not start: ${writer.error?.localizedDescription}" }
            writer.startSessionAtSourceTime(CMTimeMake(0, TIMESCALE))

            var last: RawFrame? = null
            frames.forEachIndexed { index, frame ->
                val raw = fitTo(padToEven(decodePng(frame.png)), width, height)
                append(writer, input, adaptor, raw, timeline[index].ptsMs)
                last = raw
            }
            val total = timeline.last().ptsMs + timeline.last().durationMs
            val tail = timeline.last().ptsMs
            if (total - 1 > tail) append(writer, input, adaptor, checkNotNull(last), total - 1)

            input.markAsFinished()
            // The track ends at the recorded total: without this the writer decides how long the tail sample lasts.
            writer.endSessionAtSourceTime(CMTimeMake(total, TIMESCALE))
            var done = false
            writer.finishWritingWithCompletionHandler { done = true }
            while (!done && writer.status != AVAssetWriterStatusCompleted && writer.status != AVAssetWriterStatusFailed) {
                // The test thread has no run loop to deliver the completion on, so poll the status.
                NSThread.sleepForTimeInterval(0.01)
            }
            check(writer.status == AVAssetWriterStatusCompleted) { "AVAssetWriter failed: ${writer.error?.localizedDescription}" }

            val data = NSData.dataWithContentsOfURL(url) ?: error("cannot read $path back")
            return EncodedVideo(data.toByteArray(), "mp4", "video/mp4")
        } finally {
            NSFileManager.defaultManager.removeItemAtPath(path, null)
        }
    }

    private fun append(writer: AVAssetWriter, input: AVAssetWriterInput, adaptor: AVAssetWriterInputPixelBufferAdaptor, frame: RawFrame, ptsMs: Long) {
        while (!input.readyForMoreMediaData) {
            check(writer.status != AVAssetWriterStatusFailed) { "AVAssetWriter failed: ${writer.error?.localizedDescription}" }
            NSThread.sleepForTimeInterval(0.005)
        }
        memScoped {
            val buffer = alloc<CVPixelBufferRefVar>()
            check(CVPixelBufferCreate(null, frame.width.toULong(), frame.height.toULong(), kCVPixelFormatType_32BGRA, null, buffer.ptr) == kCVReturnSuccess) {
                "cannot allocate a pixel buffer ${frame.width}x${frame.height}"
            }
            val pixelBuffer = buffer.value
            try {
                CVPixelBufferLockBaseAddress(pixelBuffer, 0u)
                val base = CVPixelBufferGetBaseAddress(pixelBuffer)!!.reinterpret<UByteVar>()
                val stride = CVPixelBufferGetBytesPerRow(pixelBuffer).toInt()
                val rowBytes = frame.width * 4
                frame.pixels.usePinned { pinned ->
                    for (y in 0 until frame.height) {
                        memcpy(base + y * stride, pinned.addressOf(y * rowBytes), rowBytes.toULong())
                    }
                }
                CVPixelBufferUnlockBaseAddress(pixelBuffer, 0u)
                check(adaptor.appendPixelBuffer(pixelBuffer, withPresentationTime = CMTimeMake(ptsMs, TIMESCALE))) {
                    "cannot append a frame: ${writer.error?.localizedDescription}"
                }
            } finally {
                CVPixelBufferRelease(pixelBuffer)
            }
        }
    }

    private fun bitRate(quality: VideoQuality): Int = when (quality) {
        VideoQuality.LOW -> 500_000
        VideoQuality.MEDIUM -> 1_000_000
        VideoQuality.HIGH -> 2_000_000
    }

    private fun NSData.toByteArray(): ByteArray = ByteArray(length.toInt()).also { out ->
        if (out.isNotEmpty()) out.usePinned { memcpy(it.addressOf(0), bytes, length) }
    }

    private companion object {
        const val TIMESCALE = 1000
    }
}
