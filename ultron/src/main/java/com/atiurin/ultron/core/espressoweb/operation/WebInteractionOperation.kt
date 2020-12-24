package com.atiurin.ultron.core.espressoweb.operation

import androidx.test.espresso.web.sugar.Web
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.OperationType

class WebInteractionOperation<T>(
    val webInteractionBlock: () -> Web.WebInteraction<T>,
    override val name: String,
    override val type: OperationType,
    override val description: String,
    override val timeoutMs: Long
) : Operation {
    override fun execute() : OperationIterationResult {
        var success = true
        var exception: Throwable? = null
        var resultWebInteraction: Web.WebInteraction<T>? = null
        try {
            resultWebInteraction = webInteractionBlock()
        }catch (error: Throwable){
            success = false
            exception = error
        }
        return WebInteractionOperationIterationResult(success, exception, resultWebInteraction)
    }
}