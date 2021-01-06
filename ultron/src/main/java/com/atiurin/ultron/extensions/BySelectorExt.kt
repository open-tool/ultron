package com.atiurin.ultron.extensions

import android.graphics.Point
import android.graphics.Rect
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiObject2
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.config.UltronConfig.UiAutomator.Companion.uiDevice
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperationResult
import com.atiurin.ultron.core.uiautomator.uiobject2.UiAutomatorBySelectorAction
import com.atiurin.ultron.core.uiautomator.uiobject2.UiAutomatorBySelectorAssertion
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2
import org.hamcrest.Matcher

fun BySelector.isSuccess(
    action: BySelector.() -> Unit
): Boolean {
    return this.methodToBoolean(action)
}

/** Returns object's parent as [UltronUiObject2], or null if it has no parent. */
fun BySelector.getParent(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): UltronUiObject2? {
    val uiObject2 = UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).getParent(timeoutMs, resultHandler)
    return if (uiObject2 != null) UltronUiObject2({ uiObject2 }, "Parent of $this.") else null
}

/**
 * @return Returns a collection of the child elements directly under this object. Empty list if no child exist
 * */
fun BySelector.getChildren(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): List<UltronUiObject2> {
    return UltronUiObject2({ uiDevice.findObject(this) }, this.toString())
        .getChildren(timeoutMs, resultHandler)
        .map { UltronUiObject2({ it }, "Child of $this") }
}

/** Returns the number of child elements directly under this object, 0 if it has no child*/
fun BySelector.getChildCount(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): Int {
    return UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).getChildCount(timeoutMs, resultHandler)
}

/**
 * Searches all elements under this object and returns the first object to match the criteria,
 * or null if no matching objects are found.
 */
fun BySelector.findObject(
    bySelector: BySelector,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): UltronUiObject2? {
    val uiObject2 = UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).findObject(bySelector,timeoutMs, resultHandler)
    return if (uiObject2 != null) UltronUiObject2({ uiObject2 }, "First child of $this with bySelector = $bySelector") else null
}

/** Searches all elements under this object and returns all objects that match the criteria. */
fun BySelector.findObjects(
    bySelector: BySelector,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): List<UltronUiObject2> {
    return UltronUiObject2({ uiDevice.findObject(this) }, this.toString())
        .findObjects(bySelector,timeoutMs, resultHandler)
        .map { UltronUiObject2({ it }, "Child of '$this' with bySelector = $bySelector") }
}
/** Clicks on this object. */
fun BySelector.click(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).click(timeoutMs, resultHandler)
}

/** Performs a click on this object that lasts for {@code duration} milliseconds. */
fun BySelector.click(
    duration: Long,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).click(duration, timeoutMs, resultHandler)
}

/** Performs a long click on this object. */
fun BySelector.longClick(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).longClick(timeoutMs, resultHandler)
}

/** Clears the text content if this object is an editable field. */
fun BySelector.clear(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).clear(timeoutMs, resultHandler)
}

/**
 * @return view text
 */
fun BySelector.getText(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): String {
    return UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).getText(timeoutMs, resultHandler)
}

/** Returns the package name of the app that this object belongs to. */
fun BySelector.getApplicationPackage(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): String {
    return UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).getApplicationPackage(timeoutMs, resultHandler)
}

/** Returns the visible bounds of this object in screen coordinates. */
fun BySelector.getVisibleBounds(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): Rect {
    return UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).getVisibleBounds(timeoutMs, resultHandler)
}

/** Returns a point in the center of the visible bounds of this object. */
fun BySelector.getVisibleCenter(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): Point {
    return UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).getVisibleCenter(timeoutMs, resultHandler)
}

/** Returns the fully qualified resource name for this object's id.  */
fun BySelector.getResourceName(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): String {
    return UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).getResourceName(timeoutMs, resultHandler)
}

/**
 * Returns the class name of the underlying [android.view.View] represented by this object.
 */
fun BySelector.getClassName(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): String {
    return UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).getClassName(timeoutMs, resultHandler)
}

/** Returns the content description for this object. */
fun BySelector.getContentDescription(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) : String {
    return UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).getContentDescription(timeoutMs, resultHandler)
}

/** Add the text content if this object is an editable field. */
fun BySelector.addText(
    text: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    return UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).addText(text,  timeoutMs, resultHandler)
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
    return UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).legacySetText(text, timeoutMs, resultHandler)
}

/** Sets the text content if this object is an editable field. */
fun BySelector.replaceText(
    text: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    return UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).replaceText(text, timeoutMs, resultHandler)
}

/**
 * Drags this object to the specified location.
 *
 * @param dest The end point that this object should be dragged to.
 */
fun BySelector.drag(
    dest: Point,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).drag(dest, timeoutMs, resultHandler)
}

/**
 * Drags this object to the specified location.
 *
 * @param dest The end point that this object should be dragged to.
 * @param speed The speed at which to perform this gesture in pixels per second.
 */
