package com.atiurin.ultron.core.espressoweb.operation

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.ResultDescriptor
import com.atiurin.ultron.core.config.UltronConfig
import kotlin.reflect.KClass

internal class WebInteractionOperationExecutor<T>(
    operation: WebInteractionOperation<T>
) : WebOperationExecutor<WebInteractionOperation<T>>(operation) {
    override fun getAllowedExceptions(operation: Operation): List<KClass<out Throwable>> {
        return UltronConfig.Espresso.WebInteractionOperationConfig.allowedExceptions.map {
            it.kotlin
        }
    }

    override val descriptor: ResultDescriptor
        get() = ResultDescriptor()
}
