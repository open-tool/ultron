package com.atiurin.ultron.core.espresso.action

import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import com.atiurin.ultron.core.common.DefaultOperationIterationResult
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.OperationType

class ViewInteractionEspressoAction(
    val viewInteraction: ViewInteraction,
    override val viewAction: ViewAction,
    override val name: String,
    override val type: OperationType,
    override val description: String,
    override val timeoutMs: Long
) : EspressoAction {
    override fun execute(): OperationIterationResult {
        var success = true
        var exception: Throwable? = null
        viewInteraction.withFailureHandler { error, _ ->
            success = false
            exception = error
        }.perform(viewAction)
        return DefaultOperationIterationResult(success, exception)
    }
}