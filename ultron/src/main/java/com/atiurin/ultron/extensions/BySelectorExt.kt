package com.atiurin.ultron.extensions

import android.graphics.Point
import android.graphics.Rect
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiObject2
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.config.UltronConfig.UiAutomator.Companion.uiDevice
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperationResult
import com.atiurin.ultron.core.uiautomator.uiobject.UiAutomatorUiSelectorOperation
import com.atiurin.ultron.core.uiautomator.uiobject.UiObjectOperations
import com.atiurin.ultron.core.uiautomator.uiobject2.*
import org.hamcrest.Matcher

fun BySelector.isSuccess(
    action: BySelector.() -> Unit
): Boolean {
    return this.methodToBoolean(action)
}

/** Returns this object's parent, or null if it has no parent. */
fun BySelector.getParent(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): UiObject2? {
    return UiObject2Operation.getParent({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/**
 * @return Returns a collection of the child elements directly under this object. Empty list if no child exist
 * */
fun BySelector.getChildren(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): List<UiObject2> {
    return UiObject2Operation.getChildren({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Returns the number of child elements directly under this object, 0 if it has no child*/
fun BySelector.getChildCount(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): Int {
    return UiObject2Operation.getChildCount({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Searches all elements under this object and returns the first object to match the criteria,
 * or null if no matching objects are found.
 */
fun BySelector.findObject(
    bySelector: BySelector,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): UiObject2? {
    return UiObject2Operation.findObject(bySelector,{ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Searches all elements under this object and returns all objects that match the criteria. */
fun BySelector.findObjects(
    bySelector: BySelector,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): List<UiObject2> {
    return UiObject2Operation.findObjects(bySelector,{ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}
/** Clicks on this object. */
fun BySelector.click(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UiObject2Operation.click({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Performs a click on this object that lasts for {@code duration} milliseconds. */
fun BySelector.click(
    duration: Long,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UiObject2Operation.click(duration, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Performs a long click on this object. */
fun BySelector.longClick(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UiObject2Operation.longClick({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Clears the text content if this object is an editable field. */
fun BySelector.clear(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    UiObject2Operation.clear({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/**
 * @return view text
 */
fun BySelector.getText(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): String {
    return UiObject2Operation.getText({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Returns the package name of the app that this object belongs to. */
fun BySelector.getApplicationPackage(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): String {
    return UiObject2Operation.getApplicationPackage({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Returns the visible bounds of this object in screen coordinates. */
fun BySelector.getVisibleBounds(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): Rect {
    return UiObject2Operation.getVisibleBounds({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Returns a point in the center of the visible bounds of this object. */
fun BySelector.getVisibleCenter(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): Point {
    return UiObject2Operation.getVisibleCenter({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Returns the fully qualified resource name for this object's id.  */
fun BySelector.getResourceName(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): String {
    return UiObject2Operation.getResourceName({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Returns the class name of the underlying [android.view.View] represented by this object.
 */
fun BySelector.getClassName(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
): String {
    return UiObject2Operation.getClassName({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Returns the content description for this object. */
fun BySelector.getContentDescription(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) : String {
    return UiObject2Operation.getContentDescription({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Add the text content if this object is an editable field. */
fun BySelector.addText(
    text: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    return UiObject2Operation.addText(text, { uiDevice.findObject(this) }, this.toString(),  timeoutMs, resultHandler)
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
    return UiObject2Operation.legacySetText(text, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Sets the text content if this object is an editable field. */
fun BySelector.replaceText(
    text: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
) {
    return UiObject2Operation.replaceText(text, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
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
    UiObject2Operation.drag(dest, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
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
    UiObject2Operation.drag(dest, speed, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
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
    UiObject2Operation.pinchClose(percent, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
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
    UiObject2Operation.pinchClose(percent, speed, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
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
    UiObject2Operation.pinchOpen(percent, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
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
    UiObject2Operation.pinchOpen(percent, speed, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
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
    UiObject2Operation.swipe(direction, percent, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
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
    UiObject2Operation.swipe(direction, percent, speed, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
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
    return UiObject2Operation.scroll(direction, percent, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
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
    return UiObject2Operation.scroll(direction, percent, speed, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
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
    return UiObject2Operation.fling(direction, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
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
    return UiObject2Operation.fling(direction, speed, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}


//asserts
/** Assert view.text has value equals [text] */
fun BySelector.hasText(
    text: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.hasText(text, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert view.text value matching [textMatcher] */
fun BySelector.hasText(
    textMatcher: Matcher<String>,
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.hasText( textMatcher, { uiDevice.findObject(this) }, this.toString(),timeoutMs, resultHandler)
}

/** Assert view.text value contains substring [textSubstring] */
fun BySelector.textContains(
    textSubstring: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.textContains(textSubstring, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert view.contentDescription has value equals [contentDesc] */
fun BySelector.hasContentDescription(
    contentDesc: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.hasContentDescription(contentDesc, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert view.contentDescription value matching [contentDescMatcher] */
fun BySelector.hasContentDescription(
    contentDescMatcher: Matcher<String>,
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.hasContentDescription(contentDescMatcher, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert view.contentDescription value contains substring [contentDescSubstring] */
fun BySelector.contentDescriptionContains(
    contentDescSubstring: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.contentDescriptionContains(contentDescSubstring, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert this object is checkable. */
fun BySelector.isCheckable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.isCheckable({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert this object is not checkable. */
fun BySelector.isNotCheckable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.isNotCheckable({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert this object is checked. */
fun BySelector.isChecked(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.isChecked({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert this object is not checked. */
fun BySelector.isNotChecked(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.isNotChecked({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert this object is clickable. */
fun BySelector.isClickable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.isClickable({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert this object is not clickable. */
fun BySelector.isNotClickable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.isNotClickable({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert this object is enabled. */
fun BySelector.isEnabled(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.isEnabled({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert this object is not enabled. */
fun BySelector.isNotEnabled(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.isNotEnabled({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert this object is focusable. */
fun BySelector.isFocusable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.isFocusable({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert this object is not focusable. */
fun BySelector.isNotFocusable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.isNotFocusable({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert this object is focused. */
fun BySelector.isFocused(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.isFocused({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert this object is not focused. */
fun BySelector.isNotFocused(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.isNotFocused({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert this object is longClickable. */
fun BySelector.isLongClickable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.isLongClickable({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert this object is not longClickable. */
fun BySelector.isNotLongClickable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.isNotLongClickable({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert this object is scrollable. */
fun BySelector.isScrollable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.isScrollable({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert this object is not scrollable. */
fun BySelector.isNotScrollable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.isNotScrollable({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert this object is selected. */
fun BySelector.isSelected(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.isSelected({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}

/** Assert this object is not selected. */
fun BySelector.isNotSelected(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
) {
    UiObject2Operation.isNotSelected({ uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
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
    UiObject2Operation.assertThat(assertBlock, assertionDescription, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
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
    UiObject2Operation.perform(actionBlock, actionDescription, { uiDevice.findObject(this) }, this.toString(), timeoutMs, resultHandler)
}