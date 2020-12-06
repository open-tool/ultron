package com.atiurin.ultron.core.espresso

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult

class EspressoOperationResult<T : Operation>(
    override val operation: T,
    override val success: Boolean,
    override val exception: Throwable?,
    override var description: String
) : OperationResult<T>