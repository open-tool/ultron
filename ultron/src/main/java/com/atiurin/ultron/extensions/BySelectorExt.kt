package com.atiurin.ultron.extensions

import android.graphics.Point
import android.graphics.Rect
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
import com.atiurin.ultron.exceptions.UltronException

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


fun BySelector.click(
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
                description = "BySelector action '${UiAutomatorActionType.CLICK}'. Click to $this during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun BySelector.click(
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
                description = "BySelector action '${UiAutomatorActionType.CLICK}'. Click to $this during $timeoutMs ms",
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
                description = "BySelector action '${UiAutomatorActionType.LONG_CLICK}'. LongClick to $this during $timeoutMs ms",
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
                description = "BySelector action '${UiAutomatorActionType.CLEAR_TEXT}'. Clear of $this during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

/**
 * @return view text
 */
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
                description = "BySelector action '${UiAutomatorActionType.GET_TEXT}'. GetText of $this  during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return text
}

/** Returns the package name of the app that this object belongs to. */
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
                name = "GetApplicationPackage of $this",
                type = UiAutomatorActionType.GET_APPLICATION_PACKAGE,
                description = "BySelector action '${UiAutomatorActionType.GET_APPLICATION_PACKAGE}'. GetApplicationPackage of $this  during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return packageName
}

/** Returns the visible bounds of this object in screen coordinates. */
fun BySelector.getVisibleBounds(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): Rect {
    var visibleBounds: Rect? = null
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { visibleBounds = this.visibleBounds },
                name = "GetVisibleBounds of $this",
                type = UiAutomatorActionType.GET_VISIBLE_BOUNDS,
                description = "BySelector action '${UiAutomatorActionType.GET_VISIBLE_BOUNDS}'. GetVisibleBounds of $this  during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return visibleBounds ?: throw UltronException("Couldn't get visibleBounds of $this")
}

/** Returns a point in the center of the visible bounds of this object. */
fun BySelector.getVisibleCenter(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): Point {
    var visibleCenter: Point? = null
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { visibleCenter = this.visibleCenter },
                name = "GetVisibleCenter of $this",
                type = UiAutomatorActionType.GET_VISIBLE_CENTER,
                description = "BySelector action '${UiAutomatorActionType.GET_VISIBLE_CENTER}'. GetVisibleCenter of $this during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return visibleCenter ?: throw UltronException("Couldn't get visibleCenter of $this")
}

/** Returns the fully qualified resource name for this object's id.  */
fun BySelector.getResourceName(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): String {
    var resName : String? = null
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { resName = this.resourceName },
                name = "GetResourceName of $this",
                type = UiAutomatorActionType.GET_RESOURCE_NAME,
                description = "BySelector action '${UiAutomatorActionType.GET_RESOURCE_NAME}'. GetResourceName of $this during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return resName ?: throw UltronException("Couldn't getResourceName of $this")
}

/**
 * Returns the class name of the underlying [android.view.View] represented by this object.
 */
fun BySelector.getClassName(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): String {
    var className : String? = null
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { className = this.className },
                name = "GetClassName of $this",
                type = UiAutomatorActionType.GET_CLASS_NAME,
                description = "BySelector action '${UiAutomatorActionType.GET_CLASS_NAME}'. GetClassName of $this during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return className ?: throw UltronException("Couldn't getClassName of $this")
}

/** Returns the content description for this object. */
fun BySelector.getContentDescription(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) : String {
    var contentDesc : String? = null
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { contentDesc = this.contentDescription },
                name = "GetContentDescription of $this",
                type = UiAutomatorActionType.GET_CONTENT_DESCRIPTION,
                description = "BySelector action '${UiAutomatorActionType.GET_CONTENT_DESCRIPTION}'. GetContentDescription of $this during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return contentDesc ?: throw UltronException("Couldn't getContentDescription of $this")
}

