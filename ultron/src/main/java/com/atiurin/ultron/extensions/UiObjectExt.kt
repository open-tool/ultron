package com.atiurin.ultron.extensions

import android.graphics.Rect
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperationResult
import com.atiurin.ultron.core.uiautomator.uiobject.UiAutomatorUiSelectorOperation
import com.atiurin.ultron.core.uiautomator.uiobject.UiObjectOperations
import org.hamcrest.Matcher

/**
 * Creates a new UiObject for a child view that is under the present UiObject.
 *
 * @param uiSelector for child view to match
 * @return a new UiObject representing the child view
 * @since API Level 16
 */
fun UiObject.getChild(
    uiSelector: UiSelector,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
): UiObject {
    return UiObjectOperations.getChild(uiSelector, { this }, this.toString(),  timeoutMs, resultHandler)
}

/**
 * Creates a new UiObject for a sibling view or a child of the sibling view,
 * relative to the present UiObject.
 *
 * @param uiSelector for a sibling view or children of the sibling view
 * @return a new UiObject representing the matched view
 * @since API Level 16
 */
fun UiObject.getFromParent(
    uiSelector: UiSelector,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
): UiObject {
    return UiObjectOperations.getFromParent(uiSelector, { this }, this.toString(),  timeoutMs, resultHandler)
}

/**
 * Counts the child views immediately under the present UiObject.
 *
 * @return the count of child views.
 * @since API Level 16
 */
