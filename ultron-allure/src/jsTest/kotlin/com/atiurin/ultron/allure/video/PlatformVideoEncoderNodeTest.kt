package com.atiurin.ultron.allure.video

import com.atiurin.ultron.allure.attach.ArtifactProvider
import com.atiurin.ultron.allure.config.AllureAttachStrategy
import com.atiurin.ultron.allure.lifecycle.AllureLifecycle
import com.atiurin.ultron.allure.model.AllureJson
import com.atiurin.ultron.allure.model.AllureTestResult
import com.atiurin.ultron.allure.writer.AllureResultsWriter
import com.atiurin.ultron.allure.writer.InMemoryResultsSink
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** The browser encoder under Node: no WebCodecs there, so the video is APNG; its codec choice runs on a fake bridge. */
class PlatformVideoEncoderNodeTest {
    /** A PNG that is well formed chunk by chunk; the assembler moves its IDAT as it is and never inflates it. */
    private fun png(width: Int, height: Int, seed: Int): ByteArray {
        val ihdr = ByteArray(13)
        fun put(at: Int, v: Int) { ihdr[at] = (v shr 24).toByte(); ihdr[at + 1] = (v shr 16).toByte(); ihdr[at + 2] = (v shr 8).toByte(); ihdr[at + 3] = v.toByte() }
        put(0, width); put(4, height); ihdr[8] = 8; ihdr[9] = 6
        val chunks = listOf(PngChunks.chunk("IHDR", ihdr), PngChunks.chunk("IDAT", byteArrayOf(seed.toByte(), 1, 2)), PngChunks.chunk("IEND", ByteArray(0)))
        return (listOf(PngChunks.SIGNATURE) + chunks).fold(ByteArray(0)) { acc, part -> acc + part }
    }

    private fun <T> runSuspend(block: suspend () -> T): T {
        var result: Result<T>? = null
        block.startCoroutine(Continuation(EmptyCoroutineContext) { result = it })
        return checkNotNull(result) { "the coroutine suspended" }.getOrThrow()
    }

    private class FakeBridge(private val supported: Set<String>) : WebCodecsBridge {
        val tried = mutableListOf<String>()
        var encodedWith: String? = null
        override suspend fun isSupported(codec: String, width: Int, height: Int, bitrate: Int): Boolean {
            tried += codec
            return codec in supported
        }

        override suspend fun encode(
            codec: String, width: Int, height: Int, bitrate: Int,
            pngs: List<ByteArray>, ptsMs: List<Long>, durationsMs: List<Int>, keyEvery: Int,
        ): BrowserEncoded {
            encodedWith = codec
            val avcC = if (codec.startsWith("avc1")) byteArrayOf(1, 0x42, 0, 0x1E) else null
            return BrowserEncoded(avcC, pngs.indices.map { BrowserChunk(byteArrayOf(it.toByte(), 5, 5), ptsMs[it], it == 0) })
        }
    }

    private val frames = listOf(ApngFrame(png(3, 3, 1), 500), ApngFrame(png(3, 3, 2), 900))

    @Test
    fun withoutWebCodecsThereIsNoPlatformEncoder() {
        assertNull(platformVideoEncoder())
    }

    @Test
    fun theVideoOfATestIsApngUnderNode() {
        val sink = InMemoryResultsSink()
        val lifecycle = AllureLifecycle(AllureResultsWriter(sink))
        var shot = 0
        val provider = object : ArtifactProvider {
            override val kind = "screenshot"
            override val extension = "png"
            override val mimeType = "image/png"
            override fun produce(): ByteArray = png(3, 3, shot++)
        }
        val video = VideoAttacher(lifecycle, FrameRecorder(), provider, platformVideoEncoder(), setOf(AllureAttachStrategy.TEST_FINISH))

        lifecycle.startTest("t")
        video.onTestStarted()
        video.captureFrame("a")
        video.captureFrame("b")
        video.onTestFinished(null)
        lifecycle.stopTest(null)

        val files = sink.snapshot()
        val result = AllureJson.format.decodeFromString(
            AllureTestResult.serializer(), files.entries.first { it.key.endsWith("-result.json") }.value.decodeToString(),
        )
        val attachment = result.attachments.single()
        assertEquals("image/png", attachment.type)
        assertEquals(2, ApngInfo.frameCount(files.getValue(attachment.source)))
    }

    @Test
    fun h264IsPreferredAndPaddedToEvenSize() {
        val bridge = FakeBridge(setOf("avc1.42001E", "vp8"))
        val video = runSuspend { WebCodecsVideoEncoder(bridge).encode(frames, VideoOptions(VideoQuality.MEDIUM)) }

        assertEquals("video/mp4", video.mimeType)
        assertEquals("avc1.42001E", bridge.encodedWith)
        val info = assertNotNull(Mp4Info.parse(video.bytes))
        assertEquals(4 to 4, info.width to info.height)
        assertEquals(listOf(500, 900), info.sampleDurations)
        assertEquals(listOf(1), info.syncSamples)
    }

    @Test
    fun withoutH264TheEncoderFallsBackToVp9InWebm() {
        val bridge = FakeBridge(setOf("vp09.00.10.08", "vp8"))
        val video = runSuspend { WebCodecsVideoEncoder(bridge).encode(frames, VideoOptions(VideoQuality.LOW)) }

        assertEquals("video/webm", video.mimeType)
        assertEquals("webm", video.extension)
        assertEquals(listOf("avc1.42001E", "vp09.00.10.08"), bridge.tried)
        assertEquals("vp09.00.10.08", bridge.encodedWith)
        assertTrue(video.bytes.decodeToString().contains("V_VP9"))
    }

    @Test
    fun noSupportedCodecFailsSoThatTheCallerFallsBackToApng() {
        val bridge = FakeBridge(emptySet())
        assertFailsWith<IllegalStateException> {
            runSuspend { WebCodecsVideoEncoder(bridge).encode(frames, VideoOptions(VideoQuality.HIGH)) }
        }
        assertEquals(listOf("avc1.42001E", "vp09.00.10.08", "vp8"), bridge.tried)
    }
}
