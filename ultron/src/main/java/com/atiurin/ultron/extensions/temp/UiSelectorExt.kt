package com.atiurin.ultron.extensions

import android.graphics.Rect
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.config.UltronConfig.UiAutomator.Companion.uiDevice
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperationResult
import com.atiurin.ultron.core.uiautomator.uiobject.UiAutomatorUiSelectorOperation
import com.atiurin.ultron.core.uiautomator.uiobject.UltronUiObject
import org.hamcrest.Matcher

/**
 * Creates a new UiObject for a child view that is under the present UiObject.
 *
 * @param uiSelector for child view to match
 * @return a new [UltronUiObject] representing the child view
 * @since API Level 16
 */
fun UiSelector.getChild(
    uiSelector: UiSelector,
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
): UltronUiObject {
    val uiObject = UltronUiObject({uiDevice.findObject(this)}, this.toString()).getChild(uiSelector, timeoutMs, resultHandler)
    return UltronUiObject({uiObject}, "Child of $this")
}

/**
 * Creates a new UiObject for a sibling view or a child of the sibling view,
 * relative to the present UiObject.
 *
 * @param uiSelector for a sibling view or children of the sibling view
 * @return a new [UltronUiObject] representing the matched view
 * @since API Level 16
 */
fun UiSelector.getFromParent(
    uiSelector: UiSelector,
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
): UltronUiObject {
    val uiObject = UltronUiObject({uiDevice.findObject(this)}, this.toString()).getFromParent(uiSelector,   timeoutMs, resultHandler)
    return  UltronUiObject( {uiObject}, "Sibling or a child of $this with uiSelector = '$uiSelector'")
}

/**
 * Counts the child views immediately under the present UiObject.
 *
 * @return the count of child views.
 * @since API Level 16
 */
fun UiSelector.getFromParent(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
): Int {
    return UltronUiObject({uiDevice.findObject(this)}, this.toString()).getChildCount( timeoutMs, resultHandler)
}

/**
 * Retrieves the <code>className</code> property of the UI element.
 *
 * @return class name of the current node represented by this UiObject
 * @since API Level 18
 */
fun UiSelector.getClassName(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
): String? {
    return UltronUiObject({uiDevice.findObject(this)}, this.toString()).getClassName( timeoutMs, resultHandler)
}

/**
 * Reads the <code>content_desc</code> property of the UI element
 *
 * @return value of node attribute "content_desc"
 * @since API Level 16
 */
fun UiSelector.getContentDescription(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
): String? {
    return UltronUiObject({uiDevice.findObject(this)}, this.toString()).getContentDescription( timeoutMs, resultHandler)
}

fun UiSelector.getPackageName(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
): String? {
    return UltronUiObject({uiDevice.findObject(this)}, this.toString()).getPackageName( timeoutMs, resultHandler)
}

/**
 * Finds the visible bounds of a partially visible UI element
 * @return Rect containing visible bounds
 */
fun UiSelector.getVisibleBounds(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
): Rect? {
    return UltronUiObject({uiDevice.findObject(this)}, this.toString()).getVisibleBounds( timeoutMs, resultHandler)
}

/**
 * Returns the view's <code>bounds</code> property. See {@link #getVisibleBounds()}
 *
 * @return Rect
 * @since API Level 16
 */
fun UiSelector.getBounds(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
): Rect? {
    return UltronUiObject({uiDevice.findObject(this)}, this.toString()).getBounds( timeoutMs, resultHandler)
}

/**
 * Reads the <code>text</code> property of the UI element
 *
 * @return text value of the current node represented by this UiObject
 * @since API Level 16
 */
fun UiSelector.getText(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
): String? {
    return UltronUiObject({uiDevice.findObject(this)}, this.toString()).getText( timeoutMs, resultHandler)
}

/**
 * Clears the existing text contents in an editable field.
 *
 * When you call this method, the method sets focus on the editable field, selects all of its
 * existing content, and clears it by sending a DELETE key press
 * @since API Level 16
 */
fun UiSelector.clearTextField(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).clearTextField( timeoutMs, resultHandler)
}

fun UiSelector.replaceText(
    text: String,
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).replaceText(text,   timeoutMs, resultHandler)
}

