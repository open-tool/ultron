package com.atiurin.ultron.core.espressoweb.assertion

import androidx.test.espresso.web.model.ElementReference
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espressoweb.WebOperationExecutor

class WebInteractionAssertionExecutor<T>(
    assertion: WebInteractionAssertion<T>
) : WebOperationExecutor<WebInteractionAssertion<T>>(assertion) {
    override fun getAllowedExceptions(operation: Operation): List<Class<out Throwable>> {
        return UltronConfig.Espresso.WebInteractionAssertionConfig.allowedExceptions
    }
}