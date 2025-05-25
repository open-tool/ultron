package com.atiurin.ultron.core.config

import com.atiurin.ultron.core.espresso.recyclerview.UltronRecyclerViewImpl

data class UltronConfigParams(
    var accelerateUiAutomator: Boolean = true,
    var operationTimeoutMs: Long = UltronCommonConfig.operationTimeoutMs,
    var recyclerViewImpl: UltronRecyclerViewImpl = UltronRecyclerViewImpl.STANDARD
){
    @Deprecated("Use global setting UltronCommonConfig.logToFile", ReplaceWith("UltronCommonConfig.logToFile"))
    var logToFile: Boolean = UltronCommonConfig.logToFile
        set(value) {
            field = value
            UltronCommonConfig.logToFile = value
        }
}