package com.atiurin.ultron.allure.runner

import com.atiurin.ultron.allure.attachment.AttachUtil
import com.atiurin.ultron.allure.config.UltronAllureConfig
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.file.MimeType
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.runner.UltronRunListener
import org.junit.runner.notification.Failure
import java.io.PrintWriter

class UltronLogAttachRunListener : UltronRunListener() {
    override fun testFailure(failure: Failure) {
        if (UltronAllureConfig.params.attachUltronLog ){
            if (!UltronConfig.params.logToFile){
                UltronLog.error("Ultron doesn't log into file. " +
                        "Change config param UltronConfig.edit { logToFile = true }"
                )
                return
            }
            val fileName = AttachUtil.attachFile(UltronLog.fileLogger.getLogFile(), MimeType.PLAIN_TEXT)
            UltronLog.info("Ultron log file '$fileName' has attached to Allure report")
        }
    }
}