fun BySelector.drag(
    dest: Point,
    speed: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).drag(dest, speed, timeoutMs, resultHandler)
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
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).pinchClose(percent, timeoutMs, resultHandler)
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
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).pinchClose(percent, speed, timeoutMs, resultHandler)
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
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).pinchOpen(percent, timeoutMs, resultHandler)
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
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).pinchOpen(percent, speed, timeoutMs, resultHandler)
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
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).swipe(direction, percent, timeoutMs, resultHandler)
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
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).swipe(direction, percent, speed, timeoutMs, resultHandler)
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
    return UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).scroll(direction, percent, timeoutMs, resultHandler)
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
    return UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).scroll(direction, percent, speed, timeoutMs, resultHandler)
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
    return UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).fling(direction, timeoutMs, resultHandler)
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
    return UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).fling(direction, speed, timeoutMs, resultHandler)
}


//asserts
/** Assert view.text has value equals [text] */
fun BySelector.hasText(
    text: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).hasText(text, timeoutMs, resultHandler)
}

/** Assert view.text value matching [textMatcher] */
fun BySelector.hasText(
    textMatcher: Matcher<String>,
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).hasText( textMatcher,timeoutMs, resultHandler)
}

/** Assert view.text value contains substring [textSubstring] */
fun BySelector.textContains(
    textSubstring: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).textContains(textSubstring, timeoutMs, resultHandler)
}

/** Assert view.contentDescription has value equals [contentDesc] */
fun BySelector.hasContentDescription(
    contentDesc: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).hasContentDescription(contentDesc, timeoutMs, resultHandler)
}

/** Assert view.contentDescription value matching [contentDescMatcher] */
fun BySelector.hasContentDescription(
    contentDescMatcher: Matcher<String>,
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).hasContentDescription(contentDescMatcher, timeoutMs, resultHandler)
}

/** Assert view.contentDescription value contains substring [contentDescSubstring] */
fun BySelector.contentDescriptionContains(
    contentDescSubstring: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).contentDescriptionContains(contentDescSubstring, timeoutMs, resultHandler)
}

/** Assert this object is checkable. */
fun BySelector.isCheckable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).isCheckable(timeoutMs, resultHandler)
}

/** Assert this object is not checkable. */
fun BySelector.isNotCheckable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).isNotCheckable(timeoutMs, resultHandler)
}

/** Assert this object is checked. */
fun BySelector.isChecked(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).isChecked(timeoutMs, resultHandler)
}

/** Assert this object is not checked. */
fun BySelector.isNotChecked(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).isNotChecked(timeoutMs, resultHandler)
}

/** Assert this object is clickable. */
fun BySelector.isClickable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).isClickable(timeoutMs, resultHandler)
}

/** Assert this object is not clickable. */
fun BySelector.isNotClickable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).isNotClickable(timeoutMs, resultHandler)
}

/** Assert this object is enabled. */
fun BySelector.isEnabled(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).isEnabled(timeoutMs, resultHandler)
}

/** Assert this object is not enabled. */
fun BySelector.isNotEnabled(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).isNotEnabled(timeoutMs, resultHandler)
}

/** Assert this object is focusable. */
fun BySelector.isFocusable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).isFocusable(timeoutMs, resultHandler)
}

/** Assert this object is not focusable. */
fun BySelector.isNotFocusable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).isNotFocusable(timeoutMs, resultHandler)
}

/** Assert this object is focused. */
fun BySelector.isFocused(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).isFocused(timeoutMs, resultHandler)
}

/** Assert this object is not focused. */
fun BySelector.isNotFocused(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).isNotFocused(timeoutMs, resultHandler)
}

/** Assert this object is longClickable. */
fun BySelector.isLongClickable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).isLongClickable(timeoutMs, resultHandler)
}

/** Assert this object is not longClickable. */
fun BySelector.isNotLongClickable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).isNotLongClickable(timeoutMs, resultHandler)
}

/** Assert this object is scrollable. */
fun BySelector.isScrollable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).isScrollable(timeoutMs, resultHandler)
}

/** Assert this object is not scrollable. */
fun BySelector.isNotScrollable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).isNotScrollable(timeoutMs, resultHandler)
}

/** Assert this object is selected. */
fun BySelector.isSelected(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).isSelected(timeoutMs, resultHandler)
}

/** Assert this object is not selected. */
fun BySelector.isNotSelected(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).isNotSelected(timeoutMs, resultHandler)
}

/**
 * Execute custom assertion on UiObject2
 * if [assertBlock] returns true, the assertion will be executed successfully without any exception
 * otherwise if it returns false during [timeoutMs] the exception will be thrown
 */
fun BySelector.assertThat(
    assertBlock: UiObject2.() -> Boolean,
    assertionDescription: String = "NO_DESCRIPTION_SPECIFIED",
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).assertThat(assertBlock, assertionDescription, timeoutMs, resultHandler)
}

/**
 * Execute custom action on UiObject2 during [timeoutMs]
 * if [actionBlock] throws an exception during [timeoutMs] the exception will be thrown
 */
fun BySelector.perform(
    actionBlock: UiObject2.() -> Unit,
    actionDescription: String = "NO_DESCRIPTION_SPECIFIED",
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UltronUiObject2({ uiDevice.findObject(this) }, this.toString()).perform(actionBlock, actionDescription, timeoutMs, resultHandler)
}