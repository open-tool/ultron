package com.atiurin.ultron.core.uiautomator.uiobject2

import android.graphics.Point
import android.graphics.Rect
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiObject2
import com.atiurin.ultron.core.uiautomator.UiAutomatorActionType
import com.atiurin.ultron.core.uiautomator.UiAutomatorAssertionType
import com.atiurin.ultron.core.uiautomator.UiAutomatorLifecycle
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperationResult
import com.atiurin.ultron.exceptions.UltronException
import org.hamcrest.Matcher

internal class UiObject2Operation {
    companion object {
        // Search functions
        /** Returns this object's parent, or null if it has no parent. */
        fun getParent(
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ): UiObject2? {
            var uiobject2: UiObject2? = null
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ): List<UiObject2> {
            val children = mutableListOf<UiObject2>()
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ): Int {
            var count = 0
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ): UiObject2? {
            var uiobject2: UiObject2? = null
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ): List<UiObject2> {
            val objects = mutableListOf<UiObject2>()
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ): String {
            var text: String = ""
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ): String {
            var className: String? = null
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ): String {
            var packageName: String = ""
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ): Rect {
            var visibleBounds: Rect? = null
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ): Point {
            var visibleCenter: Point? = null
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ): String {
            var resName: String? = null
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ): String {
            var contentDesc: String? = null
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            duration: Long,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            text: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            text: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            text: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            dest: Point,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            dest: Point,
            speed: Int,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            percent: Float,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            percent: Float,
            speed: Int,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            percent: Float,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            percent: Float,
            speed: Int,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            direction: Direction,
            percent: Float,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            direction: Direction,
            percent: Float,
            speed: Int,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            direction: Direction,
            percent: Float,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ): Boolean {
            var result = false
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            direction: Direction,
            percent: Float,
            speed: Int,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ): Boolean {
            var result = false
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            direction: Direction,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ): Boolean {
            var result: Boolean = false
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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
            block: () -> UiObject2,
            selectorDesc: String,
            direction: Direction,
            speed: Int,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAction>) -> Unit
        ): Boolean {
            var result: Boolean = false
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorActionExecutor(
                    UiAutomatorBySelectorAction(
                        objectBlock = block,
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


        //asserts
        fun hasText(
            block: () -> UiObject2,
            selectorDesc: String,
            text: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { this.text == text },
                        name = "HasText $text in $selectorDesc",
                        type = UiAutomatorAssertionType.HAS_TEXT,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.HAS_TEXT}'. HasText '$text' in $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun hasText(
            block: () -> UiObject2,
            selectorDesc: String,
            textMatcher: Matcher<String>,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { textMatcher.matches(this.text) },
                        name = "HasText $textMatcher in $selectorDesc",
                        type = UiAutomatorAssertionType.HAS_TEXT,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.HAS_TEXT}'.  HasText matches '$textMatcher' in $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun textContains(
            block: () -> UiObject2,
            selectorDesc: String,
            textSubstring: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { this.text.contains(textSubstring) },
                        name = "TextContains $textSubstring in $selectorDesc",
                        type = UiAutomatorAssertionType.CONTAINS_TEXT,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.CONTAINS_TEXT}'. TextContains '$textSubstring' in $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun hasContentDescription(
            block: () -> UiObject2,
            selectorDesc: String,
            contentDesc: String,
            timeoutMs: Long ,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { this.contentDescription == contentDesc },
                        name = "HasContentDescription $contentDesc in $selectorDesc",
                        type = UiAutomatorAssertionType.HAS_CONTENT_DESCRIPTION,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.HAS_CONTENT_DESCRIPTION}'. HasContentDescription '$contentDesc' in $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun hasContentDescription(
            block: () -> UiObject2,
            selectorDesc: String,
            contentDescMatcher: Matcher<String>,
            timeoutMs: Long ,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { contentDescMatcher.matches(this.contentDescription) },
                        name = "HasContentDescription $contentDescMatcher in $selectorDesc",
                        type = UiAutomatorAssertionType.HAS_CONTENT_DESCRIPTION,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.HAS_CONTENT_DESCRIPTION}'. HasContentDescription matches '$contentDescMatcher' in $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun contentDescriptionContains(
            block: () -> UiObject2,
            selectorDesc: String,
            contentDescSubstring: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { this.contentDescription.contains(contentDescSubstring) },
                        name = "ContentDescriptionContains $contentDescSubstring in $selectorDesc",
                        type = UiAutomatorAssertionType.CONTENT_DESCRIPTION_CONTAINS_TEXT,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.CONTENT_DESCRIPTION_CONTAINS_TEXT}'. ContentDescriptionContains '$contentDescSubstring' in $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isCheckable(
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { this.isCheckable },
                        name = "IsCheckable of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_CHECKABLE,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_CHECKABLE}'. IsCheckable of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isNotCheckable(
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { !this.isCheckable },
                        name = "IsNotCheckable of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_NOT_CHECKABLE,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_CHECKABLE}'. IsNotCheckable of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isChecked(
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { this.isChecked },
                        name = "IsChecked of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_CHECKED,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_CHECKED}'. IsChecked of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isNotChecked(
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { !this.isChecked },
                        name = "IsNotChecked of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_NOT_CHECKED,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_CHECKED}'. IsNotChecked of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isClickable(
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { this.isClickable },
                        name = "IsClickable of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_CLICKABLE,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_CLICKABLE}'. IsClickable of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isNotClickable(
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { !this.isClickable },
                        name = "IsNotClickable of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_NOT_CLICKABLE,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_CLICKABLE}'. IsNotClickable of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isEnabled(
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { this.isEnabled },
                        name = "IsEnabled of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_ENABLED,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_ENABLED}'. IsEnabled of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isNotEnabled(
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { !this.isEnabled },
                        name = "IsNotEnabled of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_NOT_ENABLED,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_ENABLED}'. IsNotEnabled of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isFocusable(
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { this.isFocusable },
                        name = "IsFocusable of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_FOCUSABLE,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_FOCUSABLE}'. IsFocusable of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isNotFocusable(
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { !this.isFocusable },
                        name = "IsNotFocusable of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_NOT_FOCUSABLE,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_FOCUSABLE}'. IsNotFocusable of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isFocused(
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { this.isFocused },
                        name = "IsFocused of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_FOCUSED,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_FOCUSED}'. IsFocused of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isNotFocused(
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { !this.isFocused },
                        name = "IsNotFocused of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_NOT_FOCUSED,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_FOCUSED}'. IsNotFocused of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isLongClickable(
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { this.isLongClickable },
                        name = "IsLongClickable of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_LONG_CLICKABLE,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_LONG_CLICKABLE}'. IsLongClickable of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isNotLongClickable(
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { !this.isLongClickable },
                        name = "IsNotLongClickable of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_NOT_LONG_CLICKABLE,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_LONG_CLICKABLE}'. IsNotLongClickable of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isScrollable(
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { this.isScrollable },
                        name = "IsScrollable of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_SCROLLABLE,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_SCROLLABLE}'. IsScrollable of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isNotScrollable(
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { !this.isScrollable },
                        name = "IsNotScrollable of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_NOT_SCROLLABLE,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_SCROLLABLE}'. IsNotScrollable of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isSelected(
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { this.isSelected },
                        name = "IsSelected of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_SELECTED,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_SELECTED}'. IsSelected of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isNotSelected(
            block: () -> UiObject2,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorBySelectorAssertion>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorBySelectorAssertionExecutor(
                    UiAutomatorBySelectorAssertion(
                        objectBlock = block,
                        actionBlock = { !this.isSelected },
                        name = "IsNotSelected of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_NOT_SELECTED,
                        description = "UiObject2 assertion '${UiAutomatorAssertionType.IS_NOT_SELECTED}'. IsNotSelected of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }
    }
}