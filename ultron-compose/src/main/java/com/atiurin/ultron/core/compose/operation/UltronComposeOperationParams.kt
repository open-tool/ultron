package com.atiurin.ultron.core.compose.operation

import com.atiurin.ultron.core.common.UltronOperationType

data class UltronComposeOperationParams(
    val operationName: String,
    val operationDescription: String,
    val operationType: UltronOperationType = ComposeOperationType.CUSTOM
)
