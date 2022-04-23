package com.atiurin.ultron.core.compose.operation

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationExecutor
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.exceptions.UltronWrapperException
import java.lang.AssertionError
import java.lang.NullPointerException

internal class ComposeOperationExecutor(
    override val operation: UltronComposeOperation
) : OperationExecutor<UltronComposeOperation, ComposeOperationResult<UltronComposeOperation>> {
    override val pollingTimeout: Long
        get() = UltronConfig.Compose.COMPOSE_OPERATION_POLLING_TIMEOUT

    override fun generateResult(
        success: Boolean,
        exceptions: List<Throwable>,
        description: String,
        operationIterationResult: OperationIterationResult?
    ): ComposeOperationResult<UltronComposeOperation> {
        return ComposeOperationResult(
            operation = operation,
            success = success,
            exceptions = exceptions,
            description = description,
            operationIterationResult = operationIterationResult
        )
    }

    override fun getAllowedExceptions(operation: Operation): List<Class<out Throwable>> {
        return UltronConfig.Compose.allowedExceptions
    }

    override fun getWrapperException(originalException: Throwable): Throwable {
        return if (originalException is AssertionError){
            UltronWrapperException("[${operation.description}] failed", originalException)
        } else originalException
    }
}