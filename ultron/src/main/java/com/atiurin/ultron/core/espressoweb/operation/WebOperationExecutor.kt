package com.atiurin.ultron.core.espressoweb.operation

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationExecutor
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.exceptions.UltronWrapperException

internal abstract class WebOperationExecutor<T : Operation>(
    override val operation: T
) : OperationExecutor<T, WebOperationResult<T>> {
    override val pollingTimeout: Long
        get() = UltronConfig.Espresso.ESPRESSO_OPERATION_POLLING_TIMEOUT

    override fun generateResult(
        success: Boolean,
        exceptions: List<Throwable>,
        description: String,
        lastOperationIterationResult: OperationIterationResult?,
        executionTimeMs: Long
    ): WebOperationResult<T> {
        return WebOperationResult(
            operation = operation,
            success = success,
            exceptions = exceptions,
            description = description,
            operationIterationResult = lastOperationIterationResult,
            executionTimeMs = executionTimeMs
        )
    }

    override fun getWrapperException(originalException: Throwable): Throwable {
        var modifiedException: Throwable? = null
        val isMessageContains = originalException.message?.contains("Atom evaluation returned null!")
        if (isMessageContains != null && isMessageContains){
            modifiedException = UltronWrapperException("Element not found in WebView!", originalException)
        }
        return modifiedException ?: originalException
    }
}