package com.atiurin.ultron.core.espressoweb.assertion

import androidx.test.espresso.web.sugar.Web
import com.atiurin.ultron.core.common.DefaultOperationIterationResult
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.OperationType

class WebInteractionAssertion<T>(
    val assertionBlock: () -> Web.WebInteraction<T>,
    override val name: String,
    override val type: OperationType,
    override val description: String,
    override val timeoutMs: Long
) : Operation {
    override fun execute() : OperationIterationResult {
        var success = true
        var exception: Throwable? = null
        try {
            assertionBlock()
        }catch (error: Throwable){
            success = false
            exception = error
        }
        return DefaultOperationIterationResult(success, exception)
    }
}