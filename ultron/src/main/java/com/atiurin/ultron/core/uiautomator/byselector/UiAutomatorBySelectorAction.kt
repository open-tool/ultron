package com.atiurin.ultron.core.uiautomator.byselector

import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiObject2
import com.atiurin.ultron.core.common.DefaultOperationIterationResult
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.OperationType
import com.atiurin.ultron.core.common.exceptions.UiObject2NotFoundException
import com.atiurin.ultron.core.config.UltronConfig.UiAutomator.Companion.uiDevice

class UiAutomatorBySelectorAction (
        val block: UiObject2.() -> Unit,
        private val bySelector: BySelector,
        override val name: String,
        override val description: String,
        override val type: OperationType,
        override val timeoutMs: Long) : Operation {

    override fun execute(): OperationIterationResult {
        var success = true
        var exception: Throwable? = null
        try {
            val uiObject2 = uiDevice.findObject(bySelector)
            if (uiObject2 != null){
                uiObject2.block()
            } else throw UiObject2NotFoundException("No object with bySelector: $bySelector")
        } catch (error: Throwable) {
            success = false
            exception = error
        }
        return DefaultOperationIterationResult(success, exception)
    }
}