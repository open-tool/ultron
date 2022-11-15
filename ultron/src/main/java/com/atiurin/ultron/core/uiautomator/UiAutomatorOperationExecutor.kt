package com.atiurin.ultron.core.uiautomator

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationExecutor
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.ResultDescriptor
import com.atiurin.ultron.core.config.UltronConfig.UiAutomator.Companion.UIAUTOMATOR_OPERATION_POLLING_TIMEOUT
import com.atiurin.ultron.exceptions.UltronWrapperException
import java.lang.NullPointerException

abstract class UiAutomatorOperationExecutor<T : Operation>(
        override val operation: T
): OperationExecutor<T, UiAutomatorOperationResult<T>> {
    override val descriptor: ResultDescriptor
        get() = ResultDescriptor()
    override val pollingTimeout: Long
        get() = UIAUTOMATOR_OPERATION_POLLING_TIMEOUT

    override fun generateResult(
        success: Boolean,
        exceptions: List<Throwable>,
        description: String,
        lastOperationIterationResult: OperationIterationResult?,
        executionTimeMs: Long
    ): UiAutomatorOperationResult<T> {
        return UiAutomatorOperationResult(
            operation = operation,
            success = success,
            exceptions = exceptions,
            description = description,
            operationIterationResult = lastOperationIterationResult,
            executionTimeMs = executionTimeMs
        )
    }

    override fun getWrapperException(originalException: Throwable): Throwable {
        return if (originalException is NullPointerException){
             UltronWrapperException("""
                |Looks like UI element not found for execution of [${operation.description}]. 
                |Original error NullPointerException[${originalException.message}] 
                |Usually it happens while searching for an object 
             """.trimMargin())
        } else originalException
    }
}