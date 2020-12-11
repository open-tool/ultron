package com.atiurin.ultron.core.espressoweb.assertion

import androidx.test.espresso.web.assertion.WebAssertion
import androidx.test.espresso.web.sugar.Web
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.OperationType

class WebInteractionAssertion<T, R>(
    val webInteraction: Web.WebInteraction<T>,
    val webAssertion: WebAssertion<R>,
    override val name: String,
    override val type: OperationType,
    override val description: String,
    override val timeoutMs: Long
) : Operation {
    override fun execute() : OperationIterationResult {
        var success = true
        var exception: Throwable? = null
        try {
            webInteraction.check(webAssertion)
        }catch (error: Throwable){
            success = false
            exception = error
        }
        return OperationIterationResult(success, exception)
    }
}