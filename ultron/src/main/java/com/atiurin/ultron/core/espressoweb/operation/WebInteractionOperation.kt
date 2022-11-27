package com.atiurin.ultron.core.espressoweb.operation

import androidx.test.espresso.web.sugar.Web
import com.atiurin.ultron.core.common.*
import com.atiurin.ultron.core.common.assertion.DefaultOperationAssertion
import com.atiurin.ultron.core.common.assertion.OperationAssertion

class WebInteractionOperation<T>(
    val webInteractionBlock: () -> Web.WebInteraction<T>,
    override val name: String,
    override val type: UltronOperationType,
    override val description: String,
    override val timeoutMs: Long,
    override val assertion: OperationAssertion = DefaultOperationAssertion("") {}
) : Operation {
    override fun execute(): OperationIterationResult {
        var success = true
        var exception: Throwable? = null
        var resultWebInteraction: Web.WebInteraction<T>? = null
        try {
            resultWebInteraction = webInteractionBlock()
        } catch (error: Throwable) {
            success = false
            exception = error
        }
        return WebInteractionOperationIterationResult(success, exception, resultWebInteraction)
    }
}