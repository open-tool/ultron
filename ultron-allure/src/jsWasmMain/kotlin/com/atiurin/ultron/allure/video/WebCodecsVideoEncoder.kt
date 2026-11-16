package com.atiurin.ultron.allure.video

/** One encoded chunk as WebCodecs delivered it. */
internal class BrowserChunk(val data: ByteArray, val ptsMs: Long, val keyFrame: Boolean)

/** [description] is the `avcC` record of an H.264 stream (`decoderConfig.description`), `null` for VP8/VP9. */
internal class BrowserEncoded(val description: ByteArray?, val chunks: List<BrowserChunk>)

/**
 * The part of WebCodecs the encoder needs, in terms Kotlin owns. The `js` and `wasmJs` dialects implement it over
 * their own interop; the choice of codec and container below is shared and tested against a fake.
 */
internal interface WebCodecsBridge {
    suspend fun isSupported(codec: String, width: Int, height: Int, bitrate: Int): Boolean

    /**
     * Decodes each PNG (`createImageBitmap`), pads it to [width] x [height] by repeating the last column and row,
     * wraps it into a `VideoFrame` with the timestamp [ptsMs] and duration [durationsMs] and encodes it; a key frame
     * every [keyEvery] frames. Throws when the codec reports an error.
     */
    suspend fun encode(
        codec: String, width: Int, height: Int, bitrate: Int,
        pngs: List<ByteArray>, ptsMs: List<Long>, durationsMs: List<Int>, keyEvery: Int,
    ): BrowserEncoded
}

/** `null` when the runtime has no WebCodecs `VideoEncoder` (Node, old browsers): the video stays APNG. */
internal expect fun webCodecsBridge(): WebCodecsBridge?

/**
 * mp4 (H.264) through the browser's WebCodecs, or webm (VP9, VP8) where the build has no H.264 (Chromium without
 * proprietary codecs). The codec is picked at run time by `isConfigSupported`; with none supported it throws and the
 * caller falls back to APNG. The containers are written by [Mp4Muxer] and [WebmMuxer].
 */
internal class WebCodecsVideoEncoder(private val bridge: WebCodecsBridge) : VideoEncoder {
    /** [webmCodecId] is `null` for the mp4 candidate. */
    private class Candidate(val codec: String, val webmCodecId: String?)

    override suspend fun encode(frames: List<ApngFrame>, options: VideoOptions): EncodedVideo {
        require(frames.isNotEmpty()) { "no frames to encode" }
        val ihdr = PngChunks.parse(frames.first().png).first { it.type == "IHDR" }.data
        val width = evenSize(PngChunks.readInt(ihdr, 0))
        val height = evenSize(PngChunks.readInt(ihdr, 4))
        val bitrate = when (options.quality) {
            VideoQuality.LOW -> 500_000
            VideoQuality.MEDIUM -> 1_000_000
            VideoQuality.HIGH -> 2_000_000
        }
        val timeline = frameTimeline(frames)
        for (candidate in CANDIDATES) {
            if (!bridge.isSupported(candidate.codec, width, height, bitrate)) continue
            val encoded = bridge.encode(
                candidate.codec, width, height, bitrate, frames.map { it.png },
                timeline.map { it.ptsMs }, timeline.map { it.durationMs }, KEY_FRAME_EVERY,
            )
            return if (candidate.webmCodecId == null) mp4(encoded, width, height, timeline)
            else webm(candidate.webmCodecId, encoded, width, height, timeline)
        }
        error("this browser has no H.264, VP9 or VP8 encoder for ${width}x$height")
    }

    private fun mp4(encoded: BrowserEncoded, width: Int, height: Int, timeline: List<FrameTime>): EncodedVideo {
        val avcC = encoded.description ?: error("the H.264 encoder gave no avcC description")
        check(encoded.chunks.size == timeline.size) { "${encoded.chunks.size} chunks for ${timeline.size} frames" }
        val samples = encoded.chunks.mapIndexed { i, chunk -> Mp4Sample(chunk.data, timeline[i].durationMs, chunk.keyFrame) }
        return EncodedVideo(Mp4Muxer.mux(avcC, width, height, samples), "mp4", "video/mp4")
    }

    private fun webm(codecId: String, encoded: BrowserEncoded, width: Int, height: Int, timeline: List<FrameTime>): EncodedVideo {
        val total = timeline.last().ptsMs + timeline.last().durationMs
        val chunks = encoded.chunks.map { WebmChunk(it.data, it.ptsMs, it.keyFrame) }
        return EncodedVideo(WebmMuxer.mux(codecId, width, height, total, chunks), "webm", "video/webm")
    }

    private companion object {
        /** A frame per operation: little change between frames, so frequent key frames cost little and make seeking cheap. */
        const val KEY_FRAME_EVERY = 10
        val CANDIDATES = listOf(
            Candidate("avc1.42001E", webmCodecId = null),
            Candidate("vp09.00.10.08", webmCodecId = "V_VP9"),
            Candidate("vp8", webmCodecId = "V_VP8"),
        )
    }
}
