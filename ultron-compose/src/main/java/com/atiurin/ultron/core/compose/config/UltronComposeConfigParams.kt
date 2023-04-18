package com.atiurin.ultron.core.compose.config

data class UltronComposeConfigParams(
    var operationTimeoutMs: Long = UltronComposeConfig.DEFAULT_OPERATION_TIMEOUT,
    var operationPollingTimeoutMs: Long = 0,
    var lazyColumnOperationTimeoutMs: Long = UltronComposeConfig.DEFAULT_LAZY_COLUMN_OPERATIONS_TIMEOUT,
    var lazyColumnItemSearchLimit: Int = -1
)
