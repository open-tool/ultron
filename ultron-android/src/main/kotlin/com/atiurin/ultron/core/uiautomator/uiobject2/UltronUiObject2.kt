package com.atiurin.ultron.core.uiautomator.uiobject2

import android.graphics.Point
import android.graphics.Rect
import androidx.annotation.IntegerRes
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiObject2
import com.atiurin.ultron.core.common.CommonOperationType
import com.atiurin.ultron.core.common.ElementInfo
import com.atiurin.ultron.core.common.DefaultElementInfo
import com.atiurin.ultron.core.common.UltronOperationType
import com.atiurin.ultron.core.common.assertion.DefaultOperationAssertion
import com.atiurin.ultron.core.common.assertion.EmptyOperationAssertion
import com.atiurin.ultron.core.common.assertion.OperationAssertion
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.uiautomator.UiAutomatorActionType
import com.atiurin.ultron.core.uiautomator.UiAutomatorAssertionType
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperation
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperationResult
import com.atiurin.ultron.core.uiautomator.UltronUiAutomatorLifecycle
import com.atiurin.ultron.exceptions.UltronAssertionException
import com.atiurin.ultron.listeners.setListenersState
import com.atiurin.ultron.utils.getTargetResourceName
import org.hamcrest.Matcher
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.isEmptyOrNullString
import org.hamcrest.Matchers.not


