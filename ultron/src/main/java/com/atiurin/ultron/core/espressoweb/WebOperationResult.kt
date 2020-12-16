package com.atiurin.ultron.core.espressoweb

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.OperationResult

class WebOperationResult<T : Operation>(
    override val operation: T,
    override val success: Boolean,
    override val exception: Throwable?,
    override var description: String,
    override var operationIterationResult: OperationIterationResult?
) : OperationResult<T>
