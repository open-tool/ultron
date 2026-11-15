package com.atiurin.ultron.allure.exit

import com.atiurin.ultron.allure.AllureTestRunHook
import com.atiurin.ultron.allure.UltronAllure
import com.atiurin.ultron.allure.attach.ArtifactProvider
import com.atiurin.ultron.allure.attach.TestFailureAttacher
import com.atiurin.ultron.allure.config.AllureAttachStrategy
import com.atiurin.ultron.allure.log.AllureLogAttacher
import com.atiurin.ultron.allure.log.AllureLogBuffer
import com.atiurin.ultron.allure.model.AllureStatus
import com.atiurin.ultron.allure.testutil.Harness
import com.atiurin.ultron.allure.video.ApngEncoder
import com.atiurin.ultron.allure.video.FrameRecorder
import com.atiurin.ultron.allure.video.VideoAttacher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The exit path is best effort: an attachment that throws never costs the result of the test open at exit. */
class CloseAtExitSafetyTest {
    private val h = Harness()
    private val buffer = AllureLogBuffer()

    private val provider = object : ArtifactProvider {
        override val kind = "screenshot"
        override val extension = "png"
        override val mimeType = "image/png"
        override fun produce(): ByteArray = byteArrayOf(1, 2, 3)
    }

    /** A recorder whose `frames()` throws: its minimum frame duration is above the maximum. */
    private fun throwingVideo() = VideoAttacher(
        h.lifecycle,
        FrameRecorder(FrameRecorder.Limits(maxFrameDurationMs = 100, minFrameDurationMs = 500)) { h.clock.now },
        provider,
        ApngEncoder,
        setOf(AllureAttachStrategy.TEST_FINISH),
    )

    private fun hook(video: VideoAttacher) = AllureTestRunHook(
        h.lifecycle,
        TestFailureAttacher(h.lifecycle, emptyList(), emptySet(), emptySet()),
        AllureLogAttacher(h.lifecycle, buffer),
        video,
    )

    @Test
    fun `the test open at exit is written even when its video throws`() {
        val video = throwingVideo()
        val hook = hook(video)
        val savedLifecycle = UltronAllure.lifecycle
        val savedHook = UltronAllure.testRunHook
        try {
            UltronAllure.lifecycle = h.lifecycle
            UltronAllure.testRunHook = hook
            hook.beforeTest("hung at exit")
            video.captureFrame("frame")
            buffer.info("inside the hung test")

            UltronAllure.closeAtExit()
        } finally {
            UltronAllure.lifecycle = savedLifecycle
            UltronAllure.testRunHook = savedHook
        }

        val result = h.single()
        assertEquals("hung at exit", result.name)
        assertEquals(AllureStatus.BROKEN, result.status)
        assertTrue("ultron_log.txt" in result.attachments.map { it.name }, "the log is attached although the video threw")
    }

    @Test
    fun `a test left open is closed when the next one starts even when its video throws`() {
        val video = throwingVideo()
        val hook = hook(video)
        hook.beforeTest("stale")
        video.captureFrame("frame")

        hook.beforeTest("next")
        hook.afterTest(null)

        assertEquals(listOf("stale" to AllureStatus.BROKEN, "next" to AllureStatus.PASSED), h.results().map { it.name to it.status })
    }
}
