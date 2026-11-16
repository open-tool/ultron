package com.atiurin.ultron.allure.runner

import com.atiurin.ultron.allure.attachment.AttachUtil
import com.atiurin.ultron.allure.config.AllureAttachStrategy
import com.atiurin.ultron.allure.screenrecord.ScreenRecordOptions
import com.atiurin.ultron.allure.screenrecord.ScreenRecorder
import com.atiurin.ultron.extensions.fullTestName
import com.atiurin.ultron.file.MimeType
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.runner.UltronRunListener
import org.junit.runner.Description
import org.junit.runner.notification.Failure

/**
 * Records every test with `screenrecord` and attaches the mp4 according to [policies]:
 * `TEST_FAILURE` attaches in `testFailure` (while the Allure test case is certainly open),
 * `TEST_FINISH` attaches in `testFinished` for tests that did not fail.
 */
class ScreenRecordRunListener(
    private val policies: Set<AllureAttachStrategy>,
    options: ScreenRecordOptions = ScreenRecordOptions(),
) : UltronRunListener() {
    private val recorder = ScreenRecorder(options)
    private var attached = false

    override fun testStarted(description: Description) {
        attached = false
        recorder.start(description.fullTestName())
    }

    override fun testFailure(failure: Failure) {
        if (!policies.contains(AllureAttachStrategy.TEST_FAILURE)) return
        stopAndAttach(failure.description)
    }

    override fun testFinished(description: Description) {
        if (!recorder.isRecording) return
        if (policies.contains(AllureAttachStrategy.TEST_FINISH) && !attached) stopAndAttach(description)
        else recorder.stopAndDiscard()
    }

    private fun stopAndAttach(description: Description) {
        val file = recorder.stop() ?: return
        runCatching {
            val name = AttachUtil.attachFile("video_${description.fullTestName()}", file, MimeType.VIDEO_MP4)
            attached = true
            UltronLog.info("Screen recording '$name' (${file.length()} bytes) attached to Allure report")
        }.onFailure { UltronLog.error("Failed to attach screen recording: ${it.message}") }
        file.delete()
    }
}
