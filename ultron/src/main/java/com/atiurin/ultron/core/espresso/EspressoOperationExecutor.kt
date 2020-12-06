package com.atiurin.ultron.core.espresso

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationExecutor
import com.atiurin.ultron.core.config.UltronConfig.Espresso.Companion.ESPRESSO_OPERATION_POLLING_TIMEOUT

abstract class EspressoOperationExecutor<T : Operation>(
    override val operation: T
) : OperationExecutor<T, EspressoOperationResult<T>> {
    override val pollingTimeout: Long
        get() = ESPRESSO_OPERATION_POLLING_TIMEOUT
    override fun generateResult(success: Boolean, exceptions: List<Throwable>, description: String): EspressoOperationResult<T> {
        return EspressoOperationResult(
            operation = operation,
            success = success,
            exception = exceptions.lastOrNull(),
            description = description
        )
    }
}