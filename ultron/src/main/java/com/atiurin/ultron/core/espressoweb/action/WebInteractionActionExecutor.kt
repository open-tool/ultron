package com.atiurin.ultron.core.espressoweb.action

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espressoweb.WebOperationExecutor

class WebInteractionActionExecutor <T, R> (
    action: WebInteractionAction<T, R>
) : WebOperationExecutor<WebInteractionAction<T, R>>(action) {
    override fun getAllowedExceptions(operation: Operation): List<Class<out Throwable>> {
        return UltronConfig.Espresso.WebInteractionActionConfig.allowedExceptions
    }
}
