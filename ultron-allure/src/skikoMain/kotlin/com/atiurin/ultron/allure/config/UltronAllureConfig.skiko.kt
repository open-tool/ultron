package com.atiurin.ultron.allure.config

import com.atiurin.ultron.allure.AllureTestRunHook
import com.atiurin.ultron.allure.UltronAllure
import com.atiurin.ultron.allure.attach.ComposeHierarchyProvider
import com.atiurin.ultron.allure.attach.ComposeScreenshotProvider
import com.atiurin.ultron.allure.attach.TestFailureAttacher
import com.atiurin.ultron.allure.listeners.AllureOperationStepListener
import com.atiurin.ultron.allure.listeners.AttachOnOperationListener
import com.atiurin.ultron.allure.listeners.RecordFrameListener
import com.atiurin.ultron.allure.log.AllureLogAttacher
import com.atiurin.ultron.allure.log.AllureLogBuffer
import com.atiurin.ultron.allure.test.AllureUltronTestListener
import com.atiurin.ultron.allure.video.FrameRecorder
import com.atiurin.ultron.allure.video.VideoAttacher
import com.atiurin.ultron.allure.video.VideoFormat
import com.atiurin.ultron.allure.video.VideoQuality
import com.atiurin.ultron.allure.video.platformVideoEncoder
import com.atiurin.ultron.core.compose.UltronTestRunHooks
import com.atiurin.ultron.core.config.UltronCommonConfig
import com.atiurin.ultron.core.test.UltronTestListeners
import com.atiurin.ultron.log.UltronLog

/** Parameters on the Skiko targets (desktop, iOS, macOS, js, wasmJs): Ultron's own Allure 2 writer. */
actual class AllureConfigParams actual constructor() {
    /** `null` → `ULTRON_ALLURE_RESULTS_DIR` → `build/allure-results`. Ignored on js/wasmJs (in-memory results). */
    var resultsDirectory: String? = null
    actual var addScreenshotPolicy: MutableSet<AllureAttachStrategy> = mutableSetOf(
        AllureAttachStrategy.TEST_FAILURE, AllureAttachStrategy.OPERATION_FAILURE
    )
    actual var addHierarchyPolicy: MutableSet<AllureAttachStrategy> = mutableSetOf(
        AllureAttachStrategy.TEST_FAILURE, AllureAttachStrategy.OPERATION_FAILURE
    )
    actual var attachUltronLog: Boolean = true
    actual var detailedAllureReport: Boolean = true
    /**
     * Frame-by-frame recording of the test: one screenshot after every operation plus the failure frame,
     * attached as an animated PNG named `video`. `TEST_FAILURE` records every test but attaches only
     * failed ones; `TEST_FINISH` attaches all. Recording costs one capture + PNG encode per operation and,
     * while an operation waits, one per `videoLimits.retryFrameIntervalMs`.
     */
    var videoPolicy: MutableSet<AllureAttachStrategy> = mutableSetOf(AllureAttachStrategy.NONE)
    var videoLimits: FrameRecorder.Limits = FrameRecorder.Limits()

    /**
     * Format of the `video` attachment. [VideoFormat.AUTO] is mp4 (H.264) where the target has an encoder and APNG
     * elsewhere; [VideoFormat.MP4] behaves the same and also falls back to APNG when the encoder is missing or
     * fails, since a report never changes the outcome of a test. To get the former animated PNG back, set
     * [VideoFormat.APNG].
     */
    var videoFormat: VideoFormat = VideoFormat.AUTO

    /** Bit rate / quantizer of the mp4 encoder. The JVM encoder may ignore it, see the docs. */
    var videoQuality: VideoQuality = VideoQuality.MEDIUM

    override fun toString(): String =
        "AllureConfigParams(resultsDirectory=$resultsDirectory, addScreenshotPolicy=$addScreenshotPolicy, " +
            "addHierarchyPolicy=$addHierarchyPolicy, attachUltronLog=$attachUltronLog, detailedAllureReport=$detailedAllureReport, " +
            "videoPolicy=$videoPolicy, videoLimits=$videoLimits, videoFormat=$videoFormat, videoQuality=$videoQuality)"
}

