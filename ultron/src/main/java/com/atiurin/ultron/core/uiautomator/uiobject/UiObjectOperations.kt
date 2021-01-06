package com.atiurin.ultron.core.uiautomator.uiobject

import android.graphics.Rect
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import com.atiurin.ultron.core.uiautomator.UiAutomatorActionType
import com.atiurin.ultron.core.uiautomator.UiAutomatorAssertionType
import com.atiurin.ultron.core.uiautomator.UiAutomatorLifecycle
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperationResult
import com.atiurin.ultron.exceptions.UltronException

internal class UiObjectOperations {
    companion object {
        fun getChild(
            block: () -> UiObject,
            selectorDesc: String,
            uiSelector: UiSelector,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ): UiObject {
            var uiObject: UiObject? = null
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = {
                            uiObject = this.getChild(selector)
                            uiObject != null
                        },
                        name = "GetChild of $selectorDesc with uiSelector $uiSelector",
                        type = UiAutomatorActionType.GET_CHILD,
                        description = "UiObject action '${UiAutomatorActionType.GET_CHILD}'. GetChild of $selectorDesc with uiSelector $uiSelector during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
            return uiObject
                ?: throw UltronException("Couldn't getChild of $selectorDesc with selector $uiSelector")
        }

        fun getFromParent(
            block: () -> UiObject,
            selectorDesc: String,
            uiSelector: UiSelector,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ): UiObject {
            var uiObject: UiObject? = null
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = {
                            uiObject = this.getFromParent(selector)
                            uiObject != null
                        },
                        name = "GetFromParent of $selectorDesc with uiSelector $uiSelector",
                        type = UiAutomatorActionType.GET_FROM_PARENT,
                        description = "UiObject action '${UiAutomatorActionType.GET_FROM_PARENT}'. GetFromParent of $selectorDesc with uiSelector $uiSelector during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
            return uiObject
                ?: throw UltronException("Couldn't getFromParent of $selectorDesc with selector $uiSelector")
        }

        fun getChildCount(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ): Int {
            var count = 0
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = {
                            count = this.childCount
                            true
                        },
                        name = "GetChildCount of $selectorDesc",
                        type = UiAutomatorActionType.GET_CHILD_COUNT,
                        description = "UiObject action '${UiAutomatorActionType.GET_CHILD_COUNT}'. GetChildCount of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
            return count
        }

        fun getClassName(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ): String {
            var className: String? = null
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = {
                            className = this.className
                            true
                        },
                        name = "GetClassName of $selectorDesc",
                        type = UiAutomatorActionType.GET_CLASS_NAME,
                        description = "UiObject action '${UiAutomatorActionType.GET_CLASS_NAME}'. GetClassName of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
            return className ?: throw UltronException("Couldn't get className of $selectorDesc")
        }

        fun getContentDescription(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ): String {
            var contentDesc: String? = null
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = {
                            contentDesc = this.contentDescription
                            true
                        },
                        name = "GetContentDescription of $selectorDesc",
                        type = UiAutomatorActionType.GET_CONTENT_DESCRIPTION,
                        description = "UiObject action '${UiAutomatorActionType.GET_CONTENT_DESCRIPTION}'. GetContentDescription of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
            return contentDesc
                ?: throw UltronException("Couldn't get contentDescription of $selectorDesc")
        }

        fun getPackageName(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ): String {
            var packageName: String? = null
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = {
                            packageName = this.packageName
                            true
                        },
                        name = "GetPackageName of $selectorDesc",
                        type = UiAutomatorActionType.GET_APPLICATION_PACKAGE,
                        description = "UiObject action '${UiAutomatorActionType.GET_APPLICATION_PACKAGE}'. GetPackageName of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
            return packageName ?: throw UltronException("Couldn't get packageName of $selectorDesc")
        }

        fun getVisibleBounds(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ): Rect {
            var bounds: Rect? = null
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = {
                            bounds = this.visibleBounds
                            bounds != null
                        },
                        name = "GetVisibleBounds of $selectorDesc",
                        type = UiAutomatorActionType.GET_VISIBLE_BOUNDS,
                        description = "UiObject action '${UiAutomatorActionType.GET_VISIBLE_BOUNDS}'. GetVisibleBounds of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
            return bounds ?: throw UltronException("Couldn't get visibleBounds of $selectorDesc")
        }

        fun getBounds(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ): Rect {
            var bounds: Rect? = null
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = {
                            bounds = this.bounds
                            bounds != null
                        },
                        name = "GetBounds of $selectorDesc",
                        type = UiAutomatorActionType.GET_BOUNDS,
                        description = "UiObject action '${UiAutomatorActionType.GET_BOUNDS}'. GetBounds of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
            return bounds ?: throw UltronException("Couldn't get bounds of $selectorDesc")
        }

        fun getText(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ): String {
            var text: String? = null
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = {
                            text = this.text
                            true
                        },
                        name = "GetText of $selectorDesc",
                        type = UiAutomatorActionType.GET_TEXT,
                        description = "UiObject action '${UiAutomatorActionType.GET_TEXT}'. GetText of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
            return text ?: throw UltronException("Couldn't get text of $selectorDesc")
        }

        fun clearTextField(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = {
                            this.clearTextField()
                            true
                        },
                        name = "ClearTextField of $selectorDesc",
                        type = UiAutomatorActionType.CLEAR_TEXT,
                        description = "UiObject action '${UiAutomatorActionType.CLEAR_TEXT}'. ClearTextField of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun replaceText(
            block: () -> UiObject,
            selectorDesc: String,
            text: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = {
                            this.text = text
                            true
                        },
                        name = "ReplaceText of $selectorDesc to '$text'",
                        type = UiAutomatorActionType.REPLACE_TEXT,
                        description = "UiObject action '${UiAutomatorActionType.REPLACE_TEXT}'. ReplaceText of $selectorDesc to '$text' during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun legacySetText(
            block: () -> UiObject,
            selectorDesc: String,
            text: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = {
                            this.legacySetText(text)
                            true
                        },
                        name = "LegacySetText of $selectorDesc to '$text'",
                        type = UiAutomatorActionType.LEGACY_SET_TEXT,
                        description = "UiObject action '${UiAutomatorActionType.LEGACY_SET_TEXT}'. LegacySetText of $selectorDesc to '$text' during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun click(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { click() },
                        name = "Click to $selectorDesc",
                        type = UiAutomatorActionType.CLICK,
                        description = "UiObject action '${UiAutomatorActionType.CLICK}'. Click to $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun clickAndWaitForNewWindow(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { clickAndWaitForNewWindow() },
                        name = "ClickAndWaitForNewWindow to $selectorDesc",
                        type = UiAutomatorActionType.CLICK_AND_WAIT_FOR_NEW_WINDOW,
                        description = "UiObject action '${UiAutomatorActionType.CLICK_AND_WAIT_FOR_NEW_WINDOW}'. ClickAndWaitForNewWindow to $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun clickAndWaitForNewWindow(
            block: () -> UiObject,
            selectorDesc: String,
            waitWindowTimeout: Long,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { clickAndWaitForNewWindow(waitWindowTimeout) },
                        name = "ClickAndWaitForNewWindow to $selectorDesc with waitWindowTimeout = $waitWindowTimeout ms",
                        type = UiAutomatorActionType.CLICK_AND_WAIT_FOR_NEW_WINDOW,
                        description = "UiObject action '${UiAutomatorActionType.CLICK_AND_WAIT_FOR_NEW_WINDOW}'. ClickAndWaitForNewWindow to $selectorDesc  with waitWindowTimeout = $waitWindowTimeout ms during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun clickTopLeft(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { clickTopLeft() },
                        name = "ClickTopLeft to $selectorDesc",
                        type = UiAutomatorActionType.CLICK_TOP_LEFT,
                        description = "UiObject action '${UiAutomatorActionType.CLICK_TOP_LEFT}'. ClickTopLeft to $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun clickBottomRight(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { clickBottomRight() },
                        name = "ClickBottomRight to $selectorDesc",
                        type = UiAutomatorActionType.CLICK_BOTTOM_RIGHT,
                        description = "UiObject action '${UiAutomatorActionType.CLICK_BOTTOM_RIGHT}'. ClickBottomRight to $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun longClick(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { longClick() },
                        name = "LongClick to $selectorDesc",
                        type = UiAutomatorActionType.LONG_CLICK,
                        description = "UiObject action '${UiAutomatorActionType.LONG_CLICK}'. LongClick to $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun longClickTopLeft(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { longClickTopLeft() },
                        name = "LongClickTopLeft to $selectorDesc",
                        type = UiAutomatorActionType.LONG_CLICK_TOP_LEFT,
                        description = "UiObject action '${UiAutomatorActionType.LONG_CLICK_TOP_LEFT}'. LongClickTopLeft to $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun longClickBottomRight(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { longClickBottomRight() },
                        name = "LongClickBottomRight to $selectorDesc",
                        type = UiAutomatorActionType.LONG_CLICK_BOTTOM_RIGHT,
                        description = "UiObject action '${UiAutomatorActionType.LONG_CLICK_BOTTOM_RIGHT}'. LongClickBottomRight to $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun dragTo(
            destObj: UiObject,
            steps: Int,
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { dragTo(destObj, steps) },
                        name = "DragTo of $selectorDesc destObj ${destObj.selector}  with $steps steps",
                        type = UiAutomatorActionType.DRAG,
                        description = "UiObject action '${UiAutomatorActionType.DRAG}'. DragTo of $selectorDesc destObj ${destObj.selector}  with $steps steps during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun dragTo(
            destX: Int,
            destY: Int,
            steps: Int,
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { dragTo(destX, destY, steps) },
                        name = "DragTo of $selectorDesc to destX = $destX, destY = $destY with $steps steps",
                        type = UiAutomatorActionType.DRAG,
                        description = "UiObject action '${UiAutomatorActionType.DRAG}'. DragTo of $selectorDesc to destX = $destX, destY = $destY with $steps steps during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun swipeUp(
            steps: Int,
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { swipeUp(steps) },
                        name = "SwipeUp of $selectorDesc with $steps steps",
                        type = UiAutomatorActionType.SWIPE_UP,
                        description = "UiObject action '${UiAutomatorActionType.SWIPE_UP}'. SwipeUp of $selectorDesc with $steps steps during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun swipeDown(
            steps: Int,
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { swipeDown(steps) },
                        name = "SwipeDown of $selectorDesc with $steps steps",
                        type = UiAutomatorActionType.SWIPE_DOWN,
                        description = "UiObject action '${UiAutomatorActionType.SWIPE_DOWN}'. SwipeDown of $selectorDesc with $steps steps during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun swipeLeft(
            steps: Int,
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { swipeLeft(steps) },
                        name = "SwipeLeft of $selectorDesc with $steps steps",
                        type = UiAutomatorActionType.SWIPE_LEFT,
                        description = "UiObject action '${UiAutomatorActionType.SWIPE_LEFT}'. SwipeLeft of $selectorDesc with $steps steps during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun swipeRight(
            steps: Int,
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { swipeRight(steps) },
                        name = "SwipeRight of $selectorDesc with $steps steps",
                        type = UiAutomatorActionType.SWIPE_RIGHT,
                        description = "UiObject action '${UiAutomatorActionType.SWIPE_RIGHT}'. SwipeRight of $selectorDesc with $steps steps during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun pinchOut(
            percent: Int,
            steps: Int,
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { pinchOut(percent, steps) },
                        name = "PinchOut of $selectorDesc with $percent% and $steps steps",
                        type = UiAutomatorActionType.PINCH_OUT,
                        description = "UiObject action '${UiAutomatorActionType.PINCH_OUT}'. PinchOut of $selectorDesc with $percent% and $steps steps steps during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun pinchIn(
            percent: Int,
            steps: Int,
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { pinchIn(percent, steps) },
                        name = "PinchIn of $selectorDesc with $percent% and $steps steps",
                        type = UiAutomatorActionType.PINCH_IN,
                        description = "UiObject action '${UiAutomatorActionType.PINCH_IN}'. PinchIn of $selectorDesc with $percent% and $steps steps steps during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }


        fun exists(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = {
                            this.exists()
                        },
                        name = "Exists of $selectorDesc",
                        type = UiAutomatorAssertionType.EXISTS,
                        description = "UiObject assertion '${UiAutomatorAssertionType.EXISTS}'. Exists of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun notExists(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = {
                            !this.exists()
                        },
                        name = "NotExists of $selectorDesc",
                        type = UiAutomatorAssertionType.NOT_EXISTS,
                        description = "UiObject assertion '${UiAutomatorAssertionType.NOT_EXISTS}'. NotExists of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isCheckable(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { this.isCheckable },
                        name = "IsCheckable of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_CHECKABLE,
                        description = "UiObject assertion '${UiAutomatorAssertionType.IS_CHECKABLE}'. IsCheckable of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isNotCheckable(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { !this.isCheckable },
                        name = "IsNotCheckable of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_NOT_CHECKABLE,
                        description = "UiObject assertion '${UiAutomatorAssertionType.IS_NOT_CHECKABLE}'. IsNotCheckable of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isChecked(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { this.isChecked },
                        name = "IsChecked of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_CHECKED,
                        description = "UiObject assertion '${UiAutomatorAssertionType.IS_CHECKED}'. IsChecked of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isNotChecked(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { !this.isChecked },
                        name = "IsNotChecked of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_NOT_CHECKED,
                        description = "UiObject assertion '${UiAutomatorAssertionType.IS_NOT_CHECKED}'. IsNotChecked of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isClickable(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { this.isClickable },
                        name = "IsClickable of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_CLICKABLE,
                        description = "UiObject assertion '${UiAutomatorAssertionType.IS_CLICKABLE}'. IsClickable of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isNotClickable(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { !this.isClickable },
                        name = "IsNotClickable of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_NOT_CLICKABLE,
                        description = "UiObject assertion '${UiAutomatorAssertionType.IS_NOT_CLICKABLE}'. IsNotClickable of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isEnabled(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { this.isEnabled },
                        name = "IsEnabled of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_ENABLED,
                        description = "UiObject assertion '${UiAutomatorAssertionType.IS_ENABLED}'. IsEnabled of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isNotEnabled(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { !this.isEnabled },
                        name = "IsNotEnabled of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_NOT_ENABLED,
                        description = "UiObject assertion '${UiAutomatorAssertionType.IS_NOT_ENABLED}'. IsNotEnabled of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isFocusable(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { this.isFocusable },
                        name = "IsFocusable of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_FOCUSABLE,
                        description = "UiObject assertion '${UiAutomatorAssertionType.IS_FOCUSABLE}'. IsFocusable of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isNotFocusable(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { !this.isFocusable },
                        name = "IsNotFocusable of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_NOT_FOCUSABLE,
                        description = "UiObject assertion '${UiAutomatorAssertionType.IS_NOT_FOCUSABLE}'. IsNotFocusable of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isFocused(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { this.isFocused },
                        name = "IsFocused of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_FOCUSED,
                        description = "UiObject assertion '${UiAutomatorAssertionType.IS_FOCUSED}'. IsFocused of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isNotFocused(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { !this.isFocused },
                        name = "IsNotFocused of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_NOT_FOCUSED,
                        description = "UiObject assertion '${UiAutomatorAssertionType.IS_NOT_FOCUSED}'. IsNotFocused of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isLongClickable(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { this.isLongClickable },
                        name = "IsLongClickable of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_LONG_CLICKABLE,
                        description = "UiObject assertion '${UiAutomatorAssertionType.IS_LONG_CLICKABLE}'. IsLongClickable of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isNotLongClickable(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { !this.isLongClickable },
                        name = "IsNotLongClickable of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_NOT_LONG_CLICKABLE,
                        description = "UiObject assertion '${UiAutomatorAssertionType.IS_NOT_LONG_CLICKABLE}'. IsNotLongClickable of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isScrollable(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { this.isScrollable },
                        name = "IsScrollable of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_SCROLLABLE,
                        description = "UiObject assertion '${UiAutomatorAssertionType.IS_SCROLLABLE}'. IsScrollable of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isNotScrollable(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { !this.isScrollable },
                        name = "IsNotScrollable of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_NOT_SCROLLABLE,
                        description = "UiObject assertion '${UiAutomatorAssertionType.IS_NOT_SCROLLABLE}'. IsNotScrollable of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isSelected(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { this.isSelected },
                        name = "IsSelected of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_SELECTED,
                        description = "UiObject assertion '${UiAutomatorAssertionType.IS_SELECTED}'. IsSelected of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        fun isNotSelected(
            block: () -> UiObject,
            selectorDesc: String,
            timeoutMs: Long,
            resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit
        ) {
            UiAutomatorLifecycle.execute(
                UiAutomatorUiSelectorOperationExecutor(
                    UiAutomatorUiSelectorOperation(
                        objectBlock = block,
                        operationBlock = { !this.isSelected },
                        name = "IsNotSelected of $selectorDesc",
                        type = UiAutomatorAssertionType.IS_NOT_SELECTED,
                        description = "UiObject assertion '${UiAutomatorAssertionType.IS_NOT_SELECTED}'. IsNotSelected of $selectorDesc during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }
    }
}