class UltronUiObject2 internal constructor(
    val uiObject2ProviderBlock: () -> UiObject2?,
    val selectorDesc: String,
    val resultHandler: (UiAutomatorOperationResult<UiAutomatorOperation>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.resultHandler,
    val timeoutMs: Long = UltronConfig.UiAutomator.OPERATION_TIMEOUT,
    val assertion: OperationAssertion = EmptyOperationAssertion(),
    val elementInfo: ElementInfo = DefaultElementInfo()
) {
    init {
        if (elementInfo.name.isEmpty()) elementInfo.name = selectorDesc
    }

    fun isSuccess(action: UltronUiObject2.() -> Unit): Boolean = runCatching { action() }.isSuccess

    fun withResultHandler(resultHandler: (UiAutomatorOperationResult<UiAutomatorOperation>) -> Unit): UltronUiObject2 {
        return UltronUiObject2(
            this.uiObject2ProviderBlock,
            this.selectorDesc,
            resultHandler,
            this.timeoutMs,
            this.assertion,
            this.elementInfo
        )
    }

    fun withTimeout(timeoutMs: Long): UltronUiObject2 = UltronUiObject2(
        this.uiObject2ProviderBlock,
        this.selectorDesc,
        this.resultHandler,
        timeoutMs,
        this.assertion,
        this.elementInfo
    )

    fun withAssertion(assertion: OperationAssertion) = UltronUiObject2(
        this.uiObject2ProviderBlock,
        this.selectorDesc,
        this.resultHandler,
        this.timeoutMs,
        assertion,
        this.elementInfo
    )

    fun withAssertion(name: String = "", isListened: Boolean = false, block: () -> Unit) =
        UltronUiObject2(
            this.uiObject2ProviderBlock, this.selectorDesc, this.resultHandler, this.timeoutMs,
            DefaultOperationAssertion(name, block.setListenersState(isListened)),
            this.elementInfo
        )


    fun withName(name: String) = apply { elementInfo.name = name }

    fun withMetaInfo(meta: Any) = apply { elementInfo.meta = meta }
    // Search functions
    /** @return this object's parent, or null if it has no parent. */
    fun getParent(): UltronUiObject2? {
        var uiobject2: UiObject2? = null
        executeAction(
            actionBlock = { uiobject2 = uiObject2ProviderBlock()!!.parent },
            name = "GetParent of ${elementInfo.name}",
            type = UiAutomatorActionType.GET_PARENT,
            description = "UiObject2 action '${UiAutomatorActionType.GET_PARENT}' of ${elementInfo.name} during $timeoutMs ms"
        )
        return uiobject2?.let {
            UltronUiObject2(
                { it }, "Parent of ${elementInfo.name}."
            )
        }
    }

    /**
     * @return a collection of the child elements directly under this object. Empty list if no child exist
     * */
    fun getChildren(): List<UltronUiObject2> {
        val children = mutableListOf<UiObject2>()
        executeAction(
            actionBlock = { children.addAll(uiObject2ProviderBlock()!!.children) },
            name = "GetChildren of ${elementInfo.name}",
            type = UiAutomatorActionType.GET_CHILDREN,
            description = "UiObject2 action '${UiAutomatorActionType.GET_CHILDREN}' of ${elementInfo.name} during $timeoutMs ms"
        )
        return children.map { UltronUiObject2({ it }, "Child of ${elementInfo.name}") }
    }


    /** Returns the number of child elements directly under this object, 0 if it has no child*/
    fun getChildCount(): Int {
        var count = 0
        executeAction(
            actionBlock = { count = uiObject2ProviderBlock()!!.childCount },
            name = "GetChildCount of ${elementInfo.name}",
            type = UiAutomatorActionType.GET_CHILD_COUNT,
            description = "UiObject2 action '${UiAutomatorActionType.GET_CHILD_COUNT}' of ${elementInfo.name} during $timeoutMs ms"
        )
        return count
    }

    /**
     * Searches all elements under this object and returns the first object to match the criteria,
     * or null if no matching objects are found.
     */
    fun findObject(bySelector: BySelector): UltronUiObject2? {
        var uiobject2: UiObject2? = null
        executeAction(
            actionBlock = { uiobject2 = uiObject2ProviderBlock()!!.findObject(bySelector) },
            name = "FindObject of ${elementInfo.name} using $bySelector",
            type = UiAutomatorActionType.FIND_OBJECT,
            description = "UiObject2 action '${UiAutomatorActionType.FIND_OBJECT}' of ${elementInfo.name} using $bySelector during $timeoutMs ms"
        )
        return uiobject2?.let {
            UltronUiObject2(
                { it }, "First child of ${elementInfo.name} with bySelector $bySelector."
            )
        } ?: null
    }

    /** Searches all elements under this object and returns all objects that match the criteria. */
    fun findObjects(bySelector: BySelector): List<UltronUiObject2> {
        val objects = mutableListOf<UiObject2>()
        executeAction(
            actionBlock = { objects.addAll(uiObject2ProviderBlock()!!.findObjects(bySelector)) },
            name = "FindObjects of ${elementInfo.name} using $bySelector",
            type = UiAutomatorActionType.FIND_OBJECTS,
            description = "UiObject2 action '${UiAutomatorActionType.FIND_OBJECTS}' of ${elementInfo.name} using $bySelector during $timeoutMs ms"
        )
        return objects.map {
            UltronUiObject2({ it }, "Child of ${elementInfo.name} with bySelector $bySelector")
        }
    }

    // Attribute accessors
    /**
     * @return view.text or null if view has no text
     * if you would like to assert view.text use [hasText] and [textContains]
     */
    fun getText(): String? {
        var text: String? = null
        executeAction(
            actionBlock = { text = uiObject2ProviderBlock()!!.text },
            name = "GetText of ${elementInfo.name}",
            type = UiAutomatorActionType.GET_TEXT,
            description = "UiObject2 action '${UiAutomatorActionType.GET_TEXT}' of ${elementInfo.name}  during $timeoutMs ms"
        )
        return text
    }

    /** @return the class name of the view represented by this object.
     *  or null if it impossible to get className */
    fun getClassName(): String? {
        var className: String? = null
        executeAction(
            actionBlock = { className = uiObject2ProviderBlock()!!.className },
            name = "GetClassName of ${elementInfo.name}",
            type = UiAutomatorActionType.GET_CLASS_NAME,
            description = "UiObject2 action '${UiAutomatorActionType.GET_CLASS_NAME}' of ${elementInfo.name} during $timeoutMs ms"
        )
        return className
    }

    /** @return the package name of the app that this object belongs to.
     * or null if it is impossible to identify app
     * */
    fun getApplicationPackage(): String? {
        var packageName: String? = null
        executeAction(
            actionBlock = { packageName = uiObject2ProviderBlock()!!.applicationPackage },
            name = "GetApplicationPackage of ${elementInfo.name}",
            type = UiAutomatorActionType.GET_APPLICATION_PACKAGE,
            description = "UiObject2 action '${UiAutomatorActionType.GET_APPLICATION_PACKAGE}' of ${elementInfo.name}  during $timeoutMs ms"
        )
        return packageName
    }

    /**  @return the visible bounds of this object in screen coordinates. */
    fun getVisibleBounds(): Rect? {
        var visibleBounds: Rect? = null
        executeAction(
            actionBlock = { visibleBounds = uiObject2ProviderBlock()!!.visibleBounds },
            name = "GetVisibleBounds of ${elementInfo.name}",
            type = UiAutomatorActionType.GET_VISIBLE_BOUNDS,
            description = "UiObject2 action '${UiAutomatorActionType.GET_VISIBLE_BOUNDS}' of ${elementInfo.name}  during $timeoutMs ms"
        )
        return visibleBounds
    }

    /**  @return a point in the center of the visible bounds of this object. */
    fun getVisibleCenter(): Point? {
        var visibleCenter: Point? = null
        executeAction(
            actionBlock = { visibleCenter = uiObject2ProviderBlock()!!.visibleCenter },
            name = "GetVisibleCenter of ${elementInfo.name}",
            type = UiAutomatorActionType.GET_VISIBLE_CENTER,
            description = "UiObject2 action '${UiAutomatorActionType.GET_VISIBLE_CENTER}' of ${elementInfo.name} during $timeoutMs ms"
        )
        return visibleCenter
    }

    /**
     * @return the fully qualified resource name for this object's id.
     * or null if resource name isn't specified
     */
    fun getResourceName(): String? {
        var resName: String? = null
        executeAction(
            actionBlock = { resName = uiObject2ProviderBlock()!!.resourceName },
            name = "GetResourceName of ${elementInfo.name}",
            type = UiAutomatorActionType.GET_RESOURCE_NAME,
            description = "UiObject2 action '${UiAutomatorActionType.GET_RESOURCE_NAME}' of ${elementInfo.name} during $timeoutMs ms"
        )
        return resName
    }

    /**
     * @return the content description for this object
     * or null if it isn't specified for view
     */
    fun getContentDescription(): String? {
        var contentDesc: String? = null
        executeAction(
            actionBlock = { contentDesc = uiObject2ProviderBlock()!!.contentDescription },
            name = "GetContentDescription of ${elementInfo.name}",
            type = UiAutomatorActionType.GET_CONTENT_DESCRIPTION,
            description = "UiObject2 action '${UiAutomatorActionType.GET_CONTENT_DESCRIPTION}' of ${elementInfo.name} during $timeoutMs ms"
        )
        return contentDesc
    }

    // Actions
    /** Clicks on object. */
    fun click(
        duration: Long = 0 // A basic click is a touch down and touch up over the same point with no delay.
    ) = apply {
        executeAction(
            actionBlock = { uiObject2ProviderBlock()!!.click(duration) },
            name = "Click to ${elementInfo.name} with duration = $duration",
            type = UiAutomatorActionType.CLICK,
            description = "UiObject2 action '${UiAutomatorActionType.CLICK}' to ${elementInfo.name} during $timeoutMs ms"
        )
    }

    /** Performs a long click on object. */
    fun longClick() = apply {
        executeAction(
            actionBlock = { uiObject2ProviderBlock()!!.longClick() },
            name = "LongClick to ${elementInfo.name}",
            type = UiAutomatorActionType.LONG_CLICK,
            description = "UiObject2 action '${UiAutomatorActionType.LONG_CLICK}' to ${elementInfo.name} during $timeoutMs ms"
        )
    }

    /** Clears the text content if object is an editable field. */
    fun clear() = apply {
        executeAction(
            actionBlock = { uiObject2ProviderBlock()!!.clear() },
            name = "Clear of ${elementInfo.name}",
            type = UiAutomatorActionType.CLEAR_TEXT,
            description = "UiObject2 action '${UiAutomatorActionType.CLEAR_TEXT}' in ${elementInfo.name} during $timeoutMs ms"
        )
    }

    /** Add the text content if object is an editable field. */
    fun addText(text: String) = apply {
        executeAction(
            actionBlock = { uiObject2ProviderBlock()!!.text += text },
            name = "AddText of ${elementInfo.name} to '$text'",
            type = UiAutomatorActionType.ADD_TEXT,
            description = "UiObject2 action '${UiAutomatorActionType.ADD_TEXT}' = '$text' to ${elementInfo.name} during $timeoutMs ms"
        )
    }

    /** Set the text content by sending individual key codes.
     * @throws NullPointerException if you are trying to apply it on uneditable object
     * */
    fun legacySetText(text: String) = apply {
        executeAction(
            actionBlock = { uiObject2ProviderBlock()!!.legacySetText(text) },
            name = "LegacySetText of ${elementInfo.name} to '$text'",
            type = UiAutomatorActionType.LEGACY_SET_TEXT,
            description = "UiObject2 action '${UiAutomatorActionType.LEGACY_SET_TEXT}' in ${elementInfo.name} to '$text' during $timeoutMs ms"
        )
    }

    /** Sets the text content if object is an editable field. */
    fun replaceText(text: String) = apply {
        executeAction(
            actionBlock = { uiObject2ProviderBlock()!!.text = text },
            name = "ReplaceText of ${elementInfo.name} to '$text'",
            type = UiAutomatorActionType.REPLACE_TEXT,
            description = "UiObject2 action '${UiAutomatorActionType.REPLACE_TEXT}' in ${elementInfo.name} to '$text' during $timeoutMs ms"
        )
    }

    /**
     * Drags object to the specified location.
     *
     * @param dest The end point that this object should be dragged to.
     * @param speed The speed at which to perform this gesture in pixels per second.
     */
    fun drag(dest: Point, speed: Int = DEFAULT_DRAG_SPEED) = apply {
        executeAction(
            actionBlock = { uiObject2ProviderBlock()!!.drag(dest, speed) },
            name = "Drag of ${elementInfo.name} to dest = '$dest' with speed = $speed",
            type = UiAutomatorActionType.DRAG,
            description = "UiObject2 action '${UiAutomatorActionType.DRAG}' of ${elementInfo.name} to dest = '$dest' with speed = $speed during $timeoutMs ms"
        )
    }

    /**
     * Performs a pinch close gesture on this object.
     *
     * @param percent The size of the pinch as a percentage of this object's size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     */
    fun pinchClose(percent: Float, speed: Int = DEFAULT_PINCH_SPEED) = apply {
        executeAction(
            actionBlock = { uiObject2ProviderBlock()!!.pinchClose(percent, speed) },
            name = "PinchClose of ${elementInfo.name} with $percent% and $speed speed",
            type = UiAutomatorActionType.PINCH_CLOSE,
            description = "UiObject2 action '${UiAutomatorActionType.PINCH_CLOSE}' of ${elementInfo.name} with $percent% and $speed speed during $timeoutMs ms"
        )
    }

    /**
     * Performs a pinch open gesture on this object.
     *
     * @param percent The size of the pinch as a percentage of this object's size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     */
    fun pinchOpen(percent: Float, speed: Int = DEFAULT_PINCH_SPEED) = apply {
        executeAction(
            actionBlock = { uiObject2ProviderBlock()!!.pinchOpen(percent, speed) },
            name = "PinchOpen of ${elementInfo.name} with $percent% and $speed speed",
            type = UiAutomatorActionType.PINCH_OPEN,
            description = "UiObject2 action '${UiAutomatorActionType.PINCH_OPEN}' of ${elementInfo.name} with $percent% and $speed speed during $timeoutMs ms"
        )
    }

    /**
     * Performs a swipe gesture on this object.
     *
     * @param direction The direction in which to swipe.
     * @param percent The length of the swipe as a percentage of this object's size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     */
    private fun swipe(direction: Direction, percent: Float, speed: Int = DEFAULT_SWIPE_SPEED) =
        apply {
            executeAction(
                actionBlock = { uiObject2ProviderBlock()!!.swipe(direction, percent, speed) },
                name = "Swipe of ${elementInfo.name} to direction = '${direction.name}' with $percent% and $speed speed",
                type = UiAutomatorActionType.SWIPE,
                description = "UiObject2 action '${UiAutomatorActionType.SWIPE}' of ${elementInfo.name} with direction = '${direction.name}' with $percent% and $speed speed during $timeoutMs ms"
            )
        }

    /**
     * Performs a swipe up gesture on this object.
     *
     * @param percent The length of the swipe as a percentage of this object's size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     */
    fun swipeUp(percent: Float = 0.95F, speed: Int = DEFAULT_SWIPE_SPEED) = apply {
        this@UltronUiObject2.swipe(Direction.UP, percent, speed)
    }

    /**
     * Performs a swipe down gesture on this object.
     *
     * @param percent The length of the swipe as a percentage of this object's size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     */
    fun swipeDown(percent: Float = 0.95F, speed: Int = DEFAULT_SWIPE_SPEED) = apply {
        this@UltronUiObject2.swipe(Direction.DOWN, percent, speed)
    }

    /**
     * Performs a swipe left gesture on this object.
     *
     * @param percent The length of the swipe as a percentage of this object's size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     */
    fun swipeLeft(percent: Float = 0.95F, speed: Int = DEFAULT_SWIPE_SPEED) = apply {
        this@UltronUiObject2.swipe(Direction.LEFT, percent, speed)
    }

    /**
     * Performs a swipe right gesture on this object.
     *
     * @param percent The length of the swipe as a percentage of this object's size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     */
    fun swipeRight(percent: Float = 0.95F, speed: Int = DEFAULT_SWIPE_SPEED) = apply {
        this@UltronUiObject2.swipe(Direction.RIGHT, percent, speed)
    }

    /**
     * Performs a scroll gesture on this object.
     *
     * @param direction The direction in which to scroll.
     * @param percent The distance to scroll as a percentage of this object's visible size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     * @return Whether the object can still scroll in the given direction.
     */
    private fun scroll(
        direction: Direction,
        percent: Float,
        speed: Int = DEFAULT_SCROLL_SPEED
    ): Boolean {
        var result = false
        executeAction(
            actionBlock = {
                result = uiObject2ProviderBlock()!!.scroll(direction, percent, speed)
            },
            name = "Scroll of ${elementInfo.name} to direction = '${direction.name}' with $percent% and $speed speed",
            type = UiAutomatorActionType.SCROLL,
            description = "UiObject2 action '${UiAutomatorActionType.SCROLL}' of ${elementInfo.name} with direction = '${direction.name}' with $percent% and $speed speed during $timeoutMs ms"
        )
        return result
    }

    /**
     * Performs a scroll up gesture on this object.
     *
     * @param percent The distance to scroll as a percentage of this object's visible size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     * @return Whether the object can still scroll in the given direction.
     */
    fun scrollUp(percent: Float = 0.95F, speed: Int = DEFAULT_SCROLL_SPEED): Boolean {
        return scroll(Direction.UP, percent, speed)
    }

    /**
     * Performs a scroll down gesture on this object.
     *
     * @param percent The distance to scroll as a percentage of this object's visible size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     * @return Whether the object can still scroll in the given direction.
     */
    fun scrollDown(percent: Float = 0.95F, speed: Int = DEFAULT_SCROLL_SPEED): Boolean {
        return scroll(Direction.DOWN, percent, speed)
    }

    /**
     * Performs a scroll left gesture on this object.
     *
     * @param percent The distance to scroll as a percentage of this object's visible size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     * @return Whether the object can still scroll in the given direction.
     */
    fun scrollLeft(percent: Float = 0.95F, speed: Int = DEFAULT_SCROLL_SPEED): Boolean {
        return scroll(Direction.LEFT, percent, speed)
    }

    /**
     * Performs a scroll right gesture on this object.
     *
     * @param percent The distance to scroll as a percentage of this object's visible size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     * @return Whether the object can still scroll in the given direction.
     */
    fun scrollRight(percent: Float = 0.95F, speed: Int = DEFAULT_SCROLL_SPEED): Boolean {
        return scroll(Direction.RIGHT, percent, speed)
    }

    /**
     * Performs a fling gesture on this object.
     *
     * @param direction The direction in which to fling.
     * @param speed The speed at which to perform this gesture in pixels per second.
     * @return Whether the object can still scroll in the given direction.
     */
    fun fling(direction: Direction, speed: Int = DEFAULT_FLING_SPEED): Boolean {
        var result: Boolean = false
        executeAction(
            actionBlock = { result = uiObject2ProviderBlock()!!.fling(direction, speed) },
            name = "FLing of ${elementInfo.name} to direction = '${direction.name}' with speed = $speed",
            type = UiAutomatorActionType.FLING,
            description = "UiObject2 action '${UiAutomatorActionType.FLING}' of ${elementInfo.name} to direction = '${direction.name}' with speed = $speed during $timeoutMs ms"
        )
        return result
    }

    /**
     * @param actionBlock provides an access to UiObject2.
     * @param actionDescription used for logging and exception description
     */
    fun perform(actionBlock: UiObject2.() -> Unit, actionDescription: String) = apply {
        executeAction(
            actionBlock = { uiObject2ProviderBlock()!!.actionBlock() },
            name = "Perform custom action '$actionDescription' on ${elementInfo.name}.",
            type = UiAutomatorActionType.PERFORM,
            description = "UiObject2 action '${UiAutomatorActionType.PERFORM}' custom action '$actionDescription' on ${elementInfo.name} during $timeoutMs ms"
        )
    }

    //asserts
    fun hasText(textMatcher: Matcher<String>) = apply {
        executeAssertion(
            assertionBlock = {
                val actualText = uiObject2ProviderBlock()!!.text
                if (!textMatcher.matches(actualText)) {
                    throw UltronAssertionException("Expected: text matches '$textMatcher', got '$actualText'.")
                }
                true
            },
            name = "HasText '$textMatcher' in ${elementInfo.name}",
            type = UiAutomatorAssertionType.HAS_TEXT,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.HAS_TEXT}' matches '$textMatcher' in ${elementInfo.name} during $timeoutMs ms"
        )
    }

    fun hasText(text: String) = apply { hasText(equalTo(text)) }
    fun textContains(textSubstring: String) = apply { hasText(containsString(textSubstring)) }
    fun textIsNullOrEmpty() = apply { hasText(isEmptyOrNullString()) }
    fun textIsNotNullOrEmpty() = apply { hasText(not(isEmptyOrNullString())) }

    fun hasContentDescription(contentDescMatcher: Matcher<String>) = apply {
        executeAssertion(
            assertionBlock = {
                val actualContentDesc = uiObject2ProviderBlock()!!.contentDescription
                if (!contentDescMatcher.matches(actualContentDesc)) {
                    throw UltronAssertionException("Expected: contentDescription matches '$contentDescMatcher', got '$actualContentDesc'.")
                }
                true
            },
            name = "HasContentDescription '$contentDescMatcher' in ${elementInfo.name}",
            type = UiAutomatorAssertionType.HAS_CONTENT_DESCRIPTION,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.HAS_CONTENT_DESCRIPTION}' matches '$contentDescMatcher' in ${elementInfo.name} during $timeoutMs ms"
        )
    }

    fun hasContentDescription(contentDesc: String) =
        apply { hasContentDescription(equalTo(contentDesc)) }

    fun contentDescriptionContains(contentDescSubstring: String) =
        apply { hasContentDescription(containsString(contentDescSubstring)) }

    fun contentDescriptionIsNullOrEmpty() = apply { hasContentDescription(isEmptyOrNullString()) }
    fun contentDescriptionIsNotNullOrEmpty() =
        apply { hasContentDescription(not(isEmptyOrNullString())) }

    fun isCheckable() = apply {
        executeAssertion(
            assertionBlock = { uiObject2ProviderBlock()!!.isCheckable },
            name = "IsCheckable of ${elementInfo.name}",
            type = UiAutomatorAssertionType.IS_CHECKABLE,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_CHECKABLE}' of ${elementInfo.name} during $timeoutMs ms"
        )
    }

    fun isNotCheckable() = apply {
        executeAssertion(
            assertionBlock = { !uiObject2ProviderBlock()!!.isCheckable },
            name = "IsNotCheckable of ${elementInfo.name}",
            type = UiAutomatorAssertionType.IS_NOT_CHECKABLE,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_CHECKABLE}' of ${elementInfo.name} during $timeoutMs ms"
        )
    }

    fun isChecked() = apply {
        executeAssertion(
            assertionBlock = { uiObject2ProviderBlock()!!.isChecked },
            name = "IsChecked of ${elementInfo.name}",
            type = UiAutomatorAssertionType.IS_CHECKED,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_CHECKED}' of ${elementInfo.name} during $timeoutMs ms"
        )
    }

    fun isNotChecked() = apply {
        executeAssertion(
            assertionBlock = { !uiObject2ProviderBlock()!!.isChecked },
            name = "IsNotChecked of ${elementInfo.name}",
            type = UiAutomatorAssertionType.IS_NOT_CHECKED,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_CHECKED}' of ${elementInfo.name} during $timeoutMs ms"
        )
    }

    fun isClickable() = apply {
        executeAssertion(
            assertionBlock = { uiObject2ProviderBlock()!!.isClickable },
            name = "IsClickable of ${elementInfo.name}",
            type = UiAutomatorAssertionType.IS_CLICKABLE,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_CLICKABLE}' of ${elementInfo.name} during $timeoutMs ms"
        )
    }

    fun isNotClickable() = apply {
        executeAssertion(
            assertionBlock = { !uiObject2ProviderBlock()!!.isClickable },
            name = "IsNotClickable of ${elementInfo.name}",
            type = UiAutomatorAssertionType.IS_NOT_CLICKABLE,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_CLICKABLE}' of ${elementInfo.name} during $timeoutMs ms"
        )
    }

    fun isEnabled() = apply {
        executeAssertion(
            assertionBlock = { uiObject2ProviderBlock()!!.isEnabled },
            name = "IsEnabled of ${elementInfo.name}",
            type = UiAutomatorAssertionType.IS_ENABLED,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_ENABLED}' of ${elementInfo.name} during $timeoutMs ms"
        )
    }

    fun isNotEnabled() = apply {
        executeAssertion(
            assertionBlock = { !uiObject2ProviderBlock()!!.isEnabled },
            name = "IsNotEnabled of ${elementInfo.name}",
            type = UiAutomatorAssertionType.IS_NOT_ENABLED,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_ENABLED}' of ${elementInfo.name} during $timeoutMs ms"
        )
    }

    fun isFocusable() = apply {
        executeAssertion(
            assertionBlock = { uiObject2ProviderBlock()!!.isFocusable },
            name = "IsFocusable of ${elementInfo.name}",
            type = UiAutomatorAssertionType.IS_FOCUSABLE,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_FOCUSABLE}' of ${elementInfo.name} during $timeoutMs ms"
        )
    }

    fun isNotFocusable() = apply {
        executeAssertion(
            assertionBlock = { !uiObject2ProviderBlock()!!.isFocusable },
            name = "IsNotFocusable of ${elementInfo.name}",
            type = UiAutomatorAssertionType.IS_NOT_FOCUSABLE,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_FOCUSABLE}' of ${elementInfo.name} during $timeoutMs ms"
        )
    }

    fun isFocused() = apply {
        executeAssertion(
            assertionBlock = { uiObject2ProviderBlock()!!.isFocused },
            name = "IsFocused of ${elementInfo.name}",
            type = UiAutomatorAssertionType.IS_FOCUSED,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_FOCUSED}' of ${elementInfo.name} during $timeoutMs ms"
        )
    }

    fun isNotFocused() = apply {
        executeAssertion(
            assertionBlock = { !uiObject2ProviderBlock()!!.isFocused },
            name = "IsNotFocused of ${elementInfo.name}",
            type = UiAutomatorAssertionType.IS_NOT_FOCUSED,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_FOCUSED}' of ${elementInfo.name} during $timeoutMs ms"
        )
    }

    fun isLongClickable() = apply {
        executeAssertion(
            assertionBlock = { uiObject2ProviderBlock()!!.isLongClickable },
            name = "IsLongClickable of ${elementInfo.name}",
            type = UiAutomatorAssertionType.IS_LONG_CLICKABLE,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_LONG_CLICKABLE}' of ${elementInfo.name} during $timeoutMs ms"
        )
    }

    fun isNotLongClickable() = apply {
        executeAssertion(
            assertionBlock = { !uiObject2ProviderBlock()!!.isLongClickable },
            name = "IsNotLongClickable of ${elementInfo.name}",
            type = UiAutomatorAssertionType.IS_NOT_LONG_CLICKABLE,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_LONG_CLICKABLE}' of ${elementInfo.name} during $timeoutMs ms"
        )
    }

    fun isScrollable() = apply {
        executeAssertion(
            assertionBlock = { uiObject2ProviderBlock()!!.isScrollable },
            name = "IsScrollable of ${elementInfo.name}",
            type = UiAutomatorAssertionType.IS_SCROLLABLE,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_SCROLLABLE}' of ${elementInfo.name} during $timeoutMs ms"
        )
    }

    fun isNotScrollable() = apply {
        executeAssertion(
            assertionBlock = { !uiObject2ProviderBlock()!!.isScrollable },
            name = "IsNotScrollable of ${elementInfo.name}",
            type = UiAutomatorAssertionType.IS_NOT_SCROLLABLE,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_SCROLLABLE}' of ${elementInfo.name} during $timeoutMs ms"
        )
    }

    fun isSelected() = apply {
        executeAssertion(
            assertionBlock = { uiObject2ProviderBlock()!!.isSelected },
            name = "IsSelected of ${elementInfo.name}",
            type = UiAutomatorAssertionType.IS_SELECTED,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_SELECTED}' of ${elementInfo.name} during $timeoutMs ms"
        )
    }

    fun isNotSelected() = apply {
        executeAssertion(
            assertionBlock = { !uiObject2ProviderBlock()!!.isSelected },
            name = "IsNotSelected of ${elementInfo.name}",
            type = UiAutomatorAssertionType.IS_NOT_SELECTED,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_SELECTED}' of ${elementInfo.name} during $timeoutMs ms"
        )
    }

    fun isDisplayed() = apply {
        executeAssertion(
            assertionBlock = { uiObject2ProviderBlock() != null },
            name = "IsDisplayed of ${elementInfo.name}",
            type = UiAutomatorAssertionType.IS_DISPLAYED,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_DISPLAYED}' of ${elementInfo.name} during $timeoutMs ms"
        )
    }

    fun isNotDisplayed() = apply {
        executeAssertion(
            assertionBlock = { uiObject2ProviderBlock() == null },
            name = "IsNotDisplayed of ${elementInfo.name}",
            type = UiAutomatorAssertionType.IS_NOT_DISPLAYED,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_DISPLAYED}' of ${elementInfo.name} during $timeoutMs ms"
        )
    }

    /**
     * @param assertBlock provides an access to UiObject2.
     * If [assertBlock] returns false it means the assertion failed and an exception will be thrown
     * @param assertionDescription used for logging and exception description
     */
    fun assertThat(assertBlock: UiObject2.() -> Boolean, assertionDescription: String) = apply {
        executeAssertion(
            assertionBlock = { uiObject2ProviderBlock()!!.assertBlock() },
            name = "AssertThat $assertionDescription in ${elementInfo.name}",
            type = UiAutomatorAssertionType.ASSERT_THAT,
            description = "UiObject2 assertion '${UiAutomatorAssertionType.ASSERT_THAT}' $assertionDescription in ${elementInfo.name} during $timeoutMs ms"
        )
    }

    @Suppress("UNCHECKED_CAST")
    fun executeAction(
        actionBlock: () -> Unit,
        name: String = "empty name",
        description: String = "empty description",
        type: UltronOperationType = CommonOperationType.DEFAULT,
        timeoutMs: Long = this.timeoutMs,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorOperation>) -> Unit = this.resultHandler
    ) {
        UltronUiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = actionBlock,
                    name = name,
                    type = type,
                    description = description,
                    timeoutMs = timeoutMs,
                    assertion = assertion,
                    elementInfo = elementInfo
                )
            ), resultHandler
        )
    }

    @Suppress("UNCHECKED_CAST")
    fun executeAssertion(
        assertionBlock: () -> Boolean,
        name: String = "empty name",
        description: String = "empty description",
        type: UltronOperationType = CommonOperationType.DEFAULT,
        timeoutMs: Long = this.timeoutMs,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorOperation>) -> Unit = this.resultHandler
    ) {
        UltronUiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = assertionBlock,
                    name = name,
                    type = type,
                    description = description,
                    timeoutMs = timeoutMs,
                    assertion = assertion,
                    elementInfo = elementInfo
                )
            ), resultHandler
        )
    }

    companion object {
        /** value from [UiObject2.DEFAULT_SWIPE_SPEED] */
        private const val DEFAULT_SWIPE_SPEED = 5000

        /** value from [UiObject2.DEFAULT_SCROLL_SPEED] */
        private const val DEFAULT_SCROLL_SPEED = 5000

        /** value from [UiObject2.DEFAULT_FLING_SPEED] */
        private const val DEFAULT_FLING_SPEED = 7500

        /** value from [UiObject2.DEFAULT_DRAG_SPEED] */
        private const val DEFAULT_DRAG_SPEED = 2500

        /** value from [UiObject2.DEFAULT_P INCH_SPEED] */
        private const val DEFAULT_PINCH_SPEED = 2500

        @JvmStatic
        fun byResId(@IntegerRes resourceId: Int): UltronUiObject2 {
            val bySelector = bySelector(resourceId)
            return UltronUiObject2(
                { UltronConfig.UiAutomator.uiDevice.findObject(bySelector) }, bySelector.toString()
            )
        }
        @JvmStatic
        fun byText(text: String): UltronUiObject2 {
            return by(By.text(text))
        }

        @JvmStatic
        fun by(bySelector: BySelector): UltronUiObject2 {
            return UltronUiObject2(
                { UltronConfig.UiAutomator.uiDevice.findObject(bySelector) }, bySelector.toString()
            )
        }

        @JvmStatic
        fun bySelector(@IntegerRes resourceId: Int): BySelector {
            return By.res(getTargetResourceName(resourceId))
        }
    }
}