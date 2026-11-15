package com.atiurin.ultron.allure.video

import com.atiurin.ultron.allure.attach.ArtifactProvider
import com.atiurin.ultron.allure.config.AllureAttachStrategy.TEST_FINISH
import com.atiurin.ultron.allure.log.AllureLogBuffer
import com.atiurin.ultron.allure.model.AllureStatus
import com.atiurin.ultron.allure.testutil.FailingVideoEncoder
import com.atiurin.ultron.allure.testutil.FakeMp4Encoder
import com.atiurin.ultron.allure.testutil.Harness
import com.atiurin.ultron.allure.testutil.SuspendingVideoEncoder
import com.atiurin.ultron.log.UltronLog
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VideoFormatSelectionTest {
    private val h = Harness()
    private val log = AllureLogBuffer().also { UltronLog.addLogger(it) }
    private var colour = 0xFF000000.toInt()

    private val provider = object : ArtifactProvider {
        override val kind = "screenshot"
        override val extension = "png"
        override val mimeType = "image/png"
        override fun produce(): ByteArray {
            val image = BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB)
            for (x in 0 until 2) for (y in 0 until 2) image.setRGB(x, y, colour)
            return ByteArrayOutputStream().also { ImageIO.write(image, "png", it) }.toByteArray()
        }
    }

    @AfterTest
    fun removeLogger() = UltronLog.removeLogger(log.id)

    private fun attacher(encoder: VideoEncoder?, format: VideoFormat = VideoFormat.AUTO) = VideoAttacher(
        h.lifecycle, FrameRecorder(FrameRecorder.Limits(100, 1_000_000, 3000, 1000)) { h.clock.now },
        provider, encoder, setOf(TEST_FINISH), format,
    )

    /** Records two different frames and finishes the test; returns the expected APNG of them. */
    private fun record(video: VideoAttacher, finish: Boolean = true): ByteArray {
        h.lifecycle.startTest("t")
        video.onTestStarted()
        video.captureFrame("a")
        h.clock.tick(600)
        colour = 0xFFFF0000.toInt()
        video.captureFrame("b")
        val expected = ApngEncoder.encode(
            listOf(ApngFrame(frameBytes(0xFF000000.toInt()), 600), ApngFrame(frameBytes(0xFFFF0000.toInt()), 1000))
        )
        video.onTestFinished(null)
        if (finish) h.lifecycle.stopTest(null)
        return expected
    }

    private fun frameBytes(argb: Int): ByteArray {
        val old = colour
        colour = argb
        return provider.produce().also { colour = old }
    }

    private fun attachmentBytes(): Pair<String, ByteArray> {
        val attachment = h.single().attachments.single()
        return attachment.type to h.sink.snapshot().getValue(attachment.source)
    }

    @Test
    fun `AUTO and MP4 use the platform encoder and attach its type`() {
        for (format in listOf(VideoFormat.AUTO, VideoFormat.MP4)) {
            val h2 = Harness()
            val encoder = FakeMp4Encoder()
            val video = VideoAttacher(
                h2.lifecycle, FrameRecorder { h2.clock.now }, provider, encoder, setOf(TEST_FINISH), format,
            )
            h2.lifecycle.startTest("t"); video.onTestStarted(); video.captureFrame("a"); video.onTestFinished(null); h2.lifecycle.stopTest(null)
            val attachment = h2.single().attachments.single()
            assertEquals("video/mp4", attachment.type, "format $format")
            assertEquals("video", attachment.name)
            assertContentEquals(encoder.bytes, h2.sink.snapshot().getValue(attachment.source))
            assertEquals(1, encoder.calls)
        }
    }

    @Test
    fun `no platform encoder gives a valid APNG`() {
        record(attacher(null))

        val (type, bytes) = attachmentBytes()
        assertEquals("image/png", type)
        assertEquals(2, ApngInfo.frameCount(bytes))
        assertTrue(log.drain().contains("not available"), "the log says why the video is APNG")
    }

    @Test
    fun `a failing encoder falls back to APNG with a warning and leaves the status alone`() {
        val encoder = FailingVideoEncoder()
        record(attacher(encoder))

        val result = h.single()
        assertEquals(AllureStatus.PASSED, result.status)
        val (type, bytes) = attachmentBytes()
        assertEquals("image/png", type)
        assertEquals(2, ApngInfo.frameCount(bytes))
        assertEquals(1, encoder.calls)
        val lines = log.drain()
        assertTrue(lines.lines().any { it.startsWith("W ") && it.contains("codec exploded") }, lines)
    }

    @Test
    fun `APNG never calls the platform encoder and equals the ApngEncoder output`() {
        val encoder = FakeMp4Encoder()
        val expected = record(attacher(encoder, VideoFormat.APNG))

        val (type, bytes) = attachmentBytes()
        assertEquals("image/png", type)
        assertEquals(0, encoder.calls)
        assertContentEquals(expected, bytes)
    }

    @Test
    fun `a suspended encoder defers the result until it resumes and writes it once`() {
        val encoder = SuspendingVideoEncoder()
        record(attacher(encoder))

        assertTrue(encoder.suspended)
        assertTrue(h.results().isEmpty(), "the result waits for the video")

        encoder.resume()

        val (type, bytes) = attachmentBytes()
        assertEquals("video/mp4", type)
        assertContentEquals(encoder.bytes, bytes)
        assertEquals(1, h.results().size)
    }

    @Suppress("DEPRECATION")
    @Test
    fun `an own AnimationEncoder plugs in through asVideoEncoder`() {
        val own = object : AnimationEncoder {
            override val extension = "gif"
            override val mimeType = "image/gif"
            override fun encode(frames: List<ApngFrame>): ByteArray = byteArrayOf(frames.size.toByte())
        }
        record(attacher(own.asVideoEncoder()))

        val (type, bytes) = attachmentBytes()
        assertEquals("image/gif", type)
        assertContentEquals(byteArrayOf(2), bytes)
        assertTrue(ApngEncoder.asVideoEncoder() === ApngEncoder, "ApngEncoder is a VideoEncoder already")
    }

    @Test
    fun `a suspended encoder that fails resumes into APNG`() {
        val encoder = SuspendingVideoEncoder()
        record(attacher(encoder))

        encoder.fail(IllegalStateException("no codec"))

        val (type, bytes) = attachmentBytes()
        assertEquals("image/png", type)
        assertEquals(2, ApngInfo.frameCount(bytes))
    }
}
