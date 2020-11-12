package com.atiurin.ultron.core.espresso.assertion

import com.atiurin.ultron.core.espresso.EspressoExecutor
import com.atiurin.ultron.core.espresso.EspressoOperationResult

interface ViewAssertionProcessor {
    fun process(executor: EspressoExecutor): EspressoOperationResult
}