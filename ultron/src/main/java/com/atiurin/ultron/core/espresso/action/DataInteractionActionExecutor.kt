package com.atiurin.ultron.core.espresso.action

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.EspressoOperationExecutor

open class DataInteractionActionExecutor(
    action: DataInteractionEspressoAction
): EspressoOperationExecutor<DataInteractionEspressoAction>(action) {
    override fun getAllowedExceptions(operation: Operation): List<Class<out Throwable>> {
        return UltronConfig.Espresso.ViewActionConfig.allowedExceptions
    }
}