package com.atiurin.ultron.allure.runner

import com.atiurin.ultron.allure.attachment.AttachUtil
import com.atiurin.ultron.file.MimeType
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.runner.UltronRunListener
import org.junit.runner.notification.Failure

class AllureLogAttachRunListener : UltronRunListener {
    override fun testFailure(failure: Failure) {
        val fileName = AttachUtil.attachFile(UltronLog.fileLogger.getLogFile(), MimeType.PLAIN_TEXT)
        UltronLog.info("Attached Ultron log file '$fileName' to allure report")
    }
}