package com.atiurin.ultron.core.espressoweb.action

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espressoweb.WebOperationExecutor

class WebInteractionActionExecutor <T> (
    action: WebInteractionAction<T>
) : WebOperationExecutor<WebInteractionAction<T>>(action) {
    override fun getAllowedExceptions(operation: Operation): List<Class<out Throwable>> {
        return UltronConfig.Espresso.WebInteractionActionConfig.allowedExceptions
    }
}
