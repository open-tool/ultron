package com.atiurin.ultron.allure.video

import com.atiurin.ultron.allure.AllureTestRunHook
import com.atiurin.ultron.allure.attach.ArtifactProvider
import com.atiurin.ultron.allure.attach.TestFailureAttacher
import com.atiurin.ultron.allure.config.AllureAttachStrategy
import com.atiurin.ultron.allure.config.AllureAttachStrategy.TEST_FAILURE
import com.atiurin.ultron.allure.config.AllureAttachStrategy.TEST_FINISH
import com.atiurin.ultron.allure.lifecycle.AllureLifecycle
import com.atiurin.ultron.allure.listeners.RecordFrameListener
import com.atiurin.ultron.allure.model.AllureStatus
import com.atiurin.ultron.allure.test.AllureUltronTestListener
import com.atiurin.ultron.allure.testutil.FakeOperation
import com.atiurin.ultron.allure.testutil.FakeResult
import com.atiurin.ultron.allure.testutil.Harness
import com.atiurin.ultron.core.common.AbstractOperationLifecycle
import com.atiurin.ultron.core.common.UltronOperationType
import com.atiurin.ultron.core.config.UltronCommonConfig
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private enum class ExcludedType : UltronOperationType { LOOKUP }

class VideoRecordingTest {
    private val h = Harness()
    private var colour = 0xFF000000.toInt()
    private var fail = false
    private var produced = 0
    private val provider = object : ArtifactProvider {
        override val kind = "screenshot"
        override val extension = "png"
        override val mimeType = "image/png"
        override fun produce(): ByteArray? {
            produced++
            if (fail) throw IllegalStateException("capture exploded")
            val image = BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB).also { img -> repeat(2) { x -> repeat(2) { y -> img.setRGB(x, y, colour) } } }
            return ByteArrayOutputStream().also { ImageIO.write(image, "png", it) }.toByteArray()
        }
    }

    private fun recorder() = FrameRecorder(FrameRecorder.Limits(100, 1_000_000, 3000, 1000)) { h.clock.now }

    private fun video(policy: Set<AllureAttachStrategy>, recorder: FrameRecorder = recorder()) =
        VideoAttacher(h.lifecycle, recorder, provider, ApngEncoder, policy)

    private fun hook(video: VideoAttacher) = AllureTestRunHook(
        lifecycle = h.lifecycle,
        failureAttacher = TestFailureAttacher(h.lifecycle, emptyList(), emptySet(), emptySet()),
        logAttacher = null,
        videoAttacher = video,
    )

    private fun operation(listener: RecordFrameListener, name: String, throwable: Throwable? = null) {
        val op = FakeOperation(name)
        val r = FakeResult(op, throwable)
        h.lifecycle.startStep(name)
        if (throwable == null) listener.afterSuccess(r) else listener.afterFailure(r)
        listener.after(r)
        h.lifecycle.stopStep()
        h.clock.tick(50)
    }

    @Test
    fun `TEST_FINISH attaches an APNG at test level with one frame per distinct operation screen`() {
        val recorder = recorder()
        val hook = hook(video(setOf(TEST_FINISH), recorder))
        val listener = RecordFrameListener(recorder, provider)

        hook.beforeTest("t")
        operation(listener, "click")
        colour = 0xFFFF0000.toInt()
        operation(listener, "type")
        operation(listener, "assert")
        hook.afterTest(null)

        val result = h.single()
        assertEquals(listOf("video"), result.attachments.map { it.name })
        assertEquals("image/png", result.attachments.single().type)
        assertTrue(result.steps.all { it.attachments.isEmpty() }, "video is never attached to a step")

        val apng = h.sink.snapshot().getValue(result.attachments.single().source)
        val chunks = PngChunks.parse(apng)
        assertEquals(2, PngChunks.readInt(chunks.single { it.type == "acTL" }.data, 0), "two distinct screens → two frames")
    }

    @Test
    fun `the video starts with the screen before the first operation, later operations add no frame before them`() {
        val recorder = recorder()
        val hook = hook(video(setOf(TEST_FINISH), recorder))
        val listener = RecordFrameListener(recorder, provider)

        hook.beforeTest("t")
        listener.before(FakeOperation("click")) // the screen as the test found it
        colour = 0xFFFF0000.toInt() // the click changes the screen
        operation(listener, "click")
        val producedBeforeSecond = produced
        listener.before(FakeOperation("assert")) // already recording frames: no extra capture
        assertEquals(producedBeforeSecond, produced, "only the first operation gets a frame before it")
        operation(listener, "assert")
        hook.afterTest(null)

        val apng = h.sink.snapshot().getValue(h.single().attachments.single().source)
        assertEquals(2, PngChunks.readInt(PngChunks.parse(apng).single { it.type == "acTL" }.data, 0), "screen before the click + after it")
    }

    @Test
    fun `TEST_FAILURE records every test but attaches only failed ones, adding a final frame`() {
        val recorder = recorder()
        val attacher = video(setOf(TEST_FAILURE), recorder)
        val hook = hook(attacher)
        val listener = RecordFrameListener(recorder, provider)

        hook.beforeTest("passing")
        operation(listener, "click")
        hook.afterTest(null)
        assertTrue(h.results().single().attachments.isEmpty(), "passing test has no video")

        hook.beforeTest("failing")
        operation(listener, "click")
        colour = 0xFF00FF00.toInt()
        val boom = AssertionError("boom")
        hook.onTestFailed(boom)
        hook.afterTest(boom)

        val failing = h.results().single { it.name == "failing" }
        assertEquals(listOf("video"), failing.attachments.map { it.name })
        val apng = h.sink.snapshot().getValue(failing.attachments.single().source)
        assertEquals(2, PngChunks.readInt(PngChunks.parse(apng).single { it.type == "acTL" }.data, 0), "operation frame + failure frame")
    }

    @Test
    fun `several runUltronUiTest blocks in one UltronTest make one video with the frames of every block`() {
        val recorder = recorder()
        val hook = hook(video(setOf(TEST_FINISH), recorder))
        val ultronTest = AllureUltronTestListener(h.lifecycle, hook::closeTest)
        val listener = RecordFrameListener(recorder, provider)

        ultronTest.beforeTest("MyTest", "two blocks")
        hook.beforeTest(null)
        operation(listener, "click")
        hook.afterTest(null)
        colour = 0xFFFF0000.toInt()
        hook.beforeTest(null)
        operation(listener, "type")
        hook.afterTest(null)
        ultronTest.afterTest("MyTest", null)

        val result = h.single()
        assertEquals(listOf("video"), result.attachments.map { it.name })
        val apng = h.sink.snapshot().getValue(result.attachments.single().source)
        assertEquals(2, PngChunks.readInt(PngChunks.parse(apng).single { it.type == "acTL" }.data, 0), "one frame from each block")
    }

    @Test
    fun `an UltronTest without a runUltronUiTest block gets no video, not the frames of the previous test`() {
        val recorder = recorder()
        val hook = hook(video(setOf(TEST_FINISH), recorder))
        val ultronTest = AllureUltronTestListener(h.lifecycle, hook::closeTest)
        val listener = RecordFrameListener(recorder, provider)

        ultronTest.beforeTest("MyTest", "with compose")
        hook.beforeTest(null)
        operation(listener, "click")
        hook.afterTest(null)
        ultronTest.afterTest("MyTest", null)

        ultronTest.beforeTest("MyTest", "plain")
        ultronTest.afterTest("MyTest", null)

        assertEquals(listOf("video"), h.results().single { it.name == "with compose" }.attachments.map { it.name })
        assertTrue(h.results().single { it.name == "plain" }.attachments.isEmpty(), "no recording for this test → no video")
        assertEquals(0, recorder.size, "frames are released once attached")
    }

    @Test
    fun `a test closed as broken by the next start keeps its own video and the next test starts afresh`() {
        val recorder = recorder()
        val hook = hook(video(setOf(TEST_FAILURE), recorder))
        val listener = RecordFrameListener(recorder, provider)

        hook.beforeTest("hung")
        operation(listener, "click")
        colour = 0xFFFF0000.toInt()
        hook.beforeTest("next") // "hung" never reached afterTest: the lifecycle closes it as broken
        operation(listener, "type")
        colour = 0xFF00FF00.toInt()
        val boom = AssertionError("boom")
        hook.onTestFailed(boom)
        hook.afterTest(boom)

        val hung = h.results().single { it.name == "hung" }
        assertEquals(listOf("video"), hung.attachments.map { it.name }, "the broken test keeps the frames it recorded")
        assertEquals(1, frameCount(hung), "only the frame of its own operation")
        val next = h.results().single { it.name == "next" }
        assertEquals(listOf("video"), next.attachments.map { it.name })
        assertEquals(2, frameCount(next), "operation frame + failure frame, nothing from 'hung'")
    }

    /** The implicit `unknown test` of an unnamed `runUltronUiTest` on a target without discovery, displaced by the next test. */
    @Test
    fun `an implicit test opened by the hook and displaced by the next start is broken and keeps its video`() {
        val recorder = recorder()
        val hook = hook(video(setOf(TEST_FAILURE), recorder))
        val listener = RecordFrameListener(recorder, provider)

        thread { hook.beforeTest(null) }.join() // no @Test method on that stack: nothing is discovered
        assertTrue(h.lifecycle.currentTest!!.implicit && h.lifecycle.currentTest!!.openedByHook, "precondition")
        operation(listener, "click")
        hook.beforeTest("next")
        hook.afterTest(null)

        val hung = h.results().single { it.name == AllureLifecycle.IMPLICIT_TEST_NAME }
        assertEquals(AllureStatus.BROKEN, hung.status)
        assertEquals(listOf("video"), hung.attachments.map { it.name }, "the broken test keeps the frames it recorded")
        assertTrue(h.results().single { it.name == "next" }.attachments.isEmpty(), "'next' passed: no video under TEST_FAILURE")
    }

    private fun frameCount(result: com.atiurin.ultron.allure.model.AllureTestResult): Int {
        val apng = h.sink.snapshot().getValue(result.attachments.single { it.name == "video" }.source)
        return PngChunks.readInt(PngChunks.parse(apng).single { it.type == "acTL" }.data, 0)
    }

    @Test
    fun `NONE records nothing at all`() {
        val recorder = recorder()
        val hook = hook(video(setOf(AllureAttachStrategy.NONE, TEST_FINISH), recorder))
        val listener = RecordFrameListener(recorder, provider)
        hook.beforeTest("t")
        operation(listener, "click")
        hook.afterTest(null)
        assertTrue(h.single().attachments.isEmpty())
    }

    @Test
    fun `a failing capture never breaks the operation or the test`() {
        val recorder = recorder()
        val hook = hook(video(setOf(TEST_FINISH), recorder))
        val listener = RecordFrameListener(recorder, provider)
        fail = true
        hook.beforeTest("t")
        operation(listener, "click")
        hook.afterTest(null)
        val result = h.single()
        assertTrue(result.attachments.isEmpty(), "no frames → no video")
        assertEquals(1, result.steps.size)
    }

    @Test
    fun `video is attached to the test even when a step is still open`() {
        val recorder = recorder()
        val hook = hook(video(setOf(TEST_FINISH), recorder))
        val listener = RecordFrameListener(recorder, provider)
        hook.beforeTest("t")
        operation(listener, "click")
        h.lifecycle.startStep("left open")
        hook.afterTest(null)
        val result = h.single()
        assertEquals(listOf("video"), result.attachments.map { it.name })
        assertTrue(result.steps.single { it.name == "left open" }.attachments.isEmpty())
    }

    @Test
    fun `a frame is captured between retries once the interval has passed`() {
        val recorder = FrameRecorder(FrameRecorder.Limits(100, 1_000_000, 3000, 1000, 0, retryFrameIntervalMs = 500)) { h.clock.now }
        recorder.start()
        val listener = RecordFrameListener(recorder, provider)
        val op = FakeOperation("wait for text")
        listener.betweenRetries(op)              // first poll: nothing captured yet → frame
        colour = 0xFF0000FF.toInt()
        h.clock.tick(100)
        listener.betweenRetries(op)              // too early
        h.clock.tick(400)
        listener.betweenRetries(op)              // 500 ms since the first frame → frame
        assertEquals(2, produced)
        assertEquals(listOf("wait for text", "wait for text"), recorder.frames().map { it.label })
    }

    @Test
    fun `retry frames respect the exclusion list and the recording state`() {
        val recorder = FrameRecorder(FrameRecorder.Limits(100, 1_000_000, 3000, 1000, 0, retryFrameIntervalMs = 500)) { h.clock.now }
        val listener = RecordFrameListener(recorder, provider)
        listener.betweenRetries(FakeOperation("not recording"))
        recorder.start()
        UltronCommonConfig.operationsExcludedFromListeners += ExcludedType.LOOKUP
        try {
            listener.betweenRetries(FakeOperation("get item", ExcludedType.LOOKUP))
        } finally {
            UltronCommonConfig.operationsExcludedFromListeners -= ExcludedType.LOOKUP
        }
        assertEquals(0, produced)
    }

    @Test
    fun `lifecycle forwards the between-retries hook to its listeners and to the common ones`() {
        val recorder = FrameRecorder(FrameRecorder.Limits(100, 1_000_000, 3000, 1000, 0, retryFrameIntervalMs = 1)) { h.clock.now }
        recorder.start()
        val lifecycle = object : AbstractOperationLifecycle() {}
        val listener = RecordFrameListener(recorder, provider)
        UltronCommonConfig.addListener(listener)
        try {
            lifecycle.notifyBetweenRetries(FakeOperation("wait"))
        } finally {
            UltronCommonConfig.removeListener(RecordFrameListener::class)
        }
        assertEquals(1, produced)
    }

    @Test
    fun `captureFrame adds a labelled frame only while recording`() {
        val recorder = recorder()
        val attacher = video(setOf(TEST_FINISH), recorder)
        attacher.captureFrame("before recording")
        attacher.onTestStarted() // no test open: nothing to record for
        attacher.captureFrame("still no test")
        assertEquals(0, produced)
        h.lifecycle.startTest("t")
        attacher.onTestStarted()
        attacher.captureFrame("animation settled")
        assertEquals(listOf("animation settled"), recorder.frames().map { it.label })
    }

    @Test
    fun `listener ignores operations excluded from listeners`() {
        val recorder = recorder().also { it.start() }
        val listener = RecordFrameListener(recorder, provider)
        val excluded = FakeOperation("get item", ExcludedType.LOOKUP)
        UltronCommonConfig.operationsExcludedFromListeners += ExcludedType.LOOKUP
        try {
            listener.after(FakeResult(excluded))
        } finally {
            UltronCommonConfig.operationsExcludedFromListeners -= ExcludedType.LOOKUP
        }
        assertTrue(recorder.frames().isEmpty())
        assertEquals(0, produced)
    }
}
