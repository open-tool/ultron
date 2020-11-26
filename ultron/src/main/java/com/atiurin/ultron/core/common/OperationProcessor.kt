package com.atiurin.ultron.core.common

interface OperationProcessor {
    fun process(executor: OperationExecutor) : OperationResult
}