fun UiObject.getFromParent(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
): Int {
    return UiObjectOperations.getChildCount({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Retrieves the <code>className</code> property of the UI element.
 *
 * @return class name of the current node represented by this UiObject
 * @since API Level 18
 */
fun UiObject.getClassName(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
): String {
    return UiObjectOperations.getClassName({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Reads the <code>content_desc</code> property of the UI element
 *
 * @return value of node attribute "content_desc"
 * @since API Level 16
 */
fun UiObject.getContentDescription(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
): String {
    return UiObjectOperations.getContentDescription({ this }, this.toString(), timeoutMs, resultHandler)
}

fun UiObject.getPackageName(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
): String {
    return UiObjectOperations.getPackageName({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Finds the visible bounds of a partially visible UI element
 * @return Rect containing visible bounds
 */
fun UiObject.getVisibleBounds(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
): Rect {
    return UiObjectOperations.getVisibleBounds({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Returns the view's <code>bounds</code> property. See {@link #getVisibleBounds()}
 *
 * @return Rect
 * @since API Level 16
 */
fun UiObject.getBounds(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
): Rect {
    return UiObjectOperations.getBounds({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Reads the <code>text</code> property of the UI element
 *
 * @return text value of the current node represented by this UiObject
 * @since API Level 16
 */
fun UiObject.getText(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
): String {
    return UiObjectOperations.getText({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Clears the existing text contents in an editable field.
 *
 * When you call this method, the method sets focus on the editable field, selects all of its
 * existing content, and clears it by sending a DELETE key press
 * @since API Level 16
 */
fun UiObject.clearTextField(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.clearTextField({ this }, this.toString(), timeoutMs, resultHandler)
}

fun UiObject.replaceText(
    text: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.replaceText(text, { this }, this.toString(),  timeoutMs, resultHandler)
}

/**
 * Set the text content by sending individual key codes.
 */
fun UiObject.legacySetText(
    text: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.legacySetText(text, { this }, this.toString(),  timeoutMs, resultHandler)
}
/**
 * Performs a click at the center of the visible bounds of the UI element represented
 * by this UiObject.
 * @since API Level 16
 */
fun UiObject.click(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.click({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Performs a click at the center of the visible bounds of the UI element represented
 * by this UiObject and waits for window transitions.
 *
 * This method differ from {@link UiObject#click()} only in that this method waits for a
 * a new window transition as a result of the click. Some examples of a window transition:
 * <li>launching a new activity</li>
 * <li>bringing up a pop-up menu</li>
 * <li>bringing up a dialog</li>
 *
 * @param waitWindowTimeout timeout before giving up on waiting for a new window
 * @return true if the event was triggered, else false
 * @since API Level 16
 */
fun UiObject.clickAndWaitForNewWindow(
    waitWindowTimeout: Long = 5500,//default uiautomator timeout value
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.clickAndWaitForNewWindow(waitWindowTimeout, { this }, this.toString(),  timeoutMs, resultHandler)
}

/**
 * Clicks the top and left corner of the UI element
 * @since API Level 16
 */
fun UiObject.clickTopLeft(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.clickTopLeft({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Clicks the bottom and right corner of the UI element
 * @since API Level 16
 */
fun UiObject.clickBottomRight(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.clickBottomRight({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Long clicks the center of the visible bounds of the UI element
 * @since API Level 16
 */
fun UiObject.longClick(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.longClick({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Long clicks on the top and left corner of the UI element
 * @since API Level 16
 */
fun UiObject.longClickTopLeft(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.longClickTopLeft({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Long clicks bottom and right corner of the UI element
 * @since API Level 16
 */
fun UiObject.longClickBottomRight(
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.longClickBottomRight({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Drags this object to a destination UiObject.
 * The number of steps specified in your input parameter can influence the
 * drag speed, and varying speeds may impact the results. Consider
 * evaluating different speeds when using this method in your tests.
 *
 * @param destObj the destination UiObject.
 * @param steps usually 40 steps. You can increase or decrease the steps to change the speed.
 * @since API Level 18
 */
fun UiObject.dragTo(
    destObj: UiObject,
    steps: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.dragTo(destObj, steps, { this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Drags this object to arbitrary coordinates.
 * The number of steps specified in your input parameter can influence the
 * drag speed, and varying speeds may impact the results. Consider
 * evaluating different speeds when using this method in your tests.
 *
 * @param destX the X-axis coordinate.
 * @param destY the Y-axis coordinate.
 * @param steps usually 40 steps. You can increase or decrease the steps to change the speed.
 * @since API Level 18
 */
fun UiObject.dragTo(
    destX: Int,
    destY: Int,
    steps: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.dragTo(destX, destY, steps, { this }, this.toString(),  timeoutMs, resultHandler)
}

/**
 * Performs the swipe up action on the UiObject.
 *
 * @param steps indicates the number of injected move steps into the system. Steps are
 * injected about 5ms apart. So a 100 steps may take about 1/2 second to complete.
 * @since API Level 16
 */
fun UiObject.swipeUp(
    steps: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.swipeUp(steps, { this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Performs the swipe down action on the UiObject.
 * The swipe gesture can be performed over any surface. The targeted
 * UI element does not need to be scrollable.
 *
 * @param steps indicates the number of injected move steps into the system. Steps are
 * injected about 5ms apart. So a 100 steps may take about 1/2 second to complete.
 * @since API Level 16
 */
fun UiObject.swipeDown(
    steps: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.swipeDown(steps, { this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Performs the swipe left action on the UiObject.
 * The swipe gesture can be performed over any surface. The targeted
 * UI element does not need to be scrollable.
 *
 * @param steps indicates the number of injected move steps into the system. Steps are
 * injected about 5ms apart. So a 100 steps may take about 1/2 second to complete.
 * @since API Level 16
 */
fun UiObject.swipeLeft(
    steps: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.swipeLeft(steps, { this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Performs the swipe right action on the UiObject.
 * The swipe gesture can be performed over any surface. The targeted
 * UI element does not need to be scrollable.
 *
 * @param steps indicates the number of injected move steps into the system. Steps are
 * injected about 5ms apart. So a 100 steps may take about 1/2 second to complete.
 * @since API Level 16
 */
fun UiObject.swipeRight(
    steps: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.swipeRight(steps, { this }, this.toString(),  timeoutMs, resultHandler)
}

/**
 * Performs a two-pointer gesture, where each pointer moves diagonally
 * opposite across the other, from the center out towards the edges of the
 * this UiObject.
 * @param percent percentage of the object's diagonal length for the pinch gesture
 * @param steps the number of steps for the gesture. Steps are injected
 * about 5 milliseconds apart, so 100 steps may take around 0.5 seconds to complete.
 * @since API Level 18
 */
fun UiObject.pinchOut(
    percent: Int,
    steps: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.pinchOut(percent, steps, { this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Performs a two-pointer gesture, where each pointer moves diagonally
 * toward the other, from the edges to the center of this UiObject .
 * @param percent percentage of the object's diagonal length for the pinch gesture
 * @param steps the number of steps for the gesture. Steps are injected
 * about 5 milliseconds apart, so 100 steps may take around 0.5 seconds to complete.
 * @since API Level 18
 */
fun UiObject.pinchIn(
    percent: Int,
    steps: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.pinchIn(percent, steps, { this }, this.toString(),  timeoutMs, resultHandler)
}

/**
 * Check if view exists.
 * @since API Level 16
 */
fun UiObject.exists(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.exists({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Check if view not exists.
 * @since API Level 16
 */
fun UiObject.notExists(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.notExists({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>checkable</code> property is currently true.
 * @since API Level 16
 */
fun UiObject.isCheckable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.isCheckable({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>checkable</code> property is currently false.
 * @since API Level 16
 */
fun UiObject.isNotCheckable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.isNotCheckable({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Check if the UI element's <code>checked</code> property is currently true
 * @since API Level 16
 */
fun UiObject.isChecked(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.isChecked({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Check if the UI element's <code>checked</code> property is currently false
 * @since API Level 16
 */
fun UiObject.isNotChecked(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.isNotChecked({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>clickable</code> property is currently true.
 * @since API Level 16
 */
fun UiObject.isClickable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.isClickable({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>clickable</code> property is currently false.
 * @since API Level 16
 */
fun UiObject.isNotClickable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.isNotClickable({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>enabled</code> property is currently true.
 * @since API Level 16
 */
fun UiObject.isEnabled(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.isEnabled({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>enabled</code> property is currently false.
 * @since API Level 16
 */
fun UiObject.isNotEnabled(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.isNotEnabled({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>focusable</code> property is currently true
 * @since API Level 16
 */
fun UiObject.isFocusable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.isFocusable({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>focusable</code> property is currently false.
 * @since API Level 16
 */
fun UiObject.isNotFocusable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.isNotFocusable({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>focused</code> property is currently true.
 * @since API Level 16
 */
fun UiObject.isFocused(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.isFocused({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>focused</code> property is currently false.
 * @since API Level 16
 */
fun UiObject.isNotFocused(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.isNotFocused({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>long-clickable</code> property is currently true.
 * @since API Level 16
 */
fun UiObject.isLongClickable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.isLongClickable({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>long-clickable</code> property is currently false.
 * @since API Level 16
 */
fun UiObject.isNotLongClickable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.isNotLongClickable({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>scrollable</code> property is currently true.
 * @since API Level 16
 */
fun UiObject.isScrollable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.isScrollable({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>scrollable</code> property is currently false.
 * @since API Level 16
 */
fun UiObject.isNotScrollable(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.isNotScrollable({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>selected</code> property is currently true.
 * @since API Level 16
 */
fun UiObject.isSelected(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.isSelected({ this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>selected</code> property is currently false.
 * @since API Level 16
 */
fun UiObject.isNotSelected(
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.isNotSelected({ this }, this.toString(), timeoutMs, resultHandler)
}

/** Assert view.text has value equals [text] */
fun UiObject.hasText(
    text: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.hasText(text, { this }, this.toString(), timeoutMs, resultHandler)
}

/** Assert view.text value matching [textMatcher] */
fun UiObject.hasText(
    textMatcher: Matcher<String>,
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.hasText(textMatcher, { this }, this.toString(),  timeoutMs, resultHandler)
}

/** Assert view.text value contains substring [textSubstring] */
fun UiObject.textContains(
    textSubstring: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.textContains(textSubstring, { this }, this.toString(), timeoutMs, resultHandler)
}

/** Assert view.contentDescription has value equals [contentDesc] */
fun UiObject.hasContentDescription(
    contentDesc: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.hasContentDescription(contentDesc, { this }, this.toString(),  timeoutMs, resultHandler)
}

/** Assert view.contentDescription value matching [contentDescMatcher] */
fun UiObject.hasContentDescription(
    contentDescMatcher: Matcher<String>,
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.hasContentDescription(contentDescMatcher, { this }, this.toString(),  timeoutMs, resultHandler)
}

/** Assert view.contentDescription value contains substring [contentDescSubstring] */
fun UiObject.contentDescriptionContains(
    contentDescSubstring: String,
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.contentDescriptionContains(contentDescSubstring, { this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Execute custom assertion on UiObject
 * if [assertBlock] returns true, the assertion will be executed successfully without any exception
 * otherwise if it returns false during [timeoutMs] the exception will be thrown
 */
fun UiObject.assertThat(
    assertBlock: UiObject.() -> Boolean,
    assertionDescription: String = "NO_DESCRIPTION_SPECIFIED",
    timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.assertThat(assertBlock, assertionDescription, { this }, this.toString(), timeoutMs, resultHandler)
}

/**
 * Execute custom action on UiObject during [timeoutMs]
 * if [actionBlock] returns false or throws an exception during [timeoutMs] the exception will be thrown
 */
fun UiObject.perform(
    actionBlock: UiObject.() -> Boolean,
    actionDescription: String = "NO_DESCRIPTION_SPECIFIED",
    timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UiObjectOperations.perform(actionBlock, actionDescription, { this }, this.toString(), timeoutMs, resultHandler)
}