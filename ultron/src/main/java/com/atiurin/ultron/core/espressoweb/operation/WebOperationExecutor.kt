package com.atiurin.ultron.core.espressoweb.operation

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationExecutor
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.config.UltronConfig

abstract class WebOperationExecutor<T : Operation>(
    override val operation: T
) : OperationExecutor<T, WebOperationResult<T>> {
    override val pollingTimeout: Long
        get() = UltronConfig.Espresso.ESPRESSO_OPERATION_POLLING_TIMEOUT

    override fun generateResult(
        success: Boolean,
        exceptions: List<Throwable>,
        description: String,
        operationIterationResult: OperationIterationResult?
    ): WebOperationResult<T> {
        return WebOperationResult(
            operation = operation,
            success = success,
            exception = exceptions.lastOrNull(),
            description = description,
            operationIterationResult = operationIterationResult
        )
    }
}