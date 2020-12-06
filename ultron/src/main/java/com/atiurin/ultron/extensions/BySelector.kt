package com.atiurin.ultron.extensions

import androidx.test.uiautomator.BySelector
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.uiautomator.UiAutomatorActionType
import com.atiurin.ultron.core.uiautomator.UiAutomatorAssertionType
import com.atiurin.ultron.core.uiautomator.UiAutomatorLifecycle
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperationResult
import com.atiurin.ultron.core.uiautomator.byselector.UiAutomatorBySelectorAction
import com.atiurin.ultron.core.uiautomator.byselector.UiAutomatorBySelectorActionExecutor
import com.atiurin.ultron.core.uiautomator.byselector.UiAutomatorBySelectorAssertion
import com.atiurin.ultron.core.uiautomator.byselector.UiAutomatorBySelectorAssertionExecutor

fun BySelector.isSuccess(
        action: BySelector.() -> Unit
): Boolean {
    var success = true
    try {
        action()
    }catch (th: Throwable){
        success = false
    }
    return success
}


fun BySelector.click(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
){
    UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                            bySelector = this,
                            block = { click() },
                            name = "Click to $this",
                            type = UiAutomatorActionType.CLICK,
                            description = "BySelector action with type '${UiAutomatorActionType.CLICK}'. Click to $this during $timeoutMs ms",
                            timeoutMs = timeoutMs
                    )
            ), resultHandler
    )
}

fun BySelector.hasText(
    text: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
){
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorAssertionExecutor(
            UiAutomatorBySelectorAssertion(
                bySelector = this,
                block = { this.text == text },
                name = "HasText $text in $this",
                type = UiAutomatorAssertionType.HAS_TEXT,
                description = "BySelector assertion with type '${UiAutomatorAssertionType.HAS_TEXT}'. Check $this hasText $text during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun BySelector.containsText(
    text: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
){
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorAssertionExecutor(
            UiAutomatorBySelectorAssertion(
                bySelector = this,
                block = { this.text.contains(text) },
                name = "ContainsText $text in $this",
                type = UiAutomatorAssertionType.CONTAINS_TEXT,
                description = "BySelector assertion with type '${UiAutomatorAssertionType.CONTAINS_TEXT}'. Check $this containsText $text during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}