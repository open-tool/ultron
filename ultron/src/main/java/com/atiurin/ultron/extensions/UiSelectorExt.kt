package com.atiurin.ultron.extensions

import androidx.test.uiautomator.UiSelector
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.config.UltronConfig.UiAutomator.Companion.uiDevice
import com.atiurin.ultron.core.uiautomator.UiAutomatorActionType
import com.atiurin.ultron.core.uiautomator.UiAutomatorLifecycle
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperationResult
import com.atiurin.ultron.core.uiautomator.uiobject.UiAutomatorUiSelectorOperation
import com.atiurin.ultron.core.uiautomator.uiobject.UiAutomatorUiSelectorOperationExecutor
import com.atiurin.ultron.core.uiautomator.uiobject.UiObjectOperations

fun UiSelector.click(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.click({uiDevice.findObject(this)}, this.toString(), timeoutMs, resultHandler)
}

fun UiSelector.exists(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.exists({uiDevice.findObject(this)}, this.toString(), timeoutMs, resultHandler)
}

fun UiSelector.notExists(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.notExists({uiDevice.findObject(this)}, this.toString(), timeoutMs, resultHandler)
}