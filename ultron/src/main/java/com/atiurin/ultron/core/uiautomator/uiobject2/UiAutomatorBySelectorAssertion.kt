package com.atiurin.ultron.core.uiautomator.uiobject2

import com.atiurin.ultron.core.common.DefaultOperationIterationResult
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.UltronOperationType
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperation
import com.atiurin.ultron.exceptions.UltronException

class UiAutomatorBySelectorAssertion (
    private val assertionBlock: () -> Boolean,
    override val name: String,
    override val description: String,
    override val type: UltronOperationType,
    override val timeoutMs: Long) : UiAutomatorOperation {
    override fun execute(): OperationIterationResult {
        var success = true
        var exception: Throwable? = null
        try {
            success = assertionBlock()
            if (!success) throw UltronException("$name returns false. It means assertion failed.")
        } catch (error: Throwable) {
            success = false
            exception = error
        }
        return DefaultOperationIterationResult(success, exception)
    }
}