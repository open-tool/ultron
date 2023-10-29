package com.atiurin.ultron.core.uiautomator.uiobject

import com.atiurin.ultron.core.common.DefaultOperationIterationResult
import com.atiurin.ultron.core.common.ElementInfo
import com.atiurin.ultron.core.common.DefaultElementInfo
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.UltronOperationType
import com.atiurin.ultron.core.common.assertion.DefaultOperationAssertion
import com.atiurin.ultron.core.common.assertion.OperationAssertion
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperation
import com.atiurin.ultron.exceptions.UltronUiAutomatorException

class UiAutomatorUiSelectorOperation(
    private val operationBlock: () -> Boolean,
    override val name: String,
    override val description: String,
    override val type: UltronOperationType,
    override val timeoutMs: Long,
    override val assertion: OperationAssertion = DefaultOperationAssertion("") {},
    override val elementInfo: ElementInfo = DefaultElementInfo()
) : UiAutomatorOperation {
    override fun execute(): OperationIterationResult {
        var success = true
        var exception: Throwable? = null
        try {
            success = operationBlock()
            if (!success) throw UltronUiAutomatorException("$name returns false. It means operation failed.")
        } catch (error: Throwable) {
            success = false
            exception = error
        }
        return DefaultOperationIterationResult(success, exception)
    }
}
