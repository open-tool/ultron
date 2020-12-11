package com.atiurin.ultron.extensions

import android.graphics.Point
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
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
    } catch (th: Throwable) {
        success = false
    }
    return success
}


fun BySelector.webClick(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
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

fun BySelector.webClick(
    duration: Long,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { click(duration) },
                name = "Click to $this with duration = $duration",
                type = UiAutomatorActionType.CLICK,
                description = "BySelector action with type '${UiAutomatorActionType.CLICK}'. Click to $this during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun BySelector.longClick(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { longClick() },
                name = "LongClick to $this",
                type = UiAutomatorActionType.LONG_CLICK,
                description = "BySelector action with type '${UiAutomatorActionType.LONG_CLICK}'. LongClick to $this during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun BySelector.clear(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { clear() },
                name = "Clear of $this",
                type = UiAutomatorActionType.CLEAR_TEXT,
                description = "BySelector action with type '${UiAutomatorActionType.CLEAR_TEXT}'. Clear of $this during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun BySelector.replaceText(
    text: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { this.text = text },
                name = "ReplaceText of $this to '$text'",
                type = UiAutomatorActionType.REPLACE_TEXT,
                description = "BySelector action with type '${UiAutomatorActionType.REPLACE_TEXT}'. ReplaceText of $this to '$text' during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun BySelector.drag(
    dest: Point,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { drag(dest) },
                name = "Drag of $this to dest = '$dest'",
                type = UiAutomatorActionType.DRAG,
                description = "BySelector action with type '${UiAutomatorActionType.DRAG}'. Drag of $this to dest = '$dest' during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun BySelector.drag(
    dest: Point,
    speed: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { drag(dest, speed) },
                name = "Drag of $this to dest = '$dest' with speed = $speed",
                type = UiAutomatorActionType.DRAG,
                description = "BySelector action with type '${UiAutomatorActionType.DRAG}'. Drag of $this to dest = '$dest' with speed = $speed during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun BySelector.fling(
    direction: Direction,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { fling(direction) },
                name = "FLing of $this to direction = '${direction.name}'",
                type = UiAutomatorActionType.FLING,
                description = "BySelector action with type '${UiAutomatorActionType.FLING}'. FLing of $this to  to direction = '${direction.name}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun BySelector.fling(
    direction: Direction,
    speed: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { fling(direction, speed) },
                name = "FLing of $this to direction = '${direction.name}' with speed = $speed",
                type = UiAutomatorActionType.FLING,
                description = "BySelector action with type '${UiAutomatorActionType.FLING}'. FLing of $this to direction = '${direction.name}' with speed = $speed during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun BySelector.getText(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): String {
    var text: String = ""
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { text = this.text },
                name = "GetText of $this",
                type = UiAutomatorActionType.GET_TEXT,
                description = "BySelector action with type '${UiAutomatorActionType.GET_TEXT}'. GetText of $this  during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return text
}

fun BySelector.getApplicationPackage(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): String {
    var packageName: String = ""
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { packageName = this.applicationPackage },
                name = "GetText of $this",
                type = UiAutomatorActionType.GET_APPLICATION_PACKAGE,
                description = "BySelector action with type '${UiAutomatorActionType.GET_APPLICATION_PACKAGE}'. GetText of $this  during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return packageName
}

//asserts
fun BySelector.hasText(
    text: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
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
) {
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