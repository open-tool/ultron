package com.atiurin.ultron.core.compose.operation

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationExecutor
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.ResultDescriptor
import com.atiurin.ultron.core.compose.config.UltronComposeConfig
import com.atiurin.ultron.exceptions.UltronWrapperException
import kotlin.reflect.KClass

internal class ComposeOperationExecutor(
    override val operation: UltronComposeOperation
) : OperationExecutor<UltronComposeOperation, ComposeOperationResult<UltronComposeOperation>> {
    override val descriptor: ResultDescriptor
        get() = ResultDescriptor()
    override val pollingTimeout: Long
        get() = UltronComposeConfig.params.operationPollingTimeoutMs

    override fun generateResult(
        success: Boolean,
        exceptions: List<Throwable>,
        description: String,
        lastOperationIterationResult: OperationIterationResult?,
        executionTimeMs: Long
    ): ComposeOperationResult<UltronComposeOperation> {
        return ComposeOperationResult(
            operation = operation,
            success = success,
            exceptions = exceptions,
            description = description,
            operationIterationResult = lastOperationIterationResult,
            executionTimeMs = executionTimeMs
        )
    }

    override fun getAllowedExceptions(operation: Operation): List<KClass<out Throwable>> {
        return UltronComposeConfig.allowedExceptions
    }

    override fun getWrapperException(originalException: Throwable): Throwable {
        return if (originalException is AssertionError){
            UltronWrapperException("[${operation.description}] failed", originalException)
        } else originalException
    }
}