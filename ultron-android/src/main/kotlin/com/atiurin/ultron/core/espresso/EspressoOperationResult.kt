package com.atiurin.ultron.core.espresso

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.OperationResult

class EspressoOperationResult<T : Operation>(
    override val operation: T,
    override val success: Boolean,
    override val exceptions: List<Throwable> = emptyList(),
    override var description: String,
    override var operationIterationResult: OperationIterationResult?,
    override val executionTimeMs: Long
) : OperationResult<T>