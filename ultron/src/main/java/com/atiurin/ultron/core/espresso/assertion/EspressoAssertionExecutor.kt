package com.atiurin.ultron.core.espresso.assertion

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.EspressoOperationExecutor
import com.atiurin.ultron.core.espresso.EspressoOperation

open class EspressoAssertionExecutor(
    operation: EspressoOperation
) : EspressoOperationExecutor<EspressoOperation>(operation){
    override fun getAllowedExceptions(operation: Operation): List<Class<out Throwable>> {
        return UltronConfig.Espresso.ViewAssertionConfig.allowedExceptions
    }
}