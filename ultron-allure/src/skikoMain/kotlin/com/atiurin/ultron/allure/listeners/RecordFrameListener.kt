package com.atiurin.ultron.allure.listeners

import com.atiurin.ultron.allure.attach.ArtifactProvider
import com.atiurin.ultron.allure.lifecycle.AllureLifecycle
import com.atiurin.ultron.allure.video.FrameRecorder
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.core.config.UltronCommonConfig
import com.atiurin.ultron.listeners.UltronLifecycleListener
import com.atiurin.ultron.log.UltronLog

/**
 * Adds one frame to the [FrameRecorder] after every operation and, while an operation retries, one frame
 * per [FrameRecorder.Limits.retryFrameIntervalMs]. Before the first operation of a recording it also captures the
 * screen as the test found it, so the video starts before the first click rather than after it. Never throws: a failed capture must not fail the
 * operation it observes. With a [lifecycle], an operation of a `runUltronUiTest` block whose test is closed already
 * (it finishes late, see `AllureLifecycle.testOfCurrentRun`) adds no frame to the recording of the test in progress.
 */
class RecordFrameListener(
    private val recorder: FrameRecorder,
    private val frameProvider: ArtifactProvider,
    private val lifecycle: AllureLifecycle? = null,
) : UltronLifecycleListener() {
    init { id = RecordFrameListener::class.simpleName!! }

    override fun before(operation: Operation) {
        // Later operations need no frame before them: the frame after the previous operation already shows that screen.
        if (!recorder.isRecording || recorder.size > 0) return
        capture(operation, "before ${operation.name}")
    }

    override fun after(operationResult: OperationResult<Operation>) {
        if (!recorder.isRecording) return
        capture(operationResult.operation)
    }

    override fun betweenRetries(operation: Operation) {
        if (!recorder.isRetryFrameDue()) return
        capture(operation)
    }

    private fun capture(operation: Operation, label: String = operation.name) {
        if (operation.type in UltronCommonConfig.operationsExcludedFromListeners) return
        if (lifecycle?.isCurrentRunLate() == true) return
        runCatching { frameProvider.produce()?.let { recorder.add(it, label) } }
            .onFailure { UltronLog.error("Allure: video frame '$label' capture failed: ${it.message}") }
    }
}
