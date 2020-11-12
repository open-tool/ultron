package com.atiurin.ultron.core.espresso.action

import com.atiurin.ultron.core.espresso.EspressoExecutor
import com.atiurin.ultron.core.espresso.EspressoOperationResult

interface ViewActionProcessor {
    fun process(executor: EspressoExecutor) : EspressoOperationResult
}