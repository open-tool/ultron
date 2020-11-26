package com.atiurin.ultron.core.uiautomator.byselector

import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiObject2
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.OperationType
import com.atiurin.ultron.core.uiautomator.UiAutomatorConfig

class UiAutomatorBySelectorAssertion (
        val block: UiObject2.() -> Boolean,
        private val bySelector: BySelector,
        override val name: String,
        override val description: String,
        override val type: OperationType,
        override val timeoutMs: Long) : Operation {
    override fun execute(): OperationIterationResult {
        var success = true
        var exception: Throwable? = null
        try {
            success = UiAutomatorConfig.uiDevice.findObject(bySelector).block()
        } catch (error: Throwable) {
            success = false
            exception = error
        }
        return OperationIterationResult(success, exception)
    }
}