/**
 * Set the text content by sending individual key codes.
 */
fun UiSelector.legacySetText(
    text: String,
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).legacySetText(text,   timeoutMs, resultHandler)
}
/**
 * Performs a click at the center of the visible bounds of the UI element represented
 * by this UiObject.
 * @since API Level 16
 */
fun UiSelector.click(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).click( timeoutMs, resultHandler)
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
fun UiSelector.clickAndWaitForNewWindow(
    waitWindowTimeout: Long = 5500,//default uiautomator timeout value
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).clickAndWaitForNewWindow(waitWindowTimeout,   timeoutMs, resultHandler)
}

/**
 * Clicks the top and left corner of the UI element
 * @since API Level 16
 */
fun UiSelector.clickTopLeft(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).clickTopLeft( timeoutMs, resultHandler)
}

/**
 * Clicks the bottom and right corner of the UI element
 * @since API Level 16
 */
fun UiSelector.clickBottomRight(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).clickBottomRight( timeoutMs, resultHandler)
}

/**
 * Long clicks the center of the visible bounds of the UI element
 * @since API Level 16
 */
fun UiSelector.longClick(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).longClick( timeoutMs, resultHandler)
}

/**
 * Long clicks on the top and left corner of the UI element
 * @since API Level 16
 */
fun UiSelector.longClickTopLeft(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).longClickTopLeft( timeoutMs, resultHandler)
}

/**
 * Long clicks bottom and right corner of the UI element
 * @since API Level 16
 */
fun UiSelector.longClickBottomRight(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).longClickBottomRight( timeoutMs, resultHandler)
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
fun UiSelector.dragTo(
    destObj: UiObject,
    steps: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).dragTo(destObj, steps,  timeoutMs, resultHandler)
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
fun UiSelector.dragTo(
    destX: Int,
    destY: Int,
    steps: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).dragTo(destX, destY, steps,   timeoutMs, resultHandler)
}

/**
 * Performs the swipe up action on the UiObject.
 *
 * @param steps indicates the number of injected move steps into the system. Steps are
 * injected about 5ms apart. So a 100 steps may take about 1/2 second to complete.
 * @since API Level 16
 */
fun UiSelector.swipeUp(
    steps: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).swipeUp(steps,  timeoutMs, resultHandler)
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
fun UiSelector.swipeDown(
    steps: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).swipeDown(steps,  timeoutMs, resultHandler)
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
fun UiSelector.swipeLeft(
    steps: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).swipeLeft(steps,  timeoutMs, resultHandler)
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
fun UiSelector.swipeRight(
    steps: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).swipeRight(steps,   timeoutMs, resultHandler)
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
fun UiSelector.pinchOut(
    percent: Int,
    steps: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).pinchOut(percent, steps,  timeoutMs, resultHandler)
}

/**
 * Performs a two-pointer gesture, where each pointer moves diagonally
 * toward the other, from the edges to the center of this UiObject .
 * @param percent percentage of the object's diagonal length for the pinch gesture
 * @param steps the number of steps for the gesture. Steps are injected
 * about 5 milliseconds apart, so 100 steps may take around 0.5 seconds to complete.
 * @since API Level 18
 */
fun UiSelector.pinchIn(
    percent: Int,
    steps: Int,
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).pinchIn(percent, steps,   timeoutMs, resultHandler)
}

/**
 * Check if view exists.
 * @since API Level 16
 */
fun UiSelector.exists(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).exists( timeoutMs, resultHandler)
}

/**
 * Check if view not exists.
 * @since API Level 16
 */
fun UiSelector.notExists(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).notExists( timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>checkable</code> property is currently true.
 * @since API Level 16
 */
fun UiSelector.isCheckable(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).isCheckable( timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>checkable</code> property is currently false.
 * @since API Level 16
 */
fun UiSelector.isNotCheckable(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).isNotCheckable( timeoutMs, resultHandler)
}

/**
 * Check if the UI element's <code>checked</code> property is currently true
 * @since API Level 16
 */
fun UiSelector.isChecked(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).isChecked( timeoutMs, resultHandler)
}

/**
 * Check if the UI element's <code>checked</code> property is currently false
 * @since API Level 16
 */
