package com.atiurin.ultron.allure.runner

import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.runner.UltronRunListener
import org.junit.runner.Description
import org.junit.runner.notification.Failure

class UltronLogCleanerRunListener : UltronRunListener() {
    override fun testFinished(description: Description) {
        if (UltronConfig.getParams().logToFile){
            UltronLog.info("Clear log file")
            UltronLog.fileLogger.clearFile()
        }
    }
}