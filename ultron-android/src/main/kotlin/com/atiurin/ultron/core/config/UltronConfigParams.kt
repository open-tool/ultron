package com.atiurin.ultron.core.config

data class UltronConfigParams(
    var accelerateUiAutomator: Boolean = true,
    @Deprecated("Use global setting UltronCommonConfig.logToFile", ReplaceWith("UltronCommonConfig.logToFile"))
    var logToFile: Boolean = UltronCommonConfig.logToFile,
    var operationTimeoutMs: Long = UltronCommonConfig.operationTimeoutMs
)