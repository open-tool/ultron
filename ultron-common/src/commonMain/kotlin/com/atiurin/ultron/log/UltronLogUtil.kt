package com.atiurin.ultron.log

object UltronLogUtil {
    const val testStageDelimiter    = "========================================================================================================"
    const val stepDelimiter         = "********************************************************************"

    fun logTextBlock(text: String, logLevel: LogLevel = LogLevel.I, delimiter: String = testStageDelimiter) {
        UltronLog.log(logLevel, delimiter)
        UltronLog.log(logLevel, text)
        UltronLog.log(logLevel, delimiter)
    }
}