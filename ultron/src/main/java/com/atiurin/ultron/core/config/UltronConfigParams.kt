package com.atiurin.ultron.core.config

import com.atiurin.ultron.core.config.UltronConfig.DEFAULT_OPERATION_TIMEOUT_MS

data class UltronConfigParams(
    var accelerateUiAutomator: Boolean = true,
    var logToFile: Boolean = true,
    var operationTimeoutMs: Long = DEFAULT_OPERATION_TIMEOUT_MS
)