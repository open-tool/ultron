package com.atiurin.ultron.core.uiautomator.uiobject2

import com.atiurin.ultron.core.common.*
import com.atiurin.ultron.core.common.assertion.DefaultOperationAssertion
import com.atiurin.ultron.core.common.assertion.OperationAssertion
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperation
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.exceptions.UltronOperationException

class UiAutomatorBySelectorAssertion(
    private val assertionBlock: () -> Boolean,
    override val name: String,
    override val description: String,
    override val type: UltronOperationType,
    override val timeoutMs: Long,
    override val assertion: OperationAssertion = DefaultOperationAssertion("") {}
) : UiAutomatorOperation {
    override fun execute(): OperationIterationResult {
        var success = true
        var exception: Throwable? = null
        try {
            success = assertionBlock()
            if (!success) throw UltronOperationException("$name returns false. It means assertion failed.")
        } catch (error: Throwable) {
            success = false
            exception = error
        }
        return DefaultOperationIterationResult(success, exception)
    }
}