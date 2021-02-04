package com.atiurin.ultron.core.espresso.action

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import com.atiurin.ultron.core.espresso.EspressoOperationExecutor

internal class EspressoActionExecutor(
    operation: UltronEspressoOperation
): EspressoOperationExecutor<UltronEspressoOperation>(operation) {
    override fun getAllowedExceptions(operation: Operation): List<Class<out Throwable>> {
        return UltronConfig.Espresso.ViewActionConfig.allowedExceptions
    }
}