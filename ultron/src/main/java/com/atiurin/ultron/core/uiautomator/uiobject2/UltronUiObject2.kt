package com.atiurin.ultron.core.uiautomator.uiobject2

import android.graphics.Point
import android.graphics.Rect
import androidx.annotation.IntegerRes
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiObject2
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.uiautomator.UiAutomatorActionType
import com.atiurin.ultron.core.uiautomator.UiAutomatorAssertionType
import com.atiurin.ultron.core.uiautomator.UiAutomatorLifecycle
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperationResult
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.extensions.methodToBoolean
import com.atiurin.ultron.utils.getTargetResourceName
import org.hamcrest.Matcher


class UltronUiObject2(
    val uiObject2ProviderBlock: () -> UiObject2?,
    val selectorDesc: String
) {
    fun isSuccess(
        action: UltronUiObject2.() -> Unit
    ): Boolean {
        return this.methodToBoolean(action)
    }
    // Search functions
    /** @return this object's parent, or null if it has no parent. */
    fun getParent(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): UltronUiObject2? {
        var uiobject2: UiObject2? = null
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { uiobject2 = uiObject2ProviderBlock()!!.parent },
                    name = "GetParent of $selectorDesc",
                    type = UiAutomatorActionType.GET_PARENT,
                    description = "UiObject2 action '${UiAutomatorActionType.GET_PARENT}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return if (uiobject2 != null) UltronUiObject2(
            { uiobject2 },
            "Parent of $selectorDesc."
        ) else null
    }

    /**
     * @return a collection of the child elements directly under this object. Empty list if no child exist
     * */
    fun getChildren(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): List<UltronUiObject2> {
        val children = mutableListOf<UiObject2>()
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { children.addAll(uiObject2ProviderBlock()!!.children) },
                    name = "GetChildren of $selectorDesc",
                    type = UiAutomatorActionType.GET_CHILDREN,
                    description = "UiObject2 action '${UiAutomatorActionType.GET_CHILDREN}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return children.map { UltronUiObject2({ it }, "Child of $selectorDesc") }
    }

    /** Returns the number of child elements directly under this object, 0 if it has no child*/
    fun getChildCount(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): Int {
        var count = 0
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { count = uiObject2ProviderBlock()!!.childCount },
                    name = "GetChildCount of $selectorDesc",
                    type = UiAutomatorActionType.GET_CHILD_COUNT,
                    description = "UiObject2 action '${UiAutomatorActionType.GET_CHILD_COUNT}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return count
    }

    /**
     * Searches all elements under this object and returns the first object to match the criteria,
     * or null if no matching objects are found.
     */
    fun findObject(
        bySelector: BySelector,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): UltronUiObject2? {
        var uiobject2: UiObject2? = null
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { uiobject2 = uiObject2ProviderBlock()!!.findObject(bySelector) },
                    name = "FindObject of $selectorDesc using $bySelector",
                    type = UiAutomatorActionType.FIND_OBJECT,
                    description = "UiObject2 action '${UiAutomatorActionType.FIND_OBJECT}' of $selectorDesc using $bySelector during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return if (uiobject2 != null) {
            UltronUiObject2(
                { uiobject2 },
                "First child of $selectorDesc with bySelector $bySelector."
            )
        } else null
    }

    /** Searches all elements under this object and returns all objects that match the criteria. */
    fun findObjects(
        bySelector: BySelector,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): List<UltronUiObject2> {
        val objects = mutableListOf<UiObject2>()
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { objects.addAll(uiObject2ProviderBlock()!!.findObjects(bySelector)) },
                    name = "FindObjects of $selectorDesc using $bySelector",
                    type = UiAutomatorActionType.FIND_OBJECTS,
                    description = "UiObject2 action '${UiAutomatorActionType.FIND_OBJECTS}' of $selectorDesc using $bySelector during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return objects.map {
            UltronUiObject2({ it }, "Child of $selectorDesc with bySelector $bySelector")
        }
    }

    // Attribute accessors
    /**
     * @return view.text or null if view has no text
     * if you would like to assert view.text use [hasText] and [textContains]
     */
    fun getText(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): String? {
        var text: String? = null
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { text = uiObject2ProviderBlock()!!.text },
                    name = "GetText of $selectorDesc",
                    type = UiAutomatorActionType.GET_TEXT,
                    description = "UiObject2 action '${UiAutomatorActionType.GET_TEXT}' of $selectorDesc  during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return text
    }

    /** @return the class name of the view represented by this object.
     *  or null if it impossible to get className */
    fun getClassName(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): String? {
        var className: String? = null
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { className = uiObject2ProviderBlock()!!.className },
                    name = "GetClassName of $selectorDesc",
                    type = UiAutomatorActionType.GET_CLASS_NAME,
                    description = "UiObject2 action '${UiAutomatorActionType.GET_CLASS_NAME}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return className
    }

    /** @return the package name of the app that this object belongs to.
     * or null if it is impossible to identify app
     * */
    fun getApplicationPackage(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): String? {
        var packageName: String? = null
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { packageName = uiObject2ProviderBlock()!!.applicationPackage },
                    name = "GetApplicationPackage of $selectorDesc",
                    type = UiAutomatorActionType.GET_APPLICATION_PACKAGE,
                    description = "UiObject2 action '${UiAutomatorActionType.GET_APPLICATION_PACKAGE}' of $selectorDesc  during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return packageName
    }

    /**  @return the visible bounds of this object in screen coordinates. */
    fun getVisibleBounds(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): Rect? {
        var visibleBounds: Rect? = null
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { visibleBounds = uiObject2ProviderBlock()!!.visibleBounds },
                    name = "GetVisibleBounds of $selectorDesc",
                    type = UiAutomatorActionType.GET_VISIBLE_BOUNDS,
                    description = "UiObject2 action '${UiAutomatorActionType.GET_VISIBLE_BOUNDS}' of $selectorDesc  during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return visibleBounds
    }

    /**  @return a point in the center of the visible bounds of this object. */
    fun getVisibleCenter(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): Point? {
        var visibleCenter: Point? = null
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { visibleCenter = uiObject2ProviderBlock()!!.visibleCenter },
                    name = "GetVisibleCenter of $selectorDesc",
                    type = UiAutomatorActionType.GET_VISIBLE_CENTER,
                    description = "UiObject2 action '${UiAutomatorActionType.GET_VISIBLE_CENTER}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return visibleCenter
    }

    /**
     * @return the fully qualified resource name for this object's id.
     * or null if resource name isn't specified
     */
    fun getResourceName(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): String? {
        var resName: String? = null
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { resName = uiObject2ProviderBlock()!!.resourceName },
                    name = "GetResourceName of $selectorDesc",
                    type = UiAutomatorActionType.GET_RESOURCE_NAME,
                    description = "UiObject2 action '${UiAutomatorActionType.GET_RESOURCE_NAME}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return resName
    }

    /**
     * @return the content description for this object
     * or null if it isn't specified for view
     */
    fun getContentDescription(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): String? {
        var contentDesc: String? = null
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { contentDesc = uiObject2ProviderBlock()!!.contentDescription },
                    name = "GetContentDescription of $selectorDesc",
                    type = UiAutomatorActionType.GET_CONTENT_DESCRIPTION,
                    description = "UiObject2 action '${UiAutomatorActionType.GET_CONTENT_DESCRIPTION}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return contentDesc
    }

    // Actions
    /** Clicks on object. */
    fun click(
        duration: Long = 0, // A basic click is a touch down and touch up over the same point with no delay.
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { uiObject2ProviderBlock()!!.click(duration) },
                    name = "Click to $selectorDesc with duration = $duration",
                    type = UiAutomatorActionType.CLICK,
                    description = "UiObject2 action '${UiAutomatorActionType.CLICK}' to $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    /** Performs a long click on object. */
    fun longClick(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { uiObject2ProviderBlock()!!.longClick() },
                    name = "LongClick to $selectorDesc",
                    type = UiAutomatorActionType.LONG_CLICK,
                    description = "UiObject2 action '${UiAutomatorActionType.LONG_CLICK}' to $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    /** Clears the text content if object is an editable field. */
    fun clear(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { uiObject2ProviderBlock()!!.clear() },
                    name = "Clear of $selectorDesc",
                    type = UiAutomatorActionType.CLEAR_TEXT,
                    description = "UiObject2 action '${UiAutomatorActionType.CLEAR_TEXT}' in $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    /** Add the text content if object is an editable field. */
    fun addText(
        text: String,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { uiObject2ProviderBlock()!!.text += text },
                    name = "AddText of $selectorDesc to '$text'",
                    type = UiAutomatorActionType.ADD_TEXT,
                    description = "UiObject2 action '${UiAutomatorActionType.ADD_TEXT}' = '$text' to $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    /** Set the text content by sending individual key codes.
     * @throws NullPointerException if you are trying to apply it on uneditable object
     * */
    fun legacySetText(
        text: String,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { uiObject2ProviderBlock()!!.legacySetText(text) },
                    name = "LegacySetText of $selectorDesc to '$text'",
                    type = UiAutomatorActionType.LEGACY_SET_TEXT,
                    description = "UiObject2 action '${UiAutomatorActionType.LEGACY_SET_TEXT}' in $selectorDesc to '$text' during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    /** Sets the text content if object is an editable field. */
    fun replaceText(
        text: String,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { uiObject2ProviderBlock()!!.text = text },
                    name = "ReplaceText of $selectorDesc to '$text'",
                    type = UiAutomatorActionType.REPLACE_TEXT,
                    description = "UiObject2 action '${UiAutomatorActionType.REPLACE_TEXT}' in $selectorDesc to '$text' during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    /**
     * Drags object to the specified location.
     *
     * @param dest The end point that this object should be dragged to.
     * @param speed The speed at which to perform this gesture in pixels per second.
     */
    fun drag(
        dest: Point,
        speed: Int = DEFAULT_DRAG_SPEED,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { uiObject2ProviderBlock()!!.drag(dest, speed) },
                    name = "Drag of $selectorDesc to dest = '$dest' with speed = $speed",
                    type = UiAutomatorActionType.DRAG,
                    description = "UiObject2 action '${UiAutomatorActionType.DRAG}' of $selectorDesc to dest = '$dest' with speed = $speed during $timeoutMs ms",
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
    fun pinchClose(
        percent: Float,
        speed: Int = DEFAULT_PINCH_SPEED,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { uiObject2ProviderBlock()!!.pinchClose(percent, speed) },
                    name = "PinchClose of $selectorDesc with $percent% and $speed speed",
                    type = UiAutomatorActionType.PINCH_CLOSE,
                    description = "UiObject2 action '${UiAutomatorActionType.PINCH_CLOSE}' of $selectorDesc with $percent% and $speed speed during $timeoutMs ms",
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
    fun pinchOpen(
        percent: Float,
        speed: Int = DEFAULT_PINCH_SPEED,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { uiObject2ProviderBlock()!!.pinchOpen(percent, speed) },
                    name = "PinchOpen of $selectorDesc with $percent% and $speed speed",
                    type = UiAutomatorActionType.PINCH_OPEN,
                    description = "UiObject2 action '${UiAutomatorActionType.PINCH_OPEN}' of $selectorDesc with $percent% and $speed speed during $timeoutMs ms",
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
    private fun swipe(
        direction: Direction,
        percent: Float,
        speed: Int = DEFAULT_SWIPE_SPEED,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { uiObject2ProviderBlock()!!.swipe(direction, percent, speed) },
                    name = "Swipe of $selectorDesc to direction = '${direction.name}' with $percent% and $speed speed",
                    type = UiAutomatorActionType.SWIPE,
                    description = "UiObject2 action '${UiAutomatorActionType.SWIPE}' of $selectorDesc with direction = '${direction.name}' with $percent% and $speed speed during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    /**
     * Performs a swipe up gesture on this object.
     *
     * @param percent The length of the swipe as a percentage of this object's size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     */
    fun swipeUp(
        percent: Float = 0.95F,
        speed: Int = DEFAULT_SWIPE_SPEED,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) = apply {
        this@UltronUiObject2.swipe(Direction.UP, percent, speed, timeoutMs, resultHandler)
    }

    /**
     * Performs a swipe down gesture on this object.
     *
     * @param percent The length of the swipe as a percentage of this object's size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     */
    fun swipeDown(
        percent: Float = 0.95F,
        speed: Int = DEFAULT_SWIPE_SPEED,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) = apply {
        this@UltronUiObject2.swipe(Direction.DOWN, percent, speed, timeoutMs, resultHandler)
    }

    /**
     * Performs a swipe left gesture on this object.
     *
     * @param percent The length of the swipe as a percentage of this object's size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     */
    fun swipeLeft(
        percent: Float = 0.95F,
        speed: Int = DEFAULT_SWIPE_SPEED,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) = apply {
        this@UltronUiObject2.swipe(Direction.LEFT, percent, speed, timeoutMs, resultHandler)
    }

    /**
     * Performs a swipe right gesture on this object.
     *
     * @param percent The length of the swipe as a percentage of this object's size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     */
    fun swipeRight(
        percent: Float = 0.95F,
        speed: Int = DEFAULT_SWIPE_SPEED,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) = apply {
        this@UltronUiObject2.swipe(Direction.RIGHT, percent, speed, timeoutMs, resultHandler)
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
        speed: Int = DEFAULT_SCROLL_SPEED,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): Boolean {
        var result = false
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = {
                        result = uiObject2ProviderBlock()!!.scroll(direction, percent, speed)
                    },
                    name = "Scroll of $selectorDesc to direction = '${direction.name}' with $percent% and $speed speed",
                    type = UiAutomatorActionType.SCROLL,
                    description = "UiObject2 action '${UiAutomatorActionType.SCROLL}' of $selectorDesc with direction = '${direction.name}' with $percent% and $speed speed during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
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
    fun scrollUp(
        percent: Float = 0.95F,
        speed: Int = DEFAULT_SCROLL_SPEED,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): Boolean {
        return scroll(Direction.UP, percent, speed, timeoutMs, resultHandler)
    }

    /**
     * Performs a scroll down gesture on this object.
     *
     * @param percent The distance to scroll as a percentage of this object's visible size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     * @return Whether the object can still scroll in the given direction.
     */
    fun scrollDown(
        percent: Float = 0.95F,
        speed: Int = DEFAULT_SCROLL_SPEED,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): Boolean {
        return scroll(Direction.DOWN, percent, speed, timeoutMs, resultHandler)
    }

    /**
     * Performs a scroll left gesture on this object.
     *
     * @param percent The distance to scroll as a percentage of this object's visible size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     * @return Whether the object can still scroll in the given direction.
     */
    fun scrollLeft(
        percent: Float = 0.95F,
        speed: Int = DEFAULT_SCROLL_SPEED,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): Boolean {
        return scroll(Direction.LEFT, percent, speed, timeoutMs, resultHandler)
    }

    /**
     * Performs a scroll right gesture on this object.
     *
     * @param percent The distance to scroll as a percentage of this object's visible size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     * @return Whether the object can still scroll in the given direction.
     */
    fun scrollRight(
        percent: Float = 0.95F,
        speed: Int = DEFAULT_SCROLL_SPEED,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): Boolean {
        return scroll(Direction.RIGHT, percent, speed, timeoutMs, resultHandler)
    }

    /**
     * Performs a fling gesture on this object.
     *
     * @param direction The direction in which to fling.
     * @param speed The speed at which to perform this gesture in pixels per second.
     * @return Whether the object can still scroll in the given direction.
     */
    fun fling(
        direction: Direction,
        speed: Int = DEFAULT_FLING_SPEED,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): Boolean {
        var result: Boolean = false
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { result = uiObject2ProviderBlock()!!.fling(direction, speed) },
                    name = "FLing of $selectorDesc to direction = '${direction.name}' with speed = $speed",
                    type = UiAutomatorActionType.FLING,
                    description = "UiObject2 action '${UiAutomatorActionType.FLING}' of $selectorDesc to direction = '${direction.name}' with speed = $speed during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return result
    }

    fun perform(
        actionBlock: UiObject2.() -> Unit,
        actionDescription: String,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    actionBlock = { uiObject2ProviderBlock()!!.actionBlock() },
                    name = "Perform custom action '$actionDescription' on $selectorDesc.",
                    type = UiAutomatorActionType.PERFORM,
                    description = "UiObject2 action '${UiAutomatorActionType.PERFORM}' custom action '$actionDescription' on $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    //asserts
    fun hasText(
        text: String?,
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = {
                        val actualText = uiObject2ProviderBlock()!!.text
                        if (actualText != text) {
                            throw UltronException("Expected: '$text', got '$actualText'.")
                        }
                        true
                    },
                    name = "HasText '$text' in $selectorDesc",
                    type = UiAutomatorAssertionType.HAS_TEXT,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.HAS_TEXT}' = '$text' in $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun hasText(
        textMatcher: Matcher<String>,
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = {
                        val actualText = uiObject2ProviderBlock()!!.text
                        if (!textMatcher.matches(actualText)) {
                            throw UltronException("Expected: text matches '$textMatcher', got '$actualText'.")
                        }
                        true
                    },
                    name = "HasText '$textMatcher' in $selectorDesc",
                    type = UiAutomatorAssertionType.HAS_TEXT,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.HAS_TEXT}' matches '$textMatcher' in $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun textContains(
        textSubstring: String,
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = {
                        val actualText = uiObject2ProviderBlock()!!.text
                        if (!actualText.contains(textSubstring)) {
                            throw UltronException("Expected: text contains '$textSubstring', got '$actualText'.")
                        }
                        true
                    },
                    name = "TextContains '$textSubstring' in $selectorDesc",
                    type = UiAutomatorAssertionType.TEXT_CONTAINS,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.TEXT_CONTAINS}' '$textSubstring' in $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun textIsNullOrEmpty(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = {
                        val actualText = uiObject2ProviderBlock()!!.text
                        if (!uiObject2ProviderBlock()!!.text.isNullOrEmpty()) {
                            throw UltronException("Expected: text isNullOrEmpty, got '$actualText'.")
                        }
                        true
                    },
                    name = "TextIsNullOrEmpty in $selectorDesc",
                    type = UiAutomatorAssertionType.TEST_IS_NULL_OR_EMPTY,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.TEST_IS_NULL_OR_EMPTY}' in $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun hasContentDescription(
        contentDesc: String,
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = {
                        val actualContentDesc = uiObject2ProviderBlock()!!.contentDescription
                        if (actualContentDesc != contentDesc) {
                            throw UltronException("Expected: '$contentDesc', got '$actualContentDesc'.")
                        }
                        true
                    },
                    name = "HasContentDescription '$contentDesc' in $selectorDesc",
                    type = UiAutomatorAssertionType.HAS_CONTENT_DESCRIPTION,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.HAS_CONTENT_DESCRIPTION}' '$contentDesc' in $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun hasContentDescription(
        contentDescMatcher: Matcher<String>,
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = {
                        val actualContentDesc = uiObject2ProviderBlock()!!.contentDescription
                        if (!contentDescMatcher.matches(actualContentDesc)) {
                            throw UltronException("Expected: contentDescription matches '$contentDescMatcher', got '$actualContentDesc'.")
                        }
                        true
                    },
                    name = "HasContentDescription '$contentDescMatcher' in $selectorDesc",
                    type = UiAutomatorAssertionType.HAS_CONTENT_DESCRIPTION,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.HAS_CONTENT_DESCRIPTION}' matches '$contentDescMatcher' in $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun contentDescriptionContains(
        contentDescSubstring: String,
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = {
                        val actualContentDesc = uiObject2ProviderBlock()!!.contentDescription
                        if (!actualContentDesc.contains(contentDescSubstring)) {
                            throw UltronException("Expected: contentDescription contains '$contentDescSubstring', got '$actualContentDesc'.")
                        }
                        true
                    },
                    name = "ContentDescriptionContains '$contentDescSubstring' in $selectorDesc",
                    type = UiAutomatorAssertionType.CONTENT_DESCRIPTION_CONTAINS_TEXT,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.CONTENT_DESCRIPTION_CONTAINS_TEXT}'. ContentDescriptionContains '$contentDescSubstring' in $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun contentDescriptionIsNullOrEmpty(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = {
                        val actualContentDesc = uiObject2ProviderBlock()!!.contentDescription
                        if (!actualContentDesc.isNullOrEmpty()) {
                            throw UltronException("Expected: contentDescription isNullOrEmpty, got '$actualContentDesc'.")
                        }
                        true
                    },
                    name = "ContentDescriptionContains isNullOrEmpty in $selectorDesc",
                    type = UiAutomatorAssertionType.CONTENT_DESCRIPTION_IS_NULL_OR_EMPTY,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.CONTENT_DESCRIPTION_IS_NULL_OR_EMPTY}' in $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun contentDescriptionIsNotNullOrEmpty(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = {
                        val actualContentDesc = uiObject2ProviderBlock()!!.contentDescription
                        if (actualContentDesc.isNullOrEmpty()) {
                            throw UltronException("Expected: contentDescription isNotNullOrEmpty, got '$actualContentDesc'.")
                        }
                        true
                    },
                    name = "ContentDescriptionContains isNotNullOrEmpty in $selectorDesc",
                    type = UiAutomatorAssertionType.CONTENT_DESCRIPTION_IS_NOT_NULL_OR_EMPTY,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.CONTENT_DESCRIPTION_IS_NOT_NULL_OR_EMPTY}' in $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isCheckable(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = { uiObject2ProviderBlock()!!.isCheckable },
                    name = "IsCheckable of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_CHECKABLE,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_CHECKABLE}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isNotCheckable(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = { !uiObject2ProviderBlock()!!.isCheckable },
                    name = "IsNotCheckable of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_NOT_CHECKABLE,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_CHECKABLE}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isChecked(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = { uiObject2ProviderBlock()!!.isChecked },
                    name = "IsChecked of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_CHECKED,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_CHECKED}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isNotChecked(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = { !uiObject2ProviderBlock()!!.isChecked },
                    name = "IsNotChecked of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_NOT_CHECKED,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_CHECKED}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isClickable(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = { uiObject2ProviderBlock()!!.isClickable },
                    name = "IsClickable of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_CLICKABLE,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_CLICKABLE}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isNotClickable(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = { !uiObject2ProviderBlock()!!.isClickable },
                    name = "IsNotClickable of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_NOT_CLICKABLE,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_CLICKABLE}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isEnabled(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = { uiObject2ProviderBlock()!!.isEnabled },
                    name = "IsEnabled of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_ENABLED,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_ENABLED}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isNotEnabled(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = { !uiObject2ProviderBlock()!!.isEnabled },
                    name = "IsNotEnabled of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_NOT_ENABLED,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_ENABLED}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isFocusable(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = { uiObject2ProviderBlock()!!.isFocusable },
                    name = "IsFocusable of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_FOCUSABLE,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_FOCUSABLE}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isNotFocusable(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = { !uiObject2ProviderBlock()!!.isFocusable },
                    name = "IsNotFocusable of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_NOT_FOCUSABLE,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_FOCUSABLE}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isFocused(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = { uiObject2ProviderBlock()!!.isFocused },
                    name = "IsFocused of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_FOCUSED,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_FOCUSED}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isNotFocused(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = { !uiObject2ProviderBlock()!!.isFocused },
                    name = "IsNotFocused of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_NOT_FOCUSED,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_FOCUSED}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isLongClickable(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = { uiObject2ProviderBlock()!!.isLongClickable },
                    name = "IsLongClickable of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_LONG_CLICKABLE,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_LONG_CLICKABLE}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isNotLongClickable(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = { !uiObject2ProviderBlock()!!.isLongClickable },
                    name = "IsNotLongClickable of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_NOT_LONG_CLICKABLE,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_LONG_CLICKABLE}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isScrollable(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = { uiObject2ProviderBlock()!!.isScrollable },
                    name = "IsScrollable of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_SCROLLABLE,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_SCROLLABLE}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isNotScrollable(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = { !uiObject2ProviderBlock()!!.isScrollable },
                    name = "IsNotScrollable of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_NOT_SCROLLABLE,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_SCROLLABLE}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isSelected(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = { uiObject2ProviderBlock()!!.isSelected },
                    name = "IsSelected of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_SELECTED,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_SELECTED}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isNotSelected(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = { !uiObject2ProviderBlock()!!.isSelected },
                    name = "IsNotSelected of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_NOT_SELECTED,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_SELECTED}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isDisplayed(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = { uiObject2ProviderBlock() != null },
                    name = "IsDisplayed of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_DISPLAYED,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_DISPLAYED}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isNotDisplayed(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = { uiObject2ProviderBlock() == null },
                    name = "IsNotDisplayed of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_NOT_DISPLAYED,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_DISPLAYED}' of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun assertThat(
        assertBlock: UiObject2.() -> Boolean,
        assertionDescription: String,
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) = apply {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    assertionBlock = { uiObject2ProviderBlock()!!.assertBlock() },
                    name = "AssertThat $assertionDescription in $selectorDesc",
                    type = UiAutomatorAssertionType.ASSERT_THAT,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.ASSERT_THAT}' $assertionDescription in $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
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
        /** value from [UiObject2.DEFAULT_PINCH_SPEED] */
        private const val DEFAULT_PINCH_SPEED = 2500

        fun resId(@IntegerRes resourceId: Int): UltronUiObject2 {
            val bySelector = byResId(resourceId)
            return UltronUiObject2(
                { UltronConfig.UiAutomator.uiDevice.findObject(bySelector) },
                bySelector.toString()
            )
        }

        fun by(bySelector: BySelector): UltronUiObject2 {
            return UltronUiObject2(
                { UltronConfig.UiAutomator.uiDevice.findObject(bySelector) },
                bySelector.toString()
            )
        }

        fun byResId(@IntegerRes resourceId: Int): BySelector {
            return By.res(getTargetResourceName(resourceId))
        }
    }
}