package com.atiurin.ultron.core.common

data class DefaultOperationIterationResult(
    override val success: Boolean,
    override val exception: Throwable?
) : OperationIterationResult