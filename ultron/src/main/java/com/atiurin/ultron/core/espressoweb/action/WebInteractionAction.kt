package com.atiurin.ultron.core.espressoweb.action

import androidx.test.espresso.web.model.Atom
import androidx.test.espresso.web.sugar.Web
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.OperationType

class WebInteractionAction<T, R>(
    val webInteraction: Web.WebInteraction<T>,
    val atom: Atom<R>,
    override val name: String,
    override val type: OperationType,
    override val description: String,
    override val timeoutMs: Long
) : Operation {
    override fun execute() : OperationIterationResult {
        var success = true
        var exception: Throwable? = null
        try {
            webInteraction.perform(atom)
        }catch (error: Throwable){
            success = false
            exception = error
        }
        return OperationIterationResult(success, exception)
    }
}