/** Add the text content if this object is an editable field. */
fun BySelector.addText(
    text: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { this.text += text },
                name = "AddText of $this to '$text'",
                type = UiAutomatorActionType.ADD_TEXT,
                description = "BySelector action '${UiAutomatorActionType.ADD_TEXT}'. AddText of $this to '$text' during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

/**
 * Set the text content by sending individual key codes.
 * @hide
 */
fun BySelector.legacySetText(
    text: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { this.legacySetText(text) },
                name = "LegacySetText of $this to '$text'",
                type = UiAutomatorActionType.LEGACY_SET_TEXT,
                description = "BySelector action '${UiAutomatorActionType.LEGACY_SET_TEXT}'. LegacySetText of $this to '$text' during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

/** Sets the text content if this object is an editable field. */
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
                description = "BySelector action '${UiAutomatorActionType.REPLACE_TEXT}'. ReplaceText of $this to '$text' during $timeoutMs ms",
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
                description = "BySelector action '${UiAutomatorActionType.DRAG}'. Drag of $this to dest = '$dest' during $timeoutMs ms",
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
                description = "BySelector action '${UiAutomatorActionType.DRAG}'. Drag of $this to dest = '$dest' with speed = $speed during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

/**
 * Performs a pinch close gesture on this object.
 *
 * @param percent The size of the pinch as a percentage of this object's size.
 */
fun BySelector.pinchClose(
    percent: Float,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { pinchClose(percent) },
                name = "PinchClose of $this with $percent%",
                type = UiAutomatorActionType.PINCH_CLOSE,
                description = "BySelector action '${UiAutomatorActionType.PINCH_CLOSE}'. PinchClose of $this with $percent% during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

/**
 * Performs a pinch close gesture on this object.
 *
 * @param percent The size of the pinch as a percentage of this object's size.
 * @param speed The speed at which to perform this gesture in pixels per second.
 */
fun BySelector.pinchClose(
    percent: Float,
    speed: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { pinchClose(percent, speed) },
                name = "PinchClose of $this with $percent% and $speed speed",
                type = UiAutomatorActionType.PINCH_CLOSE,
                description = "BySelector action '${UiAutomatorActionType.PINCH_CLOSE}'. PinchClose of $this with $percent% and $speed speed during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

/**
 * Performs a pinch open gesture on this object.
 *
 * @param percent The size of the pinch as a percentage of this object's size.
 */
fun BySelector.pinchOpen(
    percent: Float,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { pinchOpen(percent) },
                name = "PinchOpen of $this with $percent%",
                type = UiAutomatorActionType.PINCH_OPEN,
                description = "BySelector action '${UiAutomatorActionType.PINCH_OPEN}'. PinchOpen of $this with $percent% during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

/**
 * Performs a pinch open gesture on this object.
 *
 * @param percent The size of the pinch as a percentage of this object's size.
 * @param speed The speed at which to perform this gesture in pixels per second.
 */
fun BySelector.pinchOpen(
    percent: Float,
    speed: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { pinchOpen(percent, speed) },
                name = "PinchOpen of $this with $percent% and $speed speed",
                type = UiAutomatorActionType.PINCH_OPEN,
                description = "BySelector action '${UiAutomatorActionType.PINCH_OPEN}'. PinchOpen of $this with $percent% and $speed speed during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

/**
 * Performs a swipe gesture on this object.
 *
 * @param direction The direction in which to swipe.
 * @param percent The length of the swipe as a percentage of this object's size.
 */
fun BySelector.swipe(
    direction: Direction,
    percent: Float,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { swipe(direction, percent) },
                name = "Swipe of $this to direction = '${direction.name}' with $percent%",
                type = UiAutomatorActionType.SWIPE,
                description = "BySelector action '${UiAutomatorActionType.SWIPE}'. Swipe of $this to direction = '${direction.name}' with $percent% during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

/**
 * Performs a swipe gesture on this object.
 *
 * @param direction The direction in which to swipe.
 * @param percent The length of the swipe as a percentage of this object's size.
 * @param speed The speed at which to perform this gesture in pixels per second.
 */
fun BySelector.swipe(
    direction: Direction,
    percent: Float,
    speed: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { swipe(direction, percent, speed) },
                name = "Swipe of $this to direction = '${direction.name}' with $percent% and $speed speed",
                type = UiAutomatorActionType.SWIPE,
                description = "BySelector action '${UiAutomatorActionType.SWIPE}'. Swipe of $this to direction = '${direction.name}' with $percent% and $speed speed during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

/**
 * Performs a scroll gesture on this object.
 *
 * @param direction The direction in which to scroll.
 * @param percent The distance to scroll as a percentage of this object's visible size.
 * @return Whether the object can still scroll in the given direction.
 */
fun BySelector.scroll(
    direction: Direction,
    percent: Float,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): Boolean {
    var result = false
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { result = scroll(direction, percent) },
                name = "Scroll of $this to direction = '${direction.name}' with $percent%",
                type = UiAutomatorActionType.SCROLL,
                description = "BySelector action '${UiAutomatorActionType.SCROLL}'. Scroll of $this to direction = '${direction.name}' with $percent% during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return result
}

/**
 * Performs a scroll gesture on this object.
 *
 * @param direction The direction in which to scroll.
 * @param percent The distance to scroll as a percentage of this object's visible size.
 * @param speed The speed at which to perform this gesture in pixels per second.
 * @return Whether the object can still scroll in the given direction.
 */
fun BySelector.scroll(
    direction: Direction,
    percent: Float,
    speed: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): Boolean {
    var result = false
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { result = scroll(direction, percent, speed) },
                name = "Scroll of $this to direction = '${direction.name}' with $percent% and $speed speed",
                type = UiAutomatorActionType.SCROLL,
                description = "BySelector action '${UiAutomatorActionType.SCROLL}'. Scroll of $this to direction = '${direction.name}' with $percent% and $speed speed during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return result
}

/**
 * Performs a fling gesture on this object.
 *
 * @param direction The direction in which to fling.
 * @return Whether the object can still scroll in the given direction.
 */
fun BySelector.fling(
    direction: Direction,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): Boolean {
    var result: Boolean = false
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { result = fling(direction) },
                name = "FLing of $this to direction = '${direction.name}'",
                type = UiAutomatorActionType.FLING,
                description = "BySelector action '${UiAutomatorActionType.FLING}'. FLing of $this to  to direction = '${direction.name}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return result
}

/**
 * Performs a fling gesture on this object.
 *
 * @param direction The direction in which to fling.
 * @param speed The speed at which to perform this gesture in pixels per second.
 * @return Whether the object can still scroll in the given direction.
 */
fun BySelector.fling(
    direction: Direction,
    speed: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): Boolean {
    var result: Boolean = false
    UiAutomatorLifecycle.execute(
        UiAutomatorBySelectorActionExecutor(
            UiAutomatorBySelectorAction(
                bySelector = this,
                block = { result = fling(direction, speed) },
                name = "FLing of $this to direction = '${direction.name}' with speed = $speed",
                type = UiAutomatorActionType.FLING,
                description = "BySelector action '${UiAutomatorActionType.FLING}'. FLing of $this to direction = '${direction.name}' with speed = $speed during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return result
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
                description = "BySelector assertion '${UiAutomatorAssertionType.HAS_TEXT}'. Check $this hasText $text during $timeoutMs ms",
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
                description = "BySelector assertion '${UiAutomatorAssertionType.CONTAINS_TEXT}'. Check $this containsText $text during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}