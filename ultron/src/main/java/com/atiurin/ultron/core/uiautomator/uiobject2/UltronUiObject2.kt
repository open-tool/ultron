package com.atiurin.ultron.core.uiautomator.uiobject2

import android.graphics.Point
import android.graphics.Rect
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiObject2
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.uiautomator.UiAutomatorActionType
import com.atiurin.ultron.core.uiautomator.UiAutomatorAssertionType
import com.atiurin.ultron.core.uiautomator.UiAutomatorLifecycle
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperationResult
import com.atiurin.ultron.exceptions.UltronException
import org.hamcrest.Matcher


class UltronUiObject2(
    val uiObject2ProviderBlock: () -> UiObject2,
    val selectorDesc: String
) {
    // Search functions
    /** Returns this object's parent, or null if it has no parent. */
    fun getParent(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): UiObject2? {
        var uiobject2: UiObject2? = null
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { uiobject2 = this.parent },
                    name = "GetParent of $selectorDesc",
                    type = UiAutomatorActionType.GET_PARENT,
                    description = "UiObject2 action '${UiAutomatorActionType.GET_PARENT}'. GetParent of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return uiobject2
    }

    /**
     * @return Returns a collection of the child elements directly under this object. Empty list if no child exist
     * */
    fun getChildren(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): List<UiObject2> {
        val children = mutableListOf<UiObject2>()
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { children.addAll(this.children) },
                    name = "GetChildren of $selectorDesc",
                    type = UiAutomatorActionType.GET_CHILDREN,
                    description = "UiObject2 action '${UiAutomatorActionType.GET_CHILDREN}'. GetChildren of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return children
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
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { count = this.childCount },
                    name = "GetChildCount of $selectorDesc",
                    type = UiAutomatorActionType.GET_CHILD_COUNT,
                    description = "UiObject2 action '${UiAutomatorActionType.GET_CHILD_COUNT}'. GetChildCount of $selectorDesc during $timeoutMs ms",
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
    ): UiObject2? {
        var uiobject2: UiObject2? = null
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { uiobject2 = this.findObject(bySelector) },
                    name = "FindObject of $selectorDesc using bySelector $bySelector",
                    type = UiAutomatorActionType.FIND_OBJECT,
                    description = "UiObject2 action '${UiAutomatorActionType.FIND_OBJECT}'. FindObject of $selectorDesc using bySelector $bySelector during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return uiobject2
    }

    /** Searches all elements under this object and returns all objects that match the criteria. */
    fun findObjects(
        bySelector: BySelector,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): List<UiObject2> {
        val objects = mutableListOf<UiObject2>()
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { objects.addAll(this.findObjects(bySelector)) },
                    name = "FindObjects of $selectorDesc using bySelector $bySelector",
                    type = UiAutomatorActionType.FIND_OBJECTS,
                    description = "UiObject2 action '${UiAutomatorActionType.FIND_OBJECTS}'. FindObjects of $selectorDesc using bySelector $bySelector during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return objects
    }

    // Attribute accessors
    /**
     * @return view text
     */
    fun getText(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): String {
        var text: String = ""
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { text = this.text },
                    name = "GetText of $selectorDesc",
                    type = UiAutomatorActionType.GET_TEXT,
                    description = "UiObject2 action '${UiAutomatorActionType.GET_TEXT}'. GetText of $selectorDesc  during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return text
    }

    fun getClassName(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): String {
        var className: String? = null
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { className = this.className },
                    name = "GetClassName of $selectorDesc",
                    type = UiAutomatorActionType.GET_CLASS_NAME,
                    description = "UiObject2 action '${UiAutomatorActionType.GET_CLASS_NAME}'. GetClassName of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return className ?: throw UltronException("Couldn't getClassName of $selectorDesc")
    }

    /** Returns the package name of the app that this object belongs to. */
    fun getApplicationPackage(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): String {
        var packageName: String = ""
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { packageName = this.applicationPackage },
                    name = "GetApplicationPackage of $selectorDesc",
                    type = UiAutomatorActionType.GET_APPLICATION_PACKAGE,
                    description = "UiObject2 action '${UiAutomatorActionType.GET_APPLICATION_PACKAGE}'. GetApplicationPackage of $selectorDesc  during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return packageName
    }

    /** Returns the visible bounds of this object in screen coordinates. */
    fun getVisibleBounds(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): Rect {
        var visibleBounds: Rect? = null
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { visibleBounds = this.visibleBounds },
                    name = "GetVisibleBounds of $selectorDesc",
                    type = UiAutomatorActionType.GET_VISIBLE_BOUNDS,
                    description = "UiObject2 action '${UiAutomatorActionType.GET_VISIBLE_BOUNDS}'. GetVisibleBounds of $selectorDesc  during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return visibleBounds ?: throw UltronException("Couldn't get visibleBounds of $this")
    }

    /** Returns a point in the center of the visible bounds of this object. */
    fun getVisibleCenter(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): Point {
        var visibleCenter: Point? = null
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { visibleCenter = this.visibleCenter },
                    name = "GetVisibleCenter of $selectorDesc",
                    type = UiAutomatorActionType.GET_VISIBLE_CENTER,
                    description = "UiObject2 action '${UiAutomatorActionType.GET_VISIBLE_CENTER}'. GetVisibleCenter of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return visibleCenter ?: throw UltronException("Couldn't get visibleCenter of $this")
    }

    /** Returns the fully qualified resource name for this object's id.  */
    fun getResourceName(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): String {
        var resName: String? = null
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { resName = this.resourceName },
                    name = "GetResourceName of $selectorDesc",
                    type = UiAutomatorActionType.GET_RESOURCE_NAME,
                    description = "UiObject2 action '${UiAutomatorActionType.GET_RESOURCE_NAME}'. GetResourceName of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return resName ?: throw UltronException("Couldn't getResourceName of $this")
    }

    /** Returns the content description for this object. */
    fun getContentDescription(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): String {
        var contentDesc: String? = null
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { contentDesc = this.contentDescription },
                    name = "GetContentDescription of $selectorDesc",
                    type = UiAutomatorActionType.GET_CONTENT_DESCRIPTION,
                    description = "UiObject2 action '${UiAutomatorActionType.GET_CONTENT_DESCRIPTION}'. GetContentDescription of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return contentDesc ?: throw UltronException("Couldn't getContentDescription of $this")
    }

    // Actions
    /** Clicks on this object. */
    fun click(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { this.click() },
                    name = "Click to $selectorDesc",
                    type = UiAutomatorActionType.CLICK,
                    description = "UiObject2 action '${UiAutomatorActionType.CLICK}'. Click to $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun click(
        duration: Long,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { this.click(duration) },
                    name = "Click to $selectorDesc with duration = $duration",
                    type = UiAutomatorActionType.CLICK,
                    description = "UiObject2 action '${UiAutomatorActionType.CLICK}'. Click to $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun longClick(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { this.longClick() },
                    name = "LongClick to $selectorDesc",
                    type = UiAutomatorActionType.LONG_CLICK,
                    description = "UiObject2 action '${UiAutomatorActionType.LONG_CLICK}'. LongClick to $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun clear(
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { this.clear() },
                    name = "Clear of $selectorDesc",
                    type = UiAutomatorActionType.CLEAR_TEXT,
                    description = "UiObject2 action '${UiAutomatorActionType.CLEAR_TEXT}'. Clear of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    /** Add the text content if this object is an editable field. */
    fun addText(
        text: String,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { this.text += text },
                    name = "AddText of $selectorDesc to '$text'",
                    type = UiAutomatorActionType.ADD_TEXT,
                    description = "UiObject2 action '${UiAutomatorActionType.ADD_TEXT}'. AddText of $selectorDesc to '$text' during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    /**
     * Set the text content by sending individual key codes.
     * @hide
     */
    fun legacySetText(
        text: String,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { this.legacySetText(text) },
                    name = "LegacySetText of $selectorDesc to '$text'",
                    type = UiAutomatorActionType.LEGACY_SET_TEXT,
                    description = "UiObject2 action '${UiAutomatorActionType.LEGACY_SET_TEXT}'. LegacySetText of $selectorDesc to '$text' during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    /** Sets the text content if this object is an editable field. */
    fun replaceText(
        text: String,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { this.text = text },
                    name = "ReplaceText of $selectorDesc to '$text'",
                    type = UiAutomatorActionType.REPLACE_TEXT,
                    description = "UiObject2 action '${UiAutomatorActionType.REPLACE_TEXT}'. ReplaceText of $selectorDesc to '$text' during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    /**
     * Drags this object to the specified location.
     *
     * @param dest The end point that this object should be dragged to.
     */
    fun drag(
        dest: Point,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { drag(dest) },
                    name = "Drag of $selectorDesc to dest = '$dest'",
                    type = UiAutomatorActionType.DRAG,
                    description = "UiObject2 action '${UiAutomatorActionType.DRAG}'. Drag of $selectorDesc to dest = '$dest' during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    /**
     * Drags this object to the specified location.
     *
     * @param dest The end point that this object should be dragged to.
     * @param speed The speed at which to perform this gesture in pixels per second.
     */
    fun drag(
        dest: Point,
        speed: Int,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { drag(dest, speed) },
                    name = "Drag of $selectorDesc to dest = '$dest' with speed = $speed",
                    type = UiAutomatorActionType.DRAG,
                    description = "UiObject2 action '${UiAutomatorActionType.DRAG}'. Drag of $selectorDesc to dest = '$dest' with speed = $speed during $timeoutMs ms",
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
    fun pinchClose(
        percent: Float,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { pinchClose(percent) },
                    name = "PinchClose of $selectorDesc with $percent%",
                    type = UiAutomatorActionType.PINCH_CLOSE,
                    description = "UiObject2 action '${UiAutomatorActionType.PINCH_CLOSE}'. PinchClose of $selectorDesc with $percent% during $timeoutMs ms",
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
        speed: Int,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { pinchClose(percent, speed) },
                    name = "PinchClose of $selectorDesc with $percent% and $speed speed",
                    type = UiAutomatorActionType.PINCH_CLOSE,
                    description = "UiObject2 action '${UiAutomatorActionType.PINCH_CLOSE}'. PinchClose of $selectorDesc with $percent% and $speed speed during $timeoutMs ms",
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
    fun pinchOpen(
        percent: Float,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { pinchOpen(percent) },
                    name = "PinchOpen of $selectorDesc with $percent%",
                    type = UiAutomatorActionType.PINCH_OPEN,
                    description = "UiObject2 action '${UiAutomatorActionType.PINCH_OPEN}'. PinchOpen of $selectorDesc with $percent% during $timeoutMs ms",
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
        speed: Int,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { pinchOpen(percent, speed) },
                    name = "PinchOpen of $selectorDesc with $percent% and $speed speed",
                    type = UiAutomatorActionType.PINCH_OPEN,
                    description = "UiObject2 action '${UiAutomatorActionType.PINCH_OPEN}'. PinchOpen of $selectorDesc with $percent% and $speed speed during $timeoutMs ms",
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
    fun swipe(
        direction: Direction,
        percent: Float,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { swipe(direction, percent) },
                    name = "Swipe of $selectorDesc to direction = '${direction.name}' with $percent%",
                    type = UiAutomatorActionType.SWIPE,
                    description = "UiObject2 action '${UiAutomatorActionType.SWIPE}'. Swipe of $selectorDesc to direction = '${direction.name}' with $percent% during $timeoutMs ms",
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
    fun swipe(
        direction: Direction,
        percent: Float,
        speed: Int,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { swipe(direction, percent, speed) },
                    name = "Swipe of $selectorDesc to direction = '${direction.name}' with $percent% and $speed speed",
                    type = UiAutomatorActionType.SWIPE,
                    description = "UiObject2 action '${UiAutomatorActionType.SWIPE}'. Swipe of $selectorDesc to direction = '${direction.name}' with $percent% and $speed speed during $timeoutMs ms",
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
    fun scroll(
        direction: Direction,
        percent: Float,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): Boolean {
        var result = false
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { result = scroll(direction, percent) },
                    name = "Scroll of $selectorDesc to direction = '${direction.name}' with $percent%",
                    type = UiAutomatorActionType.SCROLL,
                    description = "UiObject2 action '${UiAutomatorActionType.SCROLL}'. Scroll of $selectorDesc to direction = '${direction.name}' with $percent% during $timeoutMs ms",
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
    fun scroll(
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
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { result = scroll(direction, percent, speed) },
                    name = "Scroll of $selectorDesc to direction = '${direction.name}' with $percent% and $speed speed",
                    type = UiAutomatorActionType.SCROLL,
                    description = "UiObject2 action '${UiAutomatorActionType.SCROLL}'. Scroll of $selectorDesc to direction = '${direction.name}' with $percent% and $speed speed during $timeoutMs ms",
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
    fun fling(
        direction: Direction,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): Boolean {
        var result: Boolean = false
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { result = fling(direction) },
                    name = "FLing of $selectorDesc to direction = '${direction.name}'",
                    type = UiAutomatorActionType.FLING,
                    description = "UiObject2 action '${UiAutomatorActionType.FLING}'. FLing of $selectorDesc to  to direction = '${direction.name}' during $timeoutMs ms",
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
    fun fling(
        direction: Direction,
        speed: Int,
        timeoutMs: Long = UltronConfig.UiAutomator.ACTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorActionResultHandler
    ): Boolean {
        var result: Boolean = false
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = { result = fling(direction, speed) },
                    name = "FLing of $selectorDesc to direction = '${direction.name}' with speed = $speed",
                    type = UiAutomatorActionType.FLING,
                    description = "UiObject2 action '${UiAutomatorActionType.FLING}'. FLing of $selectorDesc to direction = '${direction.name}' with speed = $speed during $timeoutMs ms",
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
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorActionExecutor(
                UiAutomatorBySelectorAction(
                    objectBlock = uiObject2ProviderBlock,
                    actionBlock = actionBlock,
                    name = "Perform custom action '$actionDescription' on $selectorDesc.",
                    type = UiAutomatorActionType.PERFORM,
                    description = "UiObject2 action '${UiAutomatorActionType.PERFORM}'. Perform custom action '$actionDescription' on $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    //asserts
    fun hasText(
        text: String,
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { this.text == text },
                    name = "HasText $text in $selectorDesc",
                    type = UiAutomatorAssertionType.HAS_TEXT,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.HAS_TEXT}'. HasText '$text' in $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun hasText(
        textMatcher: Matcher<String>,
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { textMatcher.matches(this.text) },
                    name = "HasText $textMatcher in $selectorDesc",
                    type = UiAutomatorAssertionType.HAS_TEXT,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.HAS_TEXT}'.  HasText matches '$textMatcher' in $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun textContains(
        textSubstring: String,
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { this.text.contains(textSubstring) },
                    name = "TextContains $textSubstring in $selectorDesc",
                    type = UiAutomatorAssertionType.CONTAINS_TEXT,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.CONTAINS_TEXT}'. TextContains '$textSubstring' in $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun hasContentDescription(
        contentDesc: String,
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { this.contentDescription == contentDesc },
                    name = "HasContentDescription $contentDesc in $selectorDesc",
                    type = UiAutomatorAssertionType.HAS_CONTENT_DESCRIPTION,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.HAS_CONTENT_DESCRIPTION}'. HasContentDescription '$contentDesc' in $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun hasContentDescription(
        contentDescMatcher: Matcher<String>,
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { contentDescMatcher.matches(this.contentDescription) },
                    name = "HasContentDescription $contentDescMatcher in $selectorDesc",
                    type = UiAutomatorAssertionType.HAS_CONTENT_DESCRIPTION,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.HAS_CONTENT_DESCRIPTION}'. HasContentDescription matches '$contentDescMatcher' in $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun contentDescriptionContains(
        contentDescSubstring: String,
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { this.contentDescription.contains(contentDescSubstring) },
                    name = "ContentDescriptionContains $contentDescSubstring in $selectorDesc",
                    type = UiAutomatorAssertionType.CONTENT_DESCRIPTION_CONTAINS_TEXT,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.CONTENT_DESCRIPTION_CONTAINS_TEXT}'. ContentDescriptionContains '$contentDescSubstring' in $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isCheckable(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { this.isCheckable },
                    name = "IsCheckable of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_CHECKABLE,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_CHECKABLE}'. IsCheckable of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isNotCheckable(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { !this.isCheckable },
                    name = "IsNotCheckable of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_NOT_CHECKABLE,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_CHECKABLE}'. IsNotCheckable of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isChecked(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { this.isChecked },
                    name = "IsChecked of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_CHECKED,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_CHECKED}'. IsChecked of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isNotChecked(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { !this.isChecked },
                    name = "IsNotChecked of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_NOT_CHECKED,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_CHECKED}'. IsNotChecked of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isClickable(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { this.isClickable },
                    name = "IsClickable of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_CLICKABLE,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_CLICKABLE}'. IsClickable of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isNotClickable(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { !this.isClickable },
                    name = "IsNotClickable of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_NOT_CLICKABLE,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_CLICKABLE}'. IsNotClickable of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isEnabled(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { this.isEnabled },
                    name = "IsEnabled of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_ENABLED,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_ENABLED}'. IsEnabled of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isNotEnabled(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { !this.isEnabled },
                    name = "IsNotEnabled of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_NOT_ENABLED,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_ENABLED}'. IsNotEnabled of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isFocusable(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { this.isFocusable },
                    name = "IsFocusable of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_FOCUSABLE,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_FOCUSABLE}'. IsFocusable of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isNotFocusable(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { !this.isFocusable },
                    name = "IsNotFocusable of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_NOT_FOCUSABLE,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_FOCUSABLE}'. IsNotFocusable of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isFocused(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { this.isFocused },
                    name = "IsFocused of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_FOCUSED,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_FOCUSED}'. IsFocused of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isNotFocused(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { !this.isFocused },
                    name = "IsNotFocused of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_NOT_FOCUSED,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_FOCUSED}'. IsNotFocused of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isLongClickable(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { this.isLongClickable },
                    name = "IsLongClickable of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_LONG_CLICKABLE,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_LONG_CLICKABLE}'. IsLongClickable of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isNotLongClickable(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { !this.isLongClickable },
                    name = "IsNotLongClickable of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_NOT_LONG_CLICKABLE,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_LONG_CLICKABLE}'. IsNotLongClickable of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isScrollable(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { this.isScrollable },
                    name = "IsScrollable of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_SCROLLABLE,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_SCROLLABLE}'. IsScrollable of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isNotScrollable(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { !this.isScrollable },
                    name = "IsNotScrollable of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_NOT_SCROLLABLE,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_SCROLLABLE}'. IsNotScrollable of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isSelected(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { this.isSelected },
                    name = "IsSelected of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_SELECTED,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_SELECTED}'. IsSelected of $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    fun isNotSelected(
        timeoutMs: Long = UltronConfig.UiAutomator.ASSERTION_TIMEOUT,
        resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit = UltronConfig.UiAutomator.UiObject2Config.bySelectorAssertionResultHandler
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = { !this.isSelected },
                    name = "IsNotSelected of $selectorDesc",
                    type = UiAutomatorAssertionType.IS_NOT_SELECTED,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_SELECTED}'. IsNotSelected of $selectorDesc during $timeoutMs ms",
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
    ) {
        UiAutomatorLifecycle.execute(
            UiAutomatorBySelectorAssertionExecutor(
                UiAutomatorBySelectorAssertion(
                    objectBlock = uiObject2ProviderBlock,
                    assertionBlock = assertBlock,
                    name = "AssertThat $assertionDescription in $selectorDesc",
                    type = UiAutomatorAssertionType.ASSERT_THAT,
                    description = "UiObject2 assertion '${UiAutomatorAssertionType.ASSERT_THAT}'. AssertThat $assertionDescription in $selectorDesc during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }
}