package com.atiurin.ultron.core.espresso.assertion

import android.view.View
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.OperationType
import org.hamcrest.Matcher

class ViewInteractionEspressoAssertion(
    val viewInteraction: ViewInteraction,
    override val matcher: Matcher<View>,
    override val name: String,
    override val type: OperationType,
    override val description: String,
    override val timeoutMs: Long
): EspressoAssertion{
    override fun execute() : OperationIterationResult {
        var success = true
        var exception: Throwable? = null
        try {
            viewInteraction.check(ViewAssertions.matches(matcher))
        }catch (error: Throwable){
            success = false
            exception = error
        }
        return OperationIterationResult(success, exception)
    }
}