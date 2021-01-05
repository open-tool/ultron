package com.atiurin.ultron.extensions

import androidx.test.uiautomator.UiSelector
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.uiautomator.UiAutomatorActionType
import com.atiurin.ultron.core.uiautomator.UiAutomatorLifecycle
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperationResult
import com.atiurin.ultron.core.uiautomator.uiobject.UiAutomatorUiSelectorOperation
import com.atiurin.ultron.core.uiautomator.uiobject.UiAutomatorUiSelectorOperationExecutor

fun UiSelector.click(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiAutomatorLifecycle.execute(
        UiAutomatorUiSelectorOperationExecutor(
            UiAutomatorUiSelectorOperation(
                uiSelector = this,
                block = { click() },
                name = "Click to $this",
                type = UiAutomatorActionType.CLICK,
                description = "UiSelector action with type '${UiAutomatorActionType.CLICK}'. Click to $this during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun UiSelector.exists(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
): Boolean {
    var isExist = false
    UiAutomatorLifecycle.execute(
        UiAutomatorUiSelectorOperationExecutor(
            UiAutomatorUiSelectorOperation(
                uiSelector = this,
                block = {
                    isExist = this.exists()
                    isExist
                },
                name = "Exists of $this",
                type = UiAutomatorActionType.CLICK,
                description = "UiSelector assertion with type '${UiAutomatorActionType.CLICK}'. Click to $this during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return isExist
}