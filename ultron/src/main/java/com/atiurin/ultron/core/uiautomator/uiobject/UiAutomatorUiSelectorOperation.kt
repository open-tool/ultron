package com.atiurin.ultron.core.uiautomator.uiobject

import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import com.atiurin.ultron.core.common.DefaultOperationIterationResult
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.OperationType
import com.atiurin.ultron.core.config.UltronConfig.UiAutomator.Companion.uiDevice
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperation

class UiAutomatorUiSelectorOperation(
    private val operationBlock: UiObject.() -> Boolean,
    private val objectBlock: () -> UiObject,
    override val name: String,
    override val description: String,
    override val type: OperationType,
    override val timeoutMs: Long) : UiAutomatorOperation {
    override fun execute(): OperationIterationResult {
        var success = true
        var exception: Throwable? = null
        try {
            success = objectBlock().operationBlock()
        } catch (error: Throwable) {
            success = false
            exception = error
        }
        return DefaultOperationIterationResult(success, exception)
    }
}
