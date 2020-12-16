package com.atiurin.ultron.core.uiautomator

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationExecutor
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.core.config.UltronConfig.UiAutomator.Companion.UIAUTOMATOR_OPERATION_POLLING_TIMEOUT

abstract class UiAutomatorOperationExecutor<T : Operation>(
        override val operation: T
): OperationExecutor<T, UiAutomatorOperationResult<T>> {
    override val pollingTimeout: Long
        get() = UIAUTOMATOR_OPERATION_POLLING_TIMEOUT

    override fun generateResult(
        success: Boolean,
        exceptions: List<Throwable>,
        description: String,
        operationIterationResult: OperationIterationResult?
    ): UiAutomatorOperationResult<T> {
        return UiAutomatorOperationResult(
            operation = operation,
            success = success,
            exception = exceptions.lastOrNull(),
            description = description,
            operationIterationResult = operationIterationResult
        )
    }
}