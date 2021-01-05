package com.atiurin.ultron.core.uiautomator.uiobject2

import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiObject2
import com.atiurin.ultron.core.common.DefaultOperationIterationResult
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.OperationType
import com.atiurin.ultron.core.config.UltronConfig.UiAutomator.Companion.uiDevice

class UiAutomatorBySelectorAssertion (
    private val actionBlock: UiObject2.() -> Boolean,
    private val objectBlock: () -> UiObject2,
    override val name: String,
    override val description: String,
    override val type: OperationType,
    override val timeoutMs: Long) : Operation {
    override fun execute(): OperationIterationResult {
        var success = true
        var exception: Throwable? = null
        try {
            success = objectBlock().actionBlock()
        } catch (error: Throwable) {
            success = false
            exception = error
        }
        return DefaultOperationIterationResult(success, exception)
    }
}