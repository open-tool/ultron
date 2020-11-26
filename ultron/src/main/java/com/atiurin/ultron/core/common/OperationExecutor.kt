package com.atiurin.ultron.core.common

interface OperationExecutor {
    fun execute() : OperationResult
    fun getOperation() : Operation
}
