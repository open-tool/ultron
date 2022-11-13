package com.atiurin.ultron.core.uiautomator.uiobject

import android.graphics.Rect
import androidx.annotation.IntegerRes
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import com.atiurin.ultron.core.common.CommonOperationType
import com.atiurin.ultron.core.common.UltronOperationType
import com.atiurin.ultron.core.common.assertion.DefaultOperationAssertion
import com.atiurin.ultron.core.common.assertion.EmptyOperationAssertion
import com.atiurin.ultron.core.common.assertion.OperationAssertion
import com.atiurin.ultron.core.compose.nodeinteraction.UltronComposeSemanticsNodeInteraction
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.uiautomator.*
import com.atiurin.ultron.exceptions.UltronAssertionException
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.exceptions.UltronOperationException
import com.atiurin.ultron.exceptions.UltronWrapperException
import com.atiurin.ultron.listeners.setListenersState
import com.atiurin.ultron.utils.getTargetResourceName
import org.hamcrest.Matcher
import org.hamcrest.Matchers

class UltronUiObject internal constructor(
    private val uiObjectProviderBlock: () -> UiObject,
    private val selectorDesc: String,
    private val resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit = UltronConfig.UiAutomator.UiObjectConfig.resultHandler,
    private val timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    private val assertion: OperationAssertion = EmptyOperationAssertion()
) {
    fun isSuccess(action: UltronUiObject.() -> Unit): Boolean = runCatching { action() }.isSuccess

    fun withResultHandler(resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit): UltronUiObject =
        UltronUiObject(this.uiObjectProviderBlock, this.selectorDesc, resultHandler, this.timeoutMs, assertion)

    fun withTimeout(timeoutMs: Long): UltronUiObject = UltronUiObject(this.uiObjectProviderBlock, this.selectorDesc, this.resultHandler, timeoutMs, this.assertion)
    fun withAssertion(assertion: OperationAssertion) = UltronUiObject(this.uiObjectProviderBlock, this.selectorDesc, this.resultHandler, this.timeoutMs, assertion)
    fun withAssertion(name: String = "", isListened: Boolean = false, block: () -> Unit) =
        UltronUiObject(
            this.uiObjectProviderBlock, this.selectorDesc, this.resultHandler, this.timeoutMs,
            DefaultOperationAssertion(name, block.setListenersState(isListened))
        )

    fun getChild(uiSelector: UiSelector): UltronUiObject {
        var uiObject: UiObject? = null
        executeOperation(
            operationBlock = {
                uiObject = uiObjectProviderBlock().getChild(uiSelector)
                uiObject != null
            },
            name = "GetChild of $selectorDesc with uiSelector $uiSelector",
            type = UiAutomatorActionType.GET_CHILD,
            description = "UiObject action '${UiAutomatorActionType.GET_CHILD}' of $selectorDesc with uiSelector $uiSelector during $timeoutMs ms"
        )
        return if (uiObject != null) UltronUiObject(
            { uiObject!! },
            "child of '$selectorDesc' with UiSelector '$uiSelector'"
        )
        else throw UltronException("Couldn't build selector of '$selectorDesc' with child UiSelector '$uiSelector' ")
    }

    /**
     * Creates a new UiObject for a sibling view or a child of the sibling view,
     * relative to the present UiObject.
     *
     * @param selector for a sibling view or children of the sibling view
     * @return a new UiObject representing the matched view
     * @throws UiObjectNotFoundException
     * @since API Level 16
     */
    fun getFromParent(uiSelector: UiSelector): UltronUiObject {
        var uiObject: UiObject? = null
        executeOperation(
            operationBlock = {
                uiObject = uiObjectProviderBlock().getFromParent(uiSelector)
                uiObject != null
            },
            name = "GetFromParent of $selectorDesc with uiSelector $uiSelector",
            type = UiAutomatorActionType.GET_FROM_PARENT,
            description = "UiObject action '${UiAutomatorActionType.GET_FROM_PARENT}' of $selectorDesc with uiSelector $uiSelector during $timeoutMs ms"
        )
        return if (uiObject != null) UltronUiObject(
            { uiObject!! },
            "'$selectorDesc' with parent UiSelector '$uiSelector'"
        )
        else throw UltronException("Couldn't build selector of '$selectorDesc' with parent UiSelector '$uiSelector' ")
    }

    fun getChildCount(): Int {
        var count = 0
        executeOperation(
            operationBlock = {
                count = uiObjectProviderBlock().childCount
                true
            },
            name = "GetChildCount of $selectorDesc",
            type = UiAutomatorActionType.GET_CHILD_COUNT,
            description = "UiObject action '${UiAutomatorActionType.GET_CHILD_COUNT}' of $selectorDesc during $timeoutMs ms"
        )
        return count
    }

    fun getClassName(): String? {
        var className: String? = null
        executeOperation(
            operationBlock = {
                className = uiObjectProviderBlock().className
                true
            },
            name = "GetClassName of $selectorDesc",
            type = UiAutomatorActionType.GET_CLASS_NAME,
            description = "UiObject action '${UiAutomatorActionType.GET_CLASS_NAME}' of $selectorDesc during $timeoutMs ms"
        )
        return className
    }

    fun getContentDescription(): String? {
        var contentDesc: String? = null
        executeOperation(
            operationBlock = {
                contentDesc = uiObjectProviderBlock().contentDescription
                true
            },
            name = "GetContentDescription of $selectorDesc",
            type = UiAutomatorActionType.GET_CONTENT_DESCRIPTION,
            description = "UiObject action '${UiAutomatorActionType.GET_CONTENT_DESCRIPTION}' of $selectorDesc during $timeoutMs ms"
        )
        return contentDesc

    }

    fun getPackageName(): String? {
        var packageName: String? = null
        executeOperation(
            operationBlock = {
                packageName = uiObjectProviderBlock().packageName
                true
            },
            name = "GetPackageName of $selectorDesc",
            type = UiAutomatorActionType.GET_APPLICATION_PACKAGE,
            description = "UiObject action '${UiAutomatorActionType.GET_APPLICATION_PACKAGE}' of $selectorDesc during $timeoutMs ms"
        )
        return packageName
    }

    fun getVisibleBounds(): Rect? {
        var bounds: Rect? = null
        executeOperation(
            operationBlock = {
                bounds = uiObjectProviderBlock().visibleBounds
                bounds != null
            },
            name = "GetVisibleBounds of $selectorDesc",
            type = UiAutomatorActionType.GET_VISIBLE_BOUNDS,
            description = "UiObject action '${UiAutomatorActionType.GET_VISIBLE_BOUNDS}' of $selectorDesc during $timeoutMs ms"
        )
        return bounds
    }

    fun getBounds(): Rect? {
        var bounds: Rect? = null
        executeOperation(
            operationBlock = {
                bounds = uiObjectProviderBlock().bounds
                bounds != null
            },
            name = "GetBounds of $selectorDesc",
            type = UiAutomatorActionType.GET_BOUNDS,
            description = "UiObject action '${UiAutomatorActionType.GET_BOUNDS}' of $selectorDesc during $timeoutMs ms"
        )
        return bounds
    }

    fun getText(): String? {
        var text: String? = null
        executeOperation(
            operationBlock = {
                text = uiObjectProviderBlock().text
                true
            },
            name = "GetText of $selectorDesc",
            type = UiAutomatorActionType.GET_TEXT,
            description = "UiObject action '${UiAutomatorActionType.GET_TEXT}' of $selectorDesc during $timeoutMs ms"
        )
        return text
    }

    fun clearTextField() = apply {
        executeOperation(
            operationBlock = {
                uiObjectProviderBlock().clearTextField()
                true
            },
            name = "ClearTextField of $selectorDesc",
            type = UiAutomatorActionType.CLEAR_TEXT,
            description = "UiObject action '${UiAutomatorActionType.CLEAR_TEXT}' of $selectorDesc during $timeoutMs ms"
        )
    }

    fun replaceText(text: String) = apply {
        executeOperation(
            operationBlock = {
                uiObjectProviderBlock().setText(text)
            },
            name = "ReplaceText of $selectorDesc to '$text'",
            type = UiAutomatorActionType.REPLACE_TEXT,
            description = "UiObject action '${UiAutomatorActionType.REPLACE_TEXT}' of $selectorDesc to '$text' during $timeoutMs ms"
        )
    }

    /**
     * Why legacyAddText? Because it actually not replacing the text in textField it adds to the existing one
     * In case you would like to replace text use [replaceText]
     */
    fun legacyAddText(text: String) = apply {
        executeOperation(
            operationBlock = {
                uiObjectProviderBlock().legacySetText(text)
                true
            },
            name = "LegacySetText of $selectorDesc to '$text'",
            type = UiAutomatorActionType.LEGACY_SET_TEXT,
            description = "UiObject action '${UiAutomatorActionType.LEGACY_SET_TEXT}' of $selectorDesc to '$text' during $timeoutMs ms"
        )
    }

    fun click() = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().click() },
            name = "Click to $selectorDesc",
            type = UiAutomatorActionType.CLICK,
            description = "UiObject action '${UiAutomatorActionType.CLICK}' to $selectorDesc during $timeoutMs ms"
        )
    }

    fun clickAndWaitForNewWindow(waitWindowTimeout: Long) = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().clickAndWaitForNewWindow(waitWindowTimeout) },
            name = "ClickAndWaitForNewWindow to $selectorDesc with waitWindowTimeout = $waitWindowTimeout ms",
            type = UiAutomatorActionType.CLICK_AND_WAIT_FOR_NEW_WINDOW,
            description = "UiObject action '${UiAutomatorActionType.CLICK_AND_WAIT_FOR_NEW_WINDOW}' to $selectorDesc  with waitWindowTimeout = $waitWindowTimeout ms during $timeoutMs ms"
        )
    }

    fun clickTopLeft() = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().clickTopLeft() },
            name = "ClickTopLeft to $selectorDesc",
            type = UiAutomatorActionType.CLICK_TOP_LEFT,
            description = "UiObject action '${UiAutomatorActionType.CLICK_TOP_LEFT}' to $selectorDesc during $timeoutMs ms"
        )
    }

    fun clickBottomRight() = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().clickBottomRight() },
            name = "ClickBottomRight to $selectorDesc",
            type = UiAutomatorActionType.CLICK_BOTTOM_RIGHT,
            description = "UiObject action '${UiAutomatorActionType.CLICK_BOTTOM_RIGHT}' to $selectorDesc during $timeoutMs ms"
        )
    }

    fun longClick() = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().longClick() },
            name = "LongClick to $selectorDesc",
            type = UiAutomatorActionType.LONG_CLICK,
            description = "UiObject action '${UiAutomatorActionType.LONG_CLICK}' to $selectorDesc during $timeoutMs ms"
        )
    }

    fun longClickTopLeft() = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().longClickTopLeft() },
            name = "LongClickTopLeft to $selectorDesc",
            type = UiAutomatorActionType.LONG_CLICK_TOP_LEFT,
            description = "UiObject action '${UiAutomatorActionType.LONG_CLICK_TOP_LEFT}' to $selectorDesc during $timeoutMs ms"
        )
    }

    fun longClickBottomRight() = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().longClickBottomRight() },
            name = "LongClickBottomRight to $selectorDesc",
            type = UiAutomatorActionType.LONG_CLICK_BOTTOM_RIGHT,
            description = "UiObject action '${UiAutomatorActionType.LONG_CLICK_BOTTOM_RIGHT}' to $selectorDesc during $timeoutMs ms"
        )
    }

    fun dragTo(destObj: UiObject, steps: Int) = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().dragTo(destObj, steps) },
            name = "DragTo of $selectorDesc destObj ${destObj.selector}  with $steps steps",
            type = UiAutomatorActionType.DRAG,
            description = "UiObject action '${UiAutomatorActionType.DRAG}' of $selectorDesc destObj ${destObj.selector}  with $steps steps during $timeoutMs ms"
        )
    }

    fun dragTo(destX: Int, destY: Int, steps: Int) = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().dragTo(destX, destY, steps) },
            name = "DragTo of $selectorDesc to destX = $destX, destY = $destY with $steps steps",
            type = UiAutomatorActionType.DRAG,
            description = "UiObject action '${UiAutomatorActionType.DRAG}' of $selectorDesc to destX = $destX, destY = $destY with $steps steps during $timeoutMs ms"
        )
    }

    fun swipeUp(steps: Int = 100) = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().swipeUp(steps) },
            name = "SwipeUp of $selectorDesc with $steps steps",
            type = UiAutomatorActionType.SWIPE_UP,
            description = "UiObject action '${UiAutomatorActionType.SWIPE_UP}' of $selectorDesc with $steps steps during $timeoutMs ms"
        )
    }

    fun swipeDown(steps: Int = 100) = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().swipeDown(steps) },
            name = "SwipeDown of $selectorDesc with $steps steps",
            type = UiAutomatorActionType.SWIPE_DOWN,
            description = "UiObject action '${UiAutomatorActionType.SWIPE_DOWN}' of $selectorDesc with $steps steps during $timeoutMs ms"
        )
    }

    fun swipeLeft(steps: Int = 100) = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().swipeLeft(steps) },
            name = "SwipeLeft of $selectorDesc with $steps steps",
            type = UiAutomatorActionType.SWIPE_LEFT,
            description = "UiObject action '${UiAutomatorActionType.SWIPE_LEFT}' of $selectorDesc with $steps steps during $timeoutMs ms"
        )
    }

    fun swipeRight(steps: Int = 100) = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().swipeRight(steps) },
            name = "SwipeRight of $selectorDesc with $steps steps",
            type = UiAutomatorActionType.SWIPE_RIGHT,
            description = "UiObject action '${UiAutomatorActionType.SWIPE_RIGHT}' of $selectorDesc with $steps steps during $timeoutMs ms"
        )
    }

    fun pinchOut(percent: Int, steps: Int) = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().pinchOut(percent, steps) },
            name = "PinchOut of $selectorDesc with $percent% and $steps steps",
            type = UiAutomatorActionType.PINCH_OUT,
            description = "UiObject action '${UiAutomatorActionType.PINCH_OUT}' of $selectorDesc with $percent% and $steps steps steps during $timeoutMs ms"
        )
    }

    fun pinchIn(percent: Int, steps: Int) = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().pinchIn(percent, steps) },
            name = "PinchIn of $selectorDesc with $percent% and $steps steps",
            type = UiAutomatorActionType.PINCH_IN,
            description = "UiObject action '${UiAutomatorActionType.PINCH_IN}' of $selectorDesc with $percent% and $steps steps steps during $timeoutMs ms"
        )
    }

    fun perform(actionBlock: UiObject.() -> Boolean, actionDescription: String) = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().actionBlock() },
            name = "Perform custom action '$actionDescription' on $selectorDesc.",
            type = UiAutomatorActionType.PERFORM,
            description = "UiObject action '${UiAutomatorActionType.PERFORM}' custom action '$actionDescription' on $selectorDesc during $timeoutMs ms"
        )
    }

    //asserts

    fun exists() = apply {
        executeOperation(
            operationBlock = {
                uiObjectProviderBlock().exists()
            },
            name = "Exists of $selectorDesc",
            type = UiAutomatorAssertionType.EXISTS,
            description = "UiObject assertion '${UiAutomatorAssertionType.EXISTS}' of $selectorDesc during $timeoutMs ms"
        )
    }

    fun notExists() = apply {
        executeOperation(
            operationBlock = {
                !uiObjectProviderBlock().exists()
            },
            name = "NotExists of $selectorDesc",
            type = UiAutomatorAssertionType.NOT_EXISTS,
            description = "UiObject assertion '${UiAutomatorAssertionType.NOT_EXISTS}' of $selectorDesc during $timeoutMs ms"
        )
    }

    fun isCheckable() = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().isCheckable },
            name = "IsCheckable of $selectorDesc",
            type = UiAutomatorAssertionType.IS_CHECKABLE,
            description = "UiObject assertion '${UiAutomatorAssertionType.IS_CHECKABLE}' of $selectorDesc during $timeoutMs ms"
        )
    }

    fun isNotCheckable() = apply {
        executeOperation(
            operationBlock = { !uiObjectProviderBlock().isCheckable },
            name = "IsNotCheckable of $selectorDesc",
            type = UiAutomatorAssertionType.IS_NOT_CHECKABLE,
            description = "UiObject assertion '${UiAutomatorAssertionType.IS_NOT_CHECKABLE}' of $selectorDesc during $timeoutMs ms"
        )
    }

    fun isChecked() = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().isChecked },
            name = "IsChecked of $selectorDesc",
            type = UiAutomatorAssertionType.IS_CHECKED,
            description = "UiObject assertion '${UiAutomatorAssertionType.IS_CHECKED}' of $selectorDesc during $timeoutMs ms"
        )
    }

    fun isNotChecked() = apply {
        executeOperation(
            operationBlock = { !uiObjectProviderBlock().isChecked },
            name = "IsNotChecked of $selectorDesc",
            type = UiAutomatorAssertionType.IS_NOT_CHECKED,
            description = "UiObject assertion '${UiAutomatorAssertionType.IS_NOT_CHECKED}' of $selectorDesc during $timeoutMs ms"
        )
    }

    fun isClickable() = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().isClickable },
            name = "IsClickable of $selectorDesc",
            type = UiAutomatorAssertionType.IS_CLICKABLE,
            description = "UiObject assertion '${UiAutomatorAssertionType.IS_CLICKABLE}' of $selectorDesc during $timeoutMs ms"
        )
    }

    fun isNotClickable() = apply {
        executeOperation(
            operationBlock = { !uiObjectProviderBlock().isClickable },
            name = "IsNotClickable of $selectorDesc",
            type = UiAutomatorAssertionType.IS_NOT_CLICKABLE,
            description = "UiObject assertion '${UiAutomatorAssertionType.IS_NOT_CLICKABLE}' of $selectorDesc during $timeoutMs ms"
        )
    }

    fun isEnabled() = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().isEnabled },
            name = "IsEnabled of $selectorDesc",
            type = UiAutomatorAssertionType.IS_ENABLED,
            description = "UiObject assertion '${UiAutomatorAssertionType.IS_ENABLED}' of $selectorDesc during $timeoutMs ms"
        )
    }

    fun isNotEnabled() = apply {
        executeOperation(
            operationBlock = { !uiObjectProviderBlock().isEnabled },
            name = "IsNotEnabled of $selectorDesc",
            type = UiAutomatorAssertionType.IS_NOT_ENABLED,
            description = "UiObject assertion '${UiAutomatorAssertionType.IS_NOT_ENABLED}' of $selectorDesc during $timeoutMs ms"
        )
    }

    fun isFocusable() = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().isFocusable },
            name = "IsFocusable of $selectorDesc",
            type = UiAutomatorAssertionType.IS_FOCUSABLE,
            description = "UiObject assertion '${UiAutomatorAssertionType.IS_FOCUSABLE}' of $selectorDesc during $timeoutMs ms"
        )
    }

    fun isNotFocusable() = apply {
        executeOperation(
            operationBlock = { !uiObjectProviderBlock().isFocusable },
            name = "IsNotFocusable of $selectorDesc",
            type = UiAutomatorAssertionType.IS_NOT_FOCUSABLE,
            description = "UiObject assertion '${UiAutomatorAssertionType.IS_NOT_FOCUSABLE}' of $selectorDesc during $timeoutMs ms"
        )
    }

    fun isFocused() = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().isFocused },
            name = "IsFocused of $selectorDesc",
            type = UiAutomatorAssertionType.IS_FOCUSED,
            description = "UiObject assertion '${UiAutomatorAssertionType.IS_FOCUSED}' of $selectorDesc during $timeoutMs ms"
        )
    }

    fun isNotFocused() = apply {
        executeOperation(
            operationBlock = { !uiObjectProviderBlock().isFocused },
            name = "IsNotFocused of $selectorDesc",
            type = UiAutomatorAssertionType.IS_NOT_FOCUSED,
            description = "UiObject assertion '${UiAutomatorAssertionType.IS_NOT_FOCUSED}' of $selectorDesc during $timeoutMs ms"
        )
    }

    fun isLongClickable() = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().isLongClickable },
            name = "IsLongClickable of $selectorDesc",
            type = UiAutomatorAssertionType.IS_LONG_CLICKABLE,
            description = "UiObject assertion '${UiAutomatorAssertionType.IS_LONG_CLICKABLE}' of $selectorDesc during $timeoutMs ms"
        )
    }

    fun isNotLongClickable() = apply {
        executeOperation(
            operationBlock = { !uiObjectProviderBlock().isLongClickable },
            name = "IsNotLongClickable of $selectorDesc",
            type = UiAutomatorAssertionType.IS_NOT_LONG_CLICKABLE,
            description = "UiObject assertion '${UiAutomatorAssertionType.IS_NOT_LONG_CLICKABLE}' of $selectorDesc during $timeoutMs ms"
        )
    }

    fun isScrollable() = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().isScrollable },
            name = "IsScrollable of $selectorDesc",
            type = UiAutomatorAssertionType.IS_SCROLLABLE,
            description = "UiObject assertion '${UiAutomatorAssertionType.IS_SCROLLABLE}' of $selectorDesc during $timeoutMs ms"
        )
    }

    fun isNotScrollable() = apply {
        executeOperation(
            operationBlock = { !uiObjectProviderBlock().isScrollable },
            name = "IsNotScrollable of $selectorDesc",
            type = UiAutomatorAssertionType.IS_NOT_SCROLLABLE,
            description = "UiObject assertion '${UiAutomatorAssertionType.IS_NOT_SCROLLABLE}' of $selectorDesc during $timeoutMs ms"
        )
    }

    fun isSelected() = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().isSelected },
            name = "IsSelected of $selectorDesc",
            type = UiAutomatorAssertionType.IS_SELECTED,
            description = "UiObject assertion '${UiAutomatorAssertionType.IS_SELECTED}' of $selectorDesc during $timeoutMs ms"
        )
    }

    fun isNotSelected() = apply {
        executeOperation(
            operationBlock = { !uiObjectProviderBlock().isSelected },
            name = "IsNotSelected of $selectorDesc",
            type = UiAutomatorAssertionType.IS_NOT_SELECTED,
            description = "UiObject assertion '${UiAutomatorAssertionType.IS_NOT_SELECTED}' of $selectorDesc during $timeoutMs ms"
        )
    }

    fun hasText(text: String) = apply { hasText(Matchers.equalTo(text)) }

    fun hasText(textMatcher: Matcher<String>) = apply {
        executeOperation(
            operationBlock = {
                val actualText = uiObjectProviderBlock().text
                if (!textMatcher.matches(actualText)) {
                    throw UltronAssertionException("Expected: text matches '$textMatcher', got '$actualText'.")
                }
                true
            },
            name = "HasText $textMatcher in $selectorDesc",
            type = UiAutomatorAssertionType.HAS_TEXT,
            description = "UiObject assertion '${UiAutomatorAssertionType.HAS_TEXT}' matches '$textMatcher' in $selectorDesc during $timeoutMs ms"
        )
    }

    fun textContains(textSubstring: String) = apply { hasText(Matchers.containsString(textSubstring)) }

    fun textIsNullOrEmpty() = apply {
        hasText(Matchers.isEmptyOrNullString())
    }

    fun textIsNotNullOrEmpty() = apply {
        hasText(Matchers.not(Matchers.isEmptyOrNullString()))
    }

    fun hasContentDescription(contentDesc: String) = apply {
        hasContentDescription(Matchers.equalTo(contentDesc))
    }

    fun hasContentDescription(contentDescMatcher: Matcher<String>) = apply {
        executeOperation(
            operationBlock = {
                val contentDesc = uiObjectProviderBlock().contentDescription
                if (!contentDescMatcher.matches(contentDesc)) {
                    throw UltronAssertionException("Expected: contentDescription matches '$contentDescMatcher', got '$contentDesc'.")
                }
                true
            },
            name = "HasContentDescription $contentDescMatcher in $selectorDesc",
            type = UiAutomatorAssertionType.HAS_CONTENT_DESCRIPTION,
            description = "UiObject assertion '${UiAutomatorAssertionType.HAS_CONTENT_DESCRIPTION}' matches '$contentDescMatcher' in $selectorDesc during $timeoutMs ms"
        )
    }

    fun contentDescriptionContains(contentDescSubstring: String) = apply { hasContentDescription(Matchers.containsString(contentDescSubstring)) }
    fun contentDescriptionIsNullOrEmpty() = apply { hasContentDescription(Matchers.isEmptyOrNullString()) }
    fun contentDescriptionIsNotNullOrEmpty() = apply { hasContentDescription(Matchers.not(Matchers.isEmptyOrNullString())) }

    fun assertThat(assertBlock: UiObject.() -> Boolean, assertionDescription: String) = apply {
        executeOperation(
            operationBlock = { uiObjectProviderBlock().assertBlock() },
            name = "AssertThat $assertionDescription in $selectorDesc",
            type = UiAutomatorAssertionType.ASSERT_THAT,
            description = "UiObject assertion '${UiAutomatorAssertionType.ASSERT_THAT}' $assertionDescription in $selectorDesc during $timeoutMs ms"
        )
    }

    fun executeOperation(operationBlock: () -> Boolean, name: String = "empty name", description: String = "empty name", type: UltronOperationType = CommonOperationType.DEFAULT) {
        UltronUiAutomatorLifecycle.execute(
            UiAutomatorUiSelectorOperationExecutor(
                UiAutomatorUiSelectorOperation(
                    operationBlock = operationBlock,
                    name = name,
                    type = type,
                    description = description,
                    timeoutMs = timeoutMs,
                    assertion = assertion
                )
            ), resultHandler
        )
    }

    companion object {
        @JvmStatic
        fun uiResId(@IntegerRes resourceId: Int): UltronUiObject {
            val uiSelector = uiSelector(resourceId)
            return UltronUiObject(
                { UltronConfig.UiAutomator.uiDevice.findObject(uiSelector) },
                uiSelector.toString()
            )
        }

        @JvmStatic
        fun ui(uiSelector: UiSelector): UltronUiObject {
            return UltronUiObject(
                { UltronConfig.UiAutomator.uiDevice.findObject(uiSelector) },
                uiSelector.toString()
            )
        }

        @JvmStatic
        fun uiSelector(@IntegerRes resourceId: Int): UiSelector {
            return UiSelector().resourceId(getTargetResourceName(resourceId))
        }
    }
}