package com.atiurin.ultron.core.espressoweb.operation

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.config.UltronConfig

internal class WebInteractionOperationExecutor <T> (
    operation: WebInteractionOperation<T>
) : WebOperationExecutor<WebInteractionOperation<T>>(operation) {
    override fun getAllowedExceptions(operation: Operation): List<Class<out Throwable>> {
        return UltronConfig.Espresso.WebInteractionOperationConfig.allowedExceptions
    }
}
