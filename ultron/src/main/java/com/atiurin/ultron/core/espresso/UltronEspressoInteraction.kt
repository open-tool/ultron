package com.atiurin.ultron.core.espresso

import android.view.View
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.Root
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.EspressoKey
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import com.atiurin.ultron.core.common.CommonOperationType
import com.atiurin.ultron.core.common.UltronOperationType
import com.atiurin.ultron.core.common.assertion.DefaultOperationAssertion
import com.atiurin.ultron.core.common.assertion.EmptyOperationAssertion
import com.atiurin.ultron.core.common.assertion.OperationAssertion
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.action.EspressoActionExecutor
import com.atiurin.ultron.core.espresso.action.EspressoActionType
import com.atiurin.ultron.core.espresso.action.UltronEspressoActionParams
import com.atiurin.ultron.core.espresso.action.UltronCustomClickAction
import com.atiurin.ultron.core.espresso.assertion.EspressoAssertionExecutor
import com.atiurin.ultron.core.espresso.assertion.EspressoAssertionType
import com.atiurin.ultron.core.espresso.assertion.UltronEspressoAssertionParams
import com.atiurin.ultron.custom.espresso.action.AnonymousViewAction
import com.atiurin.ultron.custom.espresso.assertion.ExistsEspressoViewAssertion
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.extensions.getDataMatcher
import com.atiurin.ultron.extensions.getRootMatcher
import com.atiurin.ultron.extensions.getViewMatcher
import com.atiurin.ultron.extensions.simpleClassName
import com.atiurin.ultron.listeners.setListenersState
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import java.util.concurrent.atomic.AtomicReference

