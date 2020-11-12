package com.atiurin.ultron.core.espresso

import com.atiurin.ultron.core.common.Operation

interface EspressoExecutor {
    fun execute() : EspressoOperationResult
    fun getOperation() : Operation
}