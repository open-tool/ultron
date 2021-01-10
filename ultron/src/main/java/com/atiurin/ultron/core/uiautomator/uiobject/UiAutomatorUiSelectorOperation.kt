package com.atiurin.ultron.core.uiautomator.uiobject

import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import com.atiurin.ultron.core.common.DefaultOperationIterationResult
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.OperationType
import com.atiurin.ultron.core.config.UltronConfig.UiAutomator.Companion.uiDevice
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperation
import com.atiurin.ultron.exceptions.UltronException

class UiAutomatorUiSelectorOperation(
    private val operationBlock: () -> Boolean,
    override val name: String,
    override val description: String,
    override val type: OperationType,
    override val timeoutMs: Long) : UiAutomatorOperation {
    override fun execute(): OperationIterationResult {
        var success = true
        var exception: Throwable? = null
        try {
            success = operationBlock()
            if (!success) throw UltronException("$name returns false. It means operation failed.")
        } catch (error: Throwable) {
            success = false
            exception = error
        }
        return DefaultOperationIterationResult(success, exception)
    }
}
