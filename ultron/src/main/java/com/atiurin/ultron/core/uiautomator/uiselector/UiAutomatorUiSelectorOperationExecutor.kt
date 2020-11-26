package com.atiurin.ultron.core.uiautomator.uiselector

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperationExecutor

class UiAutomatorUiSelectorOperationExecutor (
        operation: UiAutomatorUiSelectorOperation
): UiAutomatorOperationExecutor(operation) {
    override fun getAllowedExceptions(operation: Operation): List<Class<out Throwable>> {
        return UltronConfig.UiAutomator.UiObjectConfig.allowedExceptions
    }
}