@Suppress("MemberVisibilityCanBePrivate")
class UltronEspressoInteraction<T>(
    val interaction: T,
    val timeoutMs: Long? = null,
    val resultHandler: ((EspressoOperationResult<UltronEspressoOperation>) -> Unit)? = null,
    val assertion: OperationAssertion = EmptyOperationAssertion()
) {
    init {
        if (interaction !is ViewInteraction && interaction !is DataInteraction) throw UltronException(
            "Invalid interaction class provided ${interaction.simpleClassName()}. Use ViewInteraction or DataInteraction"
        )
    }

    fun inRoot(rootMatcher: Matcher<Root>) = apply {
        when (interaction) {
            is ViewInteraction -> {
                interaction.inRoot(rootMatcher)
            }

            is DataInteraction -> {
                interaction.inRoot(rootMatcher)
            }

            else -> throw UltronException("Unknown type of interaction provided!")
        }
    }

    fun getActionTimeout(): Long = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT

    fun getAssertionTimeout(): Long = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT

    fun withResultHandler(resultHandler: (EspressoOperationResult<UltronEspressoOperation>) -> Unit): UltronEspressoInteraction<T> {
        return UltronEspressoInteraction(this.interaction, this.timeoutMs, resultHandler)
    }

    fun withTimeout(timeoutMs: Long): UltronEspressoInteraction<T> {
        return UltronEspressoInteraction(this.interaction, timeoutMs, this.resultHandler)
    }

    fun withAssertion(assertion: OperationAssertion) = UltronEspressoInteraction(interaction, timeoutMs, resultHandler, assertion)
    fun withAssertion(name: String = "", isListened: Boolean = false, block: () -> Unit) =
        UltronEspressoInteraction(interaction, timeoutMs, resultHandler, DefaultOperationAssertion(name, block.setListenersState(isListened)))

    // =========== CUSTOM CLICKS ============================
    private fun customClick(type: EspressoActionType, location: GeneralLocation, offsetX: Int, offsetY: Int) = apply {
        executeAction(
            operationBlock = getInteractionActionBlock(
                UltronCustomClickAction(Tap.SINGLE, location, areaPercentage = 20, offsetX = offsetX, offsetY = offsetY)
            ),
            name = "$type for '${getInteractionMatcher()}'",
            type = type,
            description = "${interaction.simpleClassName()} action '${type}' to '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms"
        )
    }

    fun clickTopLeft(offsetX: Int, offsetY: Int) = apply {
        customClick(EspressoActionType.CLICK_TOP_LEFT, GeneralLocation.TOP_LEFT, offsetX = offsetX, offsetY = offsetY)
    }

    fun clickTopCenter(offsetY: Int) = apply {
        customClick(EspressoActionType.CLICK_TOP_CENTER, GeneralLocation.TOP_CENTER, offsetY = offsetY, offsetX = 0)
    }

    fun clickTopRight(offsetX: Int, offsetY: Int) = apply {
        customClick(EspressoActionType.CLICK_TOP_RIGHT, GeneralLocation.TOP_RIGHT, offsetY = offsetY, offsetX = offsetX)
    }

    fun clickCenterRight(offsetX: Int) = apply {
        customClick(EspressoActionType.CLICK_CENTER_RIGHT, GeneralLocation.CENTER_RIGHT, offsetY = 0, offsetX = offsetX)
    }

    fun clickBottomRight(offsetX: Int, offsetY: Int) = apply {
        customClick(EspressoActionType.CLICK_BOTTOM_RIGHT, GeneralLocation.BOTTOM_RIGHT, offsetY = offsetY, offsetX = offsetX)
    }

    fun clickBottomCenter(offsetY: Int) = apply {
        customClick(EspressoActionType.CLICK_BOTTOM_CENTER, GeneralLocation.BOTTOM_CENTER, offsetY = offsetY, offsetX = 0)
    }

    fun clickBottomLeft(offsetX: Int, offsetY: Int) = apply {
        customClick(EspressoActionType.CLICK_BOTTOM_LEFT, GeneralLocation.BOTTOM_LEFT, offsetY = offsetY, offsetX = offsetX)
    }

    fun clickCenterLeft(offsetX: Int) = apply {
        customClick(EspressoActionType.CLICK_CENTER_LEFT, GeneralLocation.CENTER_LEFT, offsetY = 0, offsetX = offsetX)
    }
    // ======================================================

    fun click() = apply {
        executeAction(
            operationBlock = getInteractionActionBlock(ViewActions.click()),
            name = "Click to '${getInteractionMatcher()}'",
            type = EspressoActionType.CLICK,
            description = "${interaction.simpleClassName()} action '${EspressoActionType.CLICK}' to '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms"
        )
    }

    fun doubleClick() = apply {
        executeAction(
            operationBlock = getInteractionActionBlock(ViewActions.doubleClick()),
            name = "DoubleClick to '${getInteractionMatcher()}'",
            type = EspressoActionType.CLICK,
            description = "${interaction.simpleClassName()} action '${EspressoActionType.DOUBLE_CLICK}' to '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms"
        )
    }

    fun longClick() = apply {
        executeAction(
            operationBlock = getInteractionActionBlock(ViewActions.longClick()),
            name = "LongClick to '${getInteractionMatcher()}'",
            type = EspressoActionType.LONG_CLICK,
            description = "${interaction.simpleClassName()} action '${EspressoActionType.LONG_CLICK}' to '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms"
        )
    }

    fun typeText(text: String) = apply {
        executeAction(
            operationBlock = getInteractionActionBlock(ViewActions.typeText(text)),
            name = "Type text '$text' to '${getInteractionMatcher()}'",
            type = EspressoActionType.TYPE_TEXT,
            description = "${interaction.simpleClassName()} action '${EspressoActionType.TYPE_TEXT}' '$text' to '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms"
        )
    }

    fun replaceText(text: String) = apply {
        executeAction(
            operationBlock = getInteractionActionBlock(ViewActions.replaceText(text)),
            name = "Replace text '$text' to '${getInteractionMatcher()}'",
            type = EspressoActionType.REPLACE_TEXT,
            description = "${interaction.simpleClassName()} action '${EspressoActionType.REPLACE_TEXT}' '$text' to '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms"
        )
    }

    fun clearText() = apply {
        executeAction(
            operationBlock = getInteractionActionBlock(ViewActions.clearText()),
            name = "Clear text in '${getInteractionMatcher()}'",
            type = EspressoActionType.CLEAR_TEXT,
            description = "${interaction.simpleClassName()} action '${EspressoActionType.CLEAR_TEXT}' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms"
        )
    }

    fun pressKey(keyCode: Int) = apply {
        executeAction(
            operationBlock = getInteractionActionBlock(ViewActions.pressKey(keyCode)),
            name = "PressKey code '$keyCode' on '${getInteractionMatcher()}'",
            type = EspressoActionType.PRESS_KEY,
            description = "${interaction.simpleClassName()} action '${EspressoActionType.PRESS_KEY}' '$keyCode' on '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms"
        )
    }

    fun pressKey(key: EspressoKey) = apply {
        executeAction(
            operationBlock = getInteractionActionBlock(ViewActions.pressKey(key)),
            name = "Press EspressoKey '$key' on '${getInteractionMatcher()}'",
            type = EspressoActionType.PRESS_KEY,
            description = "${interaction.simpleClassName()} action '${EspressoActionType.PRESS_KEY}' EspressoKey '$key' on '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms"
        )
    }

    fun closeSoftKeyboard() = apply {
        executeAction(
            operationBlock = getInteractionActionBlock(ViewActions.closeSoftKeyboard()),
            name = "CloseSoftKeyboard with '${getInteractionMatcher()}'",
            type = EspressoActionType.CLOSE_SOFT_KEYBOARD,
            description = "${interaction.simpleClassName()} action '${EspressoActionType.CLOSE_SOFT_KEYBOARD}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms"
        )
    }

    fun swipeLeft() = apply {
        executeAction(
            operationBlock = getInteractionActionBlock(ViewActions.swipeLeft()),
            name = "SwipeLeft with '${getInteractionMatcher()}'",
            type = EspressoActionType.SWIPE_LEFT,
            description = "${interaction.simpleClassName()} action '${EspressoActionType.SWIPE_LEFT}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms"
        )
    }

    fun swipeRight() = apply {
        executeAction(
            operationBlock = getInteractionActionBlock(ViewActions.swipeRight()),
            name = "SwipeRight with '${getInteractionMatcher()}'",
            type = EspressoActionType.SWIPE_RIGHT,
            description = "${interaction.simpleClassName()} action '${EspressoActionType.SWIPE_RIGHT}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms"
        )
    }

    fun swipeUp() = apply {
        executeAction(
            operationBlock = getInteractionActionBlock(ViewActions.swipeUp()),
            name = "SwipeUp with '${getInteractionMatcher()}'",
            type = EspressoActionType.SWIPE_UP,
            description = "${interaction.simpleClassName()} action '${EspressoActionType.SWIPE_UP}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms"
        )
    }

    fun swipeDown() = apply {
        executeAction(
            operationBlock = getInteractionActionBlock(ViewActions.swipeDown()),
            name = "SwipeDown with '${getInteractionMatcher()}'",
            type = EspressoActionType.SWIPE_DOWN,
            description = "${interaction.simpleClassName()} action '${EspressoActionType.SWIPE_DOWN}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms"
        )
    }

    fun scrollTo() = apply {
        executeAction(
            operationBlock = getInteractionActionBlock(ViewActions.scrollTo()),
            name = "ScrollTo with '${getInteractionMatcher()}'",
            type = EspressoActionType.SCROLL,
            description = "${interaction.simpleClassName()} action '${EspressoActionType.SCROLL}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms"
        )
    }


    fun perform(viewAction: ViewAction, description: String = "") = apply {
        executeAction(
            operationBlock = getInteractionActionBlock(viewAction),
            name = description.ifEmpty { "Perform action to ${getInteractionMatcher()}" },
            description = "${interaction.simpleClassName()} action '$description' of '${viewAction.description}' to '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms"
        )
    }

    fun perform(params: UltronEspressoActionParams? = null, block: (uiController: UiController, view: View) -> Unit) = apply {
        val actionParams = params ?: getDefaultActionParams()
        val viewAction = object : AnonymousViewAction(actionParams) {
            override fun perform(uiController: UiController, view: View) {
                block(uiController, view)
                uiController.loopMainThreadUntilIdle()
            }
        }
        executeAction(
            operationBlock = getInteractionActionBlock(viewAction),
            name = actionParams.operationName,
            description = actionParams.operationDescription,
            type = actionParams.operationType
        )
    }

    fun <T> execute(params: UltronEspressoActionParams? = null, block: (uiController: UiController, view: View) -> T): T {
        val actionParams = params ?: getDefaultActionParams()
        val container = AtomicReference<T>()
        val viewAction = object : AnonymousViewAction(actionParams) {
            override fun perform(uiController: UiController, view: View) {
                container.set(block(uiController, view))
                uiController.loopMainThreadUntilIdle()
            }
        }
        executeAction(
            operationBlock = getInteractionActionBlock(viewAction),
            name = actionParams.operationName,
            description = actionParams.operationDescription,
            type = actionParams.operationType
        )
        return container.get()
    }



    //assertion
    fun isDisplayed() = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(ViewMatchers.isDisplayed()),
            name = "IsDisplayed of '${getInteractionMatcher()}'",
            type = EspressoAssertionType.IS_DISPLAYED,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.IS_DISPLAYED}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    /**
     * Asserts ui element presents in view hierarchy but isn't displayed
     * If these is no element in view hierarchy it throws [NoMatchingViewException]. In this case use [doesNotExist]
     */
    fun isNotDisplayed() = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(Matchers.not(ViewMatchers.isDisplayed())),
            name = "IsNotDisplayed of '${getInteractionMatcher()}'",
            type = EspressoAssertionType.IS_NOT_DISPLAYED,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.IS_NOT_DISPLAYED}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    /**
     * Asserts ui element presents in view hierarchy
     */
    fun exists() = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(ExistsEspressoViewAssertion()),
            name = "Exists of '${getInteractionMatcher()}'",
            type = EspressoAssertionType.EXISTS,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.EXISTS}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    /**
     * Asserts ui element isn't presents in view hierarchy
     */
    fun doesNotExist() = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(ViewAssertions.doesNotExist()),
            name = "DoesNotExist of '${getInteractionMatcher()}'",
            type = EspressoAssertionType.DOES_NOT_EXIST,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.DOES_NOT_EXIST}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun isCompletelyDisplayed() = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(ViewMatchers.isCompletelyDisplayed()),
            name = "IsCompletelyDisplayed of '${getInteractionMatcher()}'",
            type = EspressoAssertionType.IS_COMPLETELY_DISPLAYED,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.IS_COMPLETELY_DISPLAYED}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun isDisplayingAtLeast(percentage: Int) = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(
                ViewMatchers.isDisplayingAtLeast(
                    percentage
                )
            ),
            name = "IsDisplayingAtLeast '$percentage'% of '${getInteractionMatcher()}'",
            type = EspressoAssertionType.IS_DISPLAYING_AT_LEAST,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.IS_DISPLAYING_AT_LEAST}' '$percentage'% of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun isEnabled() = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(ViewMatchers.isEnabled()),
            name = "IsEnabled of '${getInteractionMatcher()}'",
            type = EspressoAssertionType.IS_ENABLED,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.IS_ENABLED}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun isNotEnabled() = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(Matchers.not(ViewMatchers.isEnabled())),
            name = "IsNotEnabled of '${getInteractionMatcher()}'",
            type = EspressoAssertionType.IS_NOT_ENABLED,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.IS_NOT_ENABLED}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun isSelected() = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(ViewMatchers.isSelected()),
            name = "IsSelected of '${getInteractionMatcher()}'",
            type = EspressoAssertionType.IS_SELECTED,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.IS_SELECTED}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun isNotSelected() = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(Matchers.not(ViewMatchers.isSelected())),
            name = "IsNotSelected of '${getInteractionMatcher()}'",
            type = EspressoAssertionType.IS_NOT_SELECTED,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.IS_NOT_SELECTED}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun isClickable() = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(ViewMatchers.isClickable()),
            name = "IsClickable of '${getInteractionMatcher()}'",
            type = EspressoAssertionType.IS_CLICKABLE,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.IS_CLICKABLE}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun isNotClickable() = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(Matchers.not(ViewMatchers.isClickable())),
            name = "IsNotClickable of '${getInteractionMatcher()}'",
            type = EspressoAssertionType.IS_NOT_CLICKABLE,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.IS_NOT_CLICKABLE}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun isChecked() = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(ViewMatchers.isChecked()),
            name = "IsChecked of '${getInteractionMatcher()}'",
            type = EspressoAssertionType.IS_CHECKED,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.IS_CHECKED}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun isNotChecked() = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(ViewMatchers.isNotChecked()),
            name = "IsNotChecked of '${getInteractionMatcher()}'",
            type = EspressoAssertionType.IS_NOT_CHECKED,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.IS_NOT_CHECKED}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun isFocusable() = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(ViewMatchers.isFocusable()),
            name = "IsFocusable of '${getInteractionMatcher()}'",
            type = EspressoAssertionType.IS_FOCUSABLE,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.IS_FOCUSABLE}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun isNotFocusable() = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(Matchers.not(ViewMatchers.isFocusable())),
            name = "IsNotFocusable of '${getInteractionMatcher()}'",
            type = EspressoAssertionType.IS_NOT_FOCUSABLE,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.IS_NOT_FOCUSABLE}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun hasFocus() = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(ViewMatchers.hasFocus()),
            name = "HasFocus of '${getInteractionMatcher()}'",
            type = EspressoAssertionType.HAS_FOCUS,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.HAS_FOCUS}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun isJavascriptEnabled() = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(ViewMatchers.isJavascriptEnabled()),
            name = "IsJavascriptEnabled of '${getInteractionMatcher()}'",
            type = EspressoAssertionType.IS_JS_ENABLED,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.IS_JS_ENABLED}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun hasText(text: String) = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(ViewMatchers.withText(text)),
            name = "HasText '$text' in '${getInteractionMatcher()}'",
            type = EspressoAssertionType.HAS_TEXT,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.HAS_TEXT}' '$text' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun hasText(resourceId: Int) = apply {
        executeAction(
            operationBlock = getInteractionAssertionBlock(ViewMatchers.withText(resourceId)),
            name = "HasText with resourceId '$resourceId' in '${getInteractionMatcher()}'",
            type = EspressoAssertionType.HAS_TEXT,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.HAS_TEXT}' with resourceId '$resourceId' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun hasText(stringMatcher: Matcher<String>) = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(ViewMatchers.withText(stringMatcher)),
            name = "HasText with matcher '$stringMatcher' in '${getInteractionMatcher()}'",
            type = EspressoAssertionType.HAS_TEXT,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.HAS_TEXT}' with matcher '$stringMatcher' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun textContains(text: String) = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(
                ViewMatchers.withText(
                    Matchers.containsString(
                        text
                    )
                )
            ),
            name = "ContainsText '$text' in '${getInteractionMatcher()}'",
            type = EspressoAssertionType.CONTAINS_TEXT,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.CONTAINS_TEXT}' '$text' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun hasContentDescription(text: String) = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(
                ViewMatchers.withContentDescription(
                    text
                )
            ),
            name = "HasContentDescription '$text' in '${getInteractionMatcher()}'",
            type = EspressoAssertionType.HAS_CONTENT_DESCRIPTION,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.HAS_CONTENT_DESCRIPTION}' '$text' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun hasContentDescription(resourceId: Int) = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(
                ViewMatchers.withContentDescription(
                    resourceId
                )
            ),
            name = "HasContentDescription resourceId = '$resourceId' in '${getInteractionMatcher()}'",
            type = EspressoAssertionType.HAS_CONTENT_DESCRIPTION,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.HAS_CONTENT_DESCRIPTION}' resourceId = '$resourceId' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun hasContentDescription(charSequenceMatcher: Matcher<CharSequence>) = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(
                ViewMatchers.withContentDescription(
                    charSequenceMatcher
                )
            ),
            name = "HasContentDescription charSequenceMatcher = '$charSequenceMatcher' in '${getInteractionMatcher()}'",
            type = EspressoAssertionType.HAS_CONTENT_DESCRIPTION,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.HAS_CONTENT_DESCRIPTION}' charSequenceMatcher = '$charSequenceMatcher' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    fun contentDescriptionContains(text: String) = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(
                ViewMatchers.withContentDescription(
                    Matchers.containsString(text)
                )
            ),
            name = "ContentDescriptionContains text '$text' in '${getInteractionMatcher()}'",
            type = EspressoAssertionType.CONTENT_DESCRIPTION_CONTAINS_TEXT,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.CONTENT_DESCRIPTION_CONTAINS_TEXT}' text '$text' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    /**
     * Asserts that the provided condition matches for the interaction's view using a custom Espresso assertion.
     *
     * This method allows you to perform a custom Espresso assertion using a Matcher condition. The
     * provided condition is evaluated against the interaction's view to determine whether the assertion
     * passes. If the condition matches, the assertion is considered successful.
     *
     * @param condition The Matcher condition to evaluate against the interaction's view.
     * @return An updated instance of the class.
     */
    fun assertMatches(condition: Matcher<View>) = apply {
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(condition),
            name = "Custom assertion with '$condition' of '${getInteractionMatcher()}'",
            type = EspressoAssertionType.ASSERT_MATCHES,
            description = "${interaction.simpleClassName()} assertion '${EspressoAssertionType.ASSERT_MATCHES}' with '$condition' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }

    /**
     * Asserts that a custom Espresso assertion matches the given condition using the provided
     * parameters and assertion block.
     *
     * This method allows you to perform a custom Espresso assertion by providing the parameters
     * for the assertion and a block of code that defines the assertion's condition. The assertion
     * block receives a View as a parameter and is responsible for evaluating whether the condition
     * is met for the given view.
     *
     * The method returns an updated instance of the class.
     *
     * @param params The optional parameters for the Espresso assertion. If null, default assertion
     *               parameters are used.
     * @param block The block of code that defines the condition for the custom assertion. The block
     *              receives a View as a parameter and returns a boolean value indicating whether
     *              the condition is met.
     * @return An updated instance of the class.
     */
    fun assertMatches(params: UltronEspressoAssertionParams? = null, block: (view: View) -> Boolean) = apply {
        val assertionParams = params ?: getDefaultAssertionParams()
        val matcher = object : BaseMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText(assertionParams.descriptionToAppend)
            }

            override fun matches(actual: Any): Boolean = block(actual as View)
        }
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(matcher),
            name = assertionParams.operationName,
            description = assertionParams.operationDescription,
            type = assertionParams.operationType
        )
    }

    fun getInteractionActionBlock(viewAction: ViewAction): () -> Unit {
        return when (interaction) {
            is ViewInteraction -> {
                { interaction.perform(viewAction) }
            }

            is DataInteraction -> {
                { interaction.perform(viewAction) }
            }

            else -> throw UltronException("Unknown type of interaction provided!")
        }
    }

    fun getInteractionAssertionBlock(matcher: Matcher<View>): () -> Unit {
        return when (interaction) {
            is ViewInteraction -> {
                { interaction.check(matches(matcher)) }
            }

            is DataInteraction -> {
                { interaction.check(matches(matcher)) }
            }

            else -> throw UltronException("Unknown type of interaction provided!")
        }
    }

    fun getInteractionAssertionBlock(assertion: ViewAssertion): () -> Unit {
        return when (interaction) {
            is ViewInteraction -> {
                { interaction.check(assertion) }
            }

            is DataInteraction -> {
                { interaction.check(assertion) }
            }

            else -> throw UltronException("Unknown type of interaction provided!")
        }
    }

    fun getInteractionMatcher(): Matcher<View>? {
        return when (interaction) {
            is ViewInteraction -> {
                interaction.getViewMatcher()
            }

            is DataInteraction -> {
                interaction.getDataMatcher()
            }

            else -> throw UltronException("Unknown type of interaction provided!")
        }
    }

    fun getInteractionRootMatcher(): Matcher<Root>? {
        return when (interaction) {
            is ViewInteraction -> {
                interaction.getRootMatcher()
            }

            is DataInteraction -> {
                interaction.getRootMatcher()
            }

            else -> throw UltronException("Unknown type of interaction provided!")
        }
    }

    fun getUltronEspressoActionOperation(
        operationBlock: () -> Unit, name: String,
        type: UltronOperationType,
        description: String
    ) = UltronEspressoOperation(operationBlock, name, type, description, getActionTimeout(), assertion)

    fun getUltronEspressoAssertionOperation(
        operationBlock: () -> Unit, name: String,
        type: UltronOperationType,
        description: String
    ) = UltronEspressoOperation(operationBlock, name, type, description, getAssertionTimeout(), assertion)

    fun getActionResultHandler() = this.resultHandler ?: UltronConfig.Espresso.ViewActionConfig.resultHandler
    fun getAssertionResultHandler() = this.resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler

    fun executeAction(operation: UltronEspressoOperation) =
        UltronEspressoOperationLifecycle.execute(EspressoActionExecutor(operation), getActionResultHandler())

    fun executeAction(
        operationBlock: () -> Unit, name: String,
        type: UltronOperationType = CommonOperationType.DEFAULT,
        description: String
    ) = executeAction(getUltronEspressoActionOperation(operationBlock, name, type, description))

    fun executeAssertion(operation: UltronEspressoOperation) =
        UltronEspressoOperationLifecycle.execute(EspressoAssertionExecutor(operation), getAssertionResultHandler())

    fun executeAssertion(
        operationBlock: () -> Unit, name: String,
        type: UltronOperationType = CommonOperationType.DEFAULT,
        description: String
    ) = executeAssertion(getUltronEspressoAssertionOperation(operationBlock, name, type, description))

    private fun getDefaultActionParams() = UltronEspressoActionParams(
        operationName = "Anonymous action to '${getInteractionMatcher()}'",
        operationDescription = "Anonymous action ${interaction.simpleClassName()} on '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms"
    )

    private fun getDefaultAssertionParams() = UltronEspressoAssertionParams(
        operationName = "Anonymous assertion on '${getInteractionMatcher()}'",
        operationDescription = "Anonymous assertion on '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
    )
}