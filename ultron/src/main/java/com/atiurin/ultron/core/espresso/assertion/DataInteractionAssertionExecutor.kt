package com.atiurin.ultron.core.espresso.assertion

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.EspressoOperationExecutor

open class DataInteractionAssertionExecutor(
    val assertion: EspressoAssertion
) : EspressoOperationExecutor(assertion){
    override fun getAllowedExceptions(operation: Operation): List<Class<out Throwable>> {
        return UltronConfig.Espresso.ViewAssertionConfig.allowedExceptions
    }
}