actual object UltronAllureConfig {
    actual var params: AllureConfigParams = AllureConfigParams()

    private val logBuffer = AllureLogBuffer { UltronAllure.lifecycle }

    /** The recorder of the current configuration, `null` when `videoPolicy` is NONE. */
    var videoAttacher: VideoAttacher? = null
        private set

    actual fun applyRecommended() {
        params = AllureConfigParams()
        modify()
    }

    actual fun apply(block: AllureConfigParams.() -> Unit) {
        params.block()
        modify()
    }

    private fun modify() {
        clearRegistrations()
        // The same lifecycle as before: an `apply` from `beforeFirstTest` inside `runUltronUiTest { test { } }`
        // runs while the hook of the previous configuration has the test open, and that test must stay current.
        val lifecycle = UltronAllure.redirect(params.resultsDirectory)

        val screenshot = ComposeScreenshotProvider()
        val hierarchy = ComposeHierarchyProvider()
        if (!params.addScreenshotPolicy.contains(AllureAttachStrategy.NONE)) {
            UltronCommonConfig.addListener(AttachOnOperationListener(lifecycle, screenshot, params.addScreenshotPolicy))
        }
        if (!params.addHierarchyPolicy.contains(AllureAttachStrategy.NONE)) {
            UltronCommonConfig.addListener(AttachOnOperationListener(lifecycle, hierarchy, params.addHierarchyPolicy))
        }
        val logAttacher = if (params.attachUltronLog) {
            UltronLog.addLogger(logBuffer)
            AllureLogAttacher(lifecycle, logBuffer)
        } else null
        // Video frames are flattened onto white: mp4 and webm have no alpha channel (see ComposeScreenshotProvider).
        val frames = ComposeScreenshotProvider.forVideo()
        val video = VideoAttacher(lifecycle, FrameRecorder(params.videoLimits), frames, platformVideoEncoder(), params.videoPolicy, params.videoFormat, params.videoQuality)
            .takeIf { it.enabled }
        videoAttacher = video
        UltronAllure.videoAttacher = video
        if (video != null) UltronCommonConfig.addListener(RecordFrameListener(video.recorder, frames, lifecycle))
        // Last on purpose: listeners run in registration order, so the step must still be open when the
        // attach listeners' after() runs (OPERATION_FINISH), and its before() has nothing to nest under anyway.
        if (params.detailedAllureReport) {
            UltronCommonConfig.addListener(AllureOperationStepListener(lifecycle))
        }
        val hook = AllureTestRunHook(
            lifecycle = lifecycle,
            failureAttacher = TestFailureAttacher(lifecycle, listOf(screenshot, hierarchy), params.addScreenshotPolicy, params.addHierarchyPolicy),
            logAttacher = logAttacher,
            videoAttacher = video,
        )
        UltronTestRunHooks.register(hook)
        UltronAllure.testRunHook = hook
        val listener = AllureUltronTestListener(
            lifecycle, hook::closeTest, logAttacher,
            failTest = hook::onTestFailed,
            reopenTest = hook::reopen,
            closeStaleTest = hook::closeStaleTest,
        )
        UltronTestListeners.register(listener)
        if (lifecycle.currentTest != null) {
            // Applied inside an open test: record it from here on, the previous recorder was dropped above.
            video?.onTestStarted()
        } else {
            // The first `apply` of the run, from `beforeFirstTest` inside `runUltronUiTest { test { } }`: no hook
            // was registered when the block started, so no test is open. Open it now, as the hook's `beforeTest`
            // would have at the start; `test { }` adopts it next and the hook closes it with its artifacts.
            UltronTestRunHooks.activeRun?.let(hook::beforeTest)
            // From the common `beforeTest` or from `go { }` instead: the listeners' `beforeTest` of the test in
            // progress fired before this listener existed. Give it the event now, so that it adopts the test the
            // hook just opened (or opens one of its own) with the class and the name of the `test { }`.
            UltronTestListeners.activeTest?.let(listener::beforeTest)
        }
        UltronLog.info("UltronAllureConfig applied with params $params")
    }

    private fun clearRegistrations() {
        videoAttacher?.recorder?.stop()
        videoAttacher = null
        UltronAllure.videoAttacher = null
        UltronAllure.testRunHook = null
        UltronCommonConfig.removeListener(AllureOperationStepListener::class)
        UltronCommonConfig.removeListener("${AttachOnOperationListener::class.simpleName}_screenshot")
        UltronCommonConfig.removeListener("${AttachOnOperationListener::class.simpleName}_hierarchy")
        UltronCommonConfig.removeListener(RecordFrameListener::class)
        UltronLog.removeLogger(logBuffer.id)
        UltronTestRunHooks.unregister("AllureTestRunHook")
        UltronTestListeners.unregister(AllureUltronTestListener.ID)
    }
}
