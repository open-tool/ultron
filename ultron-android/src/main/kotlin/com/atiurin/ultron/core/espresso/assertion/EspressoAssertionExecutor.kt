package com.atiurin.ultron.core.espresso.assertion

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.EspressoOperationExecutor
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import kotlin.reflect.KClass

internal class EspressoAssertionExecutor(
    operation: UltronEspressoOperation
) : EspressoOperationExecutor<UltronEspressoOperation>(operation) {
    override fun getAllowedExceptions(operation: Operation): List<KClass<out Throwable>> {
        return UltronConfig.Espresso.ViewAssertionConfig.allowedExceptions.map { it.kotlin }
    }
}