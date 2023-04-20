package com.atiurin.ultron.allure.runner

import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.runner.UltronRunListener
import org.junit.runner.Description

class UltronLogCleanerRunListener : UltronRunListener() {
    override fun testFinished(description: Description) {
        if (UltronConfig.params.logToFile){
            UltronLog.info("Clear log file")
            UltronLog.fileLogger.clearFile()
        }
    }
}