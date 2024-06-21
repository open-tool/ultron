package com.atiurin.ultron.core.config

data class UltronConfigParams(
    var accelerateUiAutomator: Boolean = true,
    var operationTimeoutMs: Long = UltronCommonConfig.operationTimeoutMs
){
    @Deprecated("Use global setting UltronCommonConfig.logToFile", ReplaceWith("UltronCommonConfig.logToFile"))
    var logToFile: Boolean = UltronCommonConfig.logToFile
        set(value) {
            field = value
            UltronCommonConfig.logToFile = value
        }
}