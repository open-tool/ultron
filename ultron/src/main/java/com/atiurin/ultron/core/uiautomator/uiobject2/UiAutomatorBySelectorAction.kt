package com.atiurin.ultron.core.uiautomator.uiobject2

import androidx.test.uiautomator.UiObject2
import com.atiurin.ultron.core.common.DefaultOperationIterationResult
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.OperationType
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperation

class UiAutomatorBySelectorAction(
    private val actionBlock: () -> Unit,
    override val name: String,
    override val description: String,
    override val type: OperationType,
    override val timeoutMs: Long
) : UiAutomatorOperation {
    override fun execute(): OperationIterationResult {
        var success = true
        var exception: Throwable? = null
        try {
            actionBlock()
        } catch (error: Throwable) {
            success = false
            exception = error
        }
        return DefaultOperationIterationResult(success, exception)
    }
}