fun UiSelector.isNotChecked(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).isNotChecked( timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>clickable</code> property is currently true.
 * @since API Level 16
 */
fun UiSelector.isClickable(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).isClickable( timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>clickable</code> property is currently false.
 * @since API Level 16
 */
fun UiSelector.isNotClickable(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).isNotClickable( timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>enabled</code> property is currently true.
 * @since API Level 16
 */
fun UiSelector.isEnabled(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).isEnabled( timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>enabled</code> property is currently false.
 * @since API Level 16
 */
fun UiSelector.isNotEnabled(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).isNotEnabled( timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>focusable</code> property is currently true
 * @since API Level 16
 */
fun UiSelector.isFocusable(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).isFocusable( timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>focusable</code> property is currently false.
 * @since API Level 16
 */
fun UiSelector.isNotFocusable(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).isNotFocusable( timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>focused</code> property is currently true.
 * @since API Level 16
 */
fun UiSelector.isFocused(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).isFocused( timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>focused</code> property is currently false.
 * @since API Level 16
 */
fun UiSelector.isNotFocused(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).isNotFocused( timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>long-clickable</code> property is currently true.
 * @since API Level 16
 */
fun UiSelector.isLongClickable(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).isLongClickable( timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>long-clickable</code> property is currently false.
 * @since API Level 16
 */
fun UiSelector.isNotLongClickable(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).isNotLongClickable( timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>scrollable</code> property is currently true.
 * @since API Level 16
 */
fun UiSelector.isScrollable(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).isScrollable( timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>scrollable</code> property is currently false.
 * @since API Level 16
 */
fun UiSelector.isNotScrollable(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).isNotScrollable( timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>selected</code> property is currently true.
 * @since API Level 16
 */
fun UiSelector.isSelected(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).isSelected( timeoutMs, resultHandler)
}

/**
 * Checks if the UI element's <code>selected</code> property is currently false.
 * @since API Level 16
 */
fun UiSelector.isNotSelected(
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).isNotSelected( timeoutMs, resultHandler)
}

/** Assert view.text has value equals [text] */
fun UiSelector.hasText(
    text: String,
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).hasText(text,  timeoutMs, resultHandler)
}

/** Assert view.text value matching [textMatcher] */
fun UiSelector.hasText(
    textMatcher: Matcher<String>,
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).hasText(textMatcher,   timeoutMs, resultHandler)
}

/** Assert view.text value contains substring [textSubstring] */
fun UiSelector.textContains(
    textSubstring: String,
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).textContains(textSubstring,  timeoutMs, resultHandler)
}

/** Assert view.contentDescription has value equals [contentDesc] */
fun UiSelector.hasContentDescription(
    contentDesc: String,
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).hasContentDescription(contentDesc,   timeoutMs, resultHandler)
}

/** Assert view.contentDescription value matching [contentDescMatcher] */
fun UiSelector.hasContentDescription(
    contentDescMatcher: Matcher<String>,
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).hasContentDescription(contentDescMatcher,   timeoutMs, resultHandler)
}

/** Assert view.contentDescription value contains substring [contentDescSubstring] */
fun UiSelector.contentDescriptionContains(
    contentDescSubstring: String,
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).contentDescriptionContains(contentDescSubstring,  timeoutMs, resultHandler)
}

/**
 * Execute custom assertion on UiObject
 * if [assertBlock] returns true, the assertion will be executed successfully without any exception
 * otherwise if it returns false during [timeoutMs] the exception will be thrown
 */
fun UiSelector.assertThat(
    assertBlock: UiObject.() -> Boolean,
    assertionDescription: String = "NO_DESCRIPTION_SPECIFIED",
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).assertThat(assertBlock, assertionDescription,  timeoutMs, resultHandler)
}

/**
 * Execute custom action on UiObject during [timeoutMs]
 * if [actionBlock] returns false or throws an exception during [timeoutMs] the exception will be thrown
 */
fun UiSelector.perform(
    actionBlock: UiObject.() -> Boolean,
    actionDescription: String = "NO_DESCRIPTION_SPECIFIED",
    timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler
) {
    UltronUiObject({uiDevice.findObject(this)}, this.toString()).perform(actionBlock, actionDescription,  timeoutMs, resultHandler)
}