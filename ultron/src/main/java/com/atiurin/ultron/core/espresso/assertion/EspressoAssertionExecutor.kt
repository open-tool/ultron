package com.atiurin.ultron.core.espresso.assertion

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.EspressoOperationExecutor
import com.atiurin.ultron.core.espresso.UltronEspressoOperation

internal class EspressoAssertionExecutor(
    operation: UltronEspressoOperation
) : EspressoOperationExecutor<UltronEspressoOperation>(operation){
    override fun getAllowedExceptions(operation: Operation): List<Class<out Throwable>> {
        return UltronConfig.Espresso.ViewAssertionConfig.allowedExceptions
    }
}