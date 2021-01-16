package com.atiurin.ultron.core.espresso.action

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.EspressoOperation
import com.atiurin.ultron.core.espresso.EspressoOperationExecutor

open class EspressoActionExecutor(
    operation: EspressoOperation
): EspressoOperationExecutor<EspressoOperation>(operation) {
    override fun getAllowedExceptions(operation: Operation): List<Class<out Throwable>> {
        return UltronConfig.Espresso.ViewActionConfig.allowedExceptions
    }
}