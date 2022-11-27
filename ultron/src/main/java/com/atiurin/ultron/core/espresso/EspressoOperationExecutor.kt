package com.atiurin.ultron.core.espresso

import androidx.test.espresso.NoMatchingViewException
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationExecutor
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.ResultDescriptor
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.config.UltronConfig.Espresso.Companion.ESPRESSO_OPERATION_POLLING_TIMEOUT

abstract class EspressoOperationExecutor<T : Operation>(
    override val operation: T
) : OperationExecutor<T, EspressoOperationResult<T>> {
    override val descriptor: ResultDescriptor
        get() = ResultDescriptor()
    override val pollingTimeout: Long
        get() = ESPRESSO_OPERATION_POLLING_TIMEOUT

    override fun generateResult(
        success: Boolean,
        exceptions: List<Throwable>,
        description: String,
        lastOperationIterationResult: OperationIterationResult?,
        executionTimeMs: Long
    ): EspressoOperationResult<T> {
        return EspressoOperationResult(
            operation = operation,
            success = success,
            exceptions = exceptions,
            description = description,
            operationIterationResult = lastOperationIterationResult,
            executionTimeMs = executionTimeMs
        )
    }

    override fun getWrapperException(originalException: Throwable): Throwable {
        return if (originalException is NoMatchingViewException){
            NoMatchingViewException.Builder().from(originalException).includeViewHierarchy(UltronConfig.Espresso.INCLUDE_VIEW_HIERARCHY_TO_EXCEPTION).build()
        } else originalException
    }
}