package com.atiurin.ultron.core.espresso

import android.view.View
import androidx.test.espresso.*
import androidx.test.espresso.action.EspressoKey
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.UltronEspresso.executeAction
import com.atiurin.ultron.core.espresso.UltronEspresso.executeAssertion
import com.atiurin.ultron.core.espresso.action.*
import com.atiurin.ultron.core.espresso.assertion.EspressoAssertionType
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.extensions.getDataMatcher
import com.atiurin.ultron.extensions.getRootMatcher
import com.atiurin.ultron.extensions.getViewMatcher
import org.hamcrest.Matcher
import org.hamcrest.Matchers

class UltronEspressoInteraction<T>(
    private val interaction: T,
    private val timeoutMs: Long? = null,
    private val resultHandler: ((EspressoOperationResult<UltronEspressoOperation>) -> Unit)? = null
) {
    init {
        if (interaction !is ViewInteraction && interaction !is DataInteraction) throw UltronException(
            "Invalid interaction class provided ${interaction!!::class.java.simpleName}. Use ViewInteraction or DataInteraction"
        )
    }

    fun withResultHandler(resultHandler: (EspressoOperationResult<UltronEspressoOperation>) -> Unit): UltronEspressoInteraction<T> {
        return UltronEspressoInteraction(this.interaction, this.timeoutMs, resultHandler)
    }

    fun withTimeout(timeoutMs: Long): UltronEspressoInteraction<T> {
        return UltronEspressoInteraction(this.interaction, timeoutMs, this.resultHandler)
    }

    fun click() = apply {
        executeAction(
            UltronEspressoOperation(
                operationBlock = getInteractionActionBlock(ViewActions.click()),
                name = "Click to '${getInteractionMatcher()}'",
                type = EspressoActionType.CLICK,
                description = "${interaction!!::class.java.simpleName} action '${EspressoActionType.CLICK}' to '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewActionConfig.resultHandler
        )
    }

    fun doubleClick() = apply {
        executeAction(
            UltronEspressoOperation(
                operationBlock = getInteractionActionBlock(ViewActions.doubleClick()),
                name = "DoubleClick to '${getInteractionMatcher()}'",
                type = EspressoActionType.CLICK,
                description = "${interaction!!::class.java.simpleName} action '${EspressoActionType.DOUBLE_CLICK}' to '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewActionConfig.resultHandler
        )
    }

    fun longClick() = apply {
        executeAction(
            UltronEspressoOperation(
                operationBlock = getInteractionActionBlock(ViewActions.longClick()),
                name = "LongClick to '${getInteractionMatcher()}'",
                type = EspressoActionType.LONG_CLICK,
                description = "${interaction!!::class.java.simpleName} action '${EspressoActionType.LONG_CLICK}' to '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewActionConfig.resultHandler
        )
    }

    fun typeText(text: String) = apply {
        executeAction(
            UltronEspressoOperation(
                operationBlock = getInteractionActionBlock(ViewActions.typeText(text)),
                name = "Type text '$text' to '${getInteractionMatcher()}'",
                type = EspressoActionType.TYPE_TEXT,
                description = "${interaction!!::class.java.simpleName} action '${EspressoActionType.TYPE_TEXT}' '$text' to '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewActionConfig.resultHandler
        )
    }

    fun replaceText(text: String) = apply {
        executeAction(
            UltronEspressoOperation(
                operationBlock = getInteractionActionBlock(ViewActions.replaceText(text)),
                name = "Replace text '$text' to '${getInteractionMatcher()}'",
                type = EspressoActionType.REPLACE_TEXT,
                description = "${interaction!!::class.java.simpleName} action '${EspressoActionType.REPLACE_TEXT}' '$text' to '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewActionConfig.resultHandler
        )
    }

    fun clearText() = apply {
        executeAction(
            UltronEspressoOperation(
                operationBlock = getInteractionActionBlock(ViewActions.clearText()),
                name = "Clear text in '${getInteractionMatcher()}'",
                type = EspressoActionType.CLEAR_TEXT,
                description = "${interaction!!::class.java.simpleName} action '${EspressoActionType.CLEAR_TEXT}' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewActionConfig.resultHandler
        )
    }

    fun pressKey(keyCode: Int) = apply {
        executeAction(
            UltronEspressoOperation(
                operationBlock = getInteractionActionBlock(ViewActions.pressKey(keyCode)),
                name = "PressKey code '$keyCode' on '${getInteractionMatcher()}'",
                type = EspressoActionType.PRESS_KEY,
                description = "${interaction!!::class.java.simpleName} action '${EspressoActionType.PRESS_KEY}' '$keyCode' on '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewActionConfig.resultHandler
        )
    }

    fun pressKey(key: EspressoKey) = apply {
        executeAction(
            UltronEspressoOperation(
                operationBlock = getInteractionActionBlock(ViewActions.pressKey(key)),
                name = "Press EspressoKey '$key' on '${getInteractionMatcher()}'",
                type = EspressoActionType.PRESS_KEY,
                description = "${interaction!!::class.java.simpleName} action '${EspressoActionType.PRESS_KEY}' EspressoKey '$key' on '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewActionConfig.resultHandler
        )
    }

    fun closeSoftKeyboard() = apply {
        executeAction(
            UltronEspressoOperation(
                operationBlock = getInteractionActionBlock(ViewActions.closeSoftKeyboard()),
                name = "CloseSoftKeyboard with '${getInteractionMatcher()}'",
                type = EspressoActionType.CLOSE_SOFT_KEYBOARD,
                description = "${interaction!!::class.java.simpleName} action '${EspressoActionType.CLOSE_SOFT_KEYBOARD}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewActionConfig.resultHandler
        )
    }

    fun swipeLeft() = apply {
        executeAction(
            UltronEspressoOperation(
                operationBlock = getInteractionActionBlock(ViewActions.swipeLeft()),
                name = "SwipeLeft with '${getInteractionMatcher()}'",
                type = EspressoActionType.SWIPE_LEFT,
                description = "${interaction!!::class.java.simpleName} action '${EspressoActionType.SWIPE_LEFT}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewActionConfig.resultHandler
        )
    }

    fun swipeRight() = apply {
        executeAction(
            UltronEspressoOperation(
                operationBlock = getInteractionActionBlock(ViewActions.swipeRight()),
                name = "SwipeRight with '${getInteractionMatcher()}'",
                type = EspressoActionType.SWIPE_RIGHT,
                description = "${interaction!!::class.java.simpleName} action '${EspressoActionType.SWIPE_RIGHT}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewActionConfig.resultHandler
        )
    }

    fun swipeUp() = apply {
        executeAction(
            UltronEspressoOperation(
                operationBlock = getInteractionActionBlock(ViewActions.swipeUp()),
                name = "SwipeUp with '${getInteractionMatcher()}'",
                type = EspressoActionType.SWIPE_UP,
                description = "${interaction!!::class.java.simpleName} action '${EspressoActionType.SWIPE_UP}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewActionConfig.resultHandler
        )
    }

    fun swipeDown() = apply {
        executeAction(
            UltronEspressoOperation(
                operationBlock = getInteractionActionBlock(ViewActions.swipeDown()),
                name = "SwipeDown with '${getInteractionMatcher()}'",
                type = EspressoActionType.SWIPE_DOWN,
                description = "${interaction!!::class.java.simpleName} action '${EspressoActionType.SWIPE_DOWN}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewActionConfig.resultHandler
        )
    }

    fun scrollTo() = apply {
        executeAction(
            UltronEspressoOperation(
                operationBlock = getInteractionActionBlock(ViewActions.scrollTo()),
                name = "ScrollTo with '${getInteractionMatcher()}'",
                type = EspressoActionType.SCROLL,
                description = "${interaction!!::class.java.simpleName} action '${EspressoActionType.SCROLL}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewActionConfig.resultHandler
        )
    }

    fun perform(viewAction: ViewAction, description: String = "") = apply {
        executeAction(
            UltronEspressoOperation(
                operationBlock = getInteractionActionBlock(viewAction),
                name = "Custom action '$description' to ${getInteractionMatcher()}",
                type = EspressoActionType.CUSTOM,
                description = "${interaction!!::class.java.simpleName} action '${EspressoActionType.CUSTOM}' of '${viewAction.description}' to '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewActionConfig.resultHandler
        )
    }

    //assertion
    fun isDisplayed() = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(ViewMatchers.isDisplayed()),
                name = "IsDisplayed of '${getInteractionMatcher()}'",
                type = EspressoAssertionType.IS_DISPLAYED,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.IS_DISPLAYED}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    /**
     * Asserts ui element presents in view hierarchy but isn't displayed
     * If these is no element in view hierarchy it throws [NoMatchingViewException]. In this case use [doesNotExist]
     */
    fun isNotDisplayed() = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(Matchers.not(ViewMatchers.isDisplayed())),
                name = "IsNotDisplayed of '${getInteractionMatcher()}'",
                type = EspressoAssertionType.IS_NOT_DISPLAYED,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.IS_NOT_DISPLAYED}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    /**
     * Asserts ui element isn't presents in view hierarchy
     */
    fun doesNotExist() = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(ViewAssertions.doesNotExist()),
                name = "DoesNotExist of '${getInteractionMatcher()}'",
                type = EspressoAssertionType.DOES_NOT_EXIST,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.DOES_NOT_EXIST}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun isCompletelyDisplayed() = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(ViewMatchers.isCompletelyDisplayed()),
                name = "IsCompletelyDisplayed of '${getInteractionMatcher()}'",
                type = EspressoAssertionType.IS_COMPLETELY_DISPLAYED,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.IS_COMPLETELY_DISPLAYED}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun isDisplayingAtLeast(percentage: Int) = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(
                    ViewMatchers.isDisplayingAtLeast(
                        percentage
                    )
                ),
                name = "IsDisplayingAtLeast '$percentage'% of '${getInteractionMatcher()}'",
                type = EspressoAssertionType.IS_DISPLAYING_AT_LEAST,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.IS_DISPLAYING_AT_LEAST}' '$percentage'% of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun isEnabled() = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(ViewMatchers.isEnabled()),
                name = "IsEnabled of '${getInteractionMatcher()}'",
                type = EspressoAssertionType.IS_ENABLED,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.IS_ENABLED}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun isNotEnabled() = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(Matchers.not(ViewMatchers.isEnabled())),
                name = "IsNotEnabled of '${getInteractionMatcher()}'",
                type = EspressoAssertionType.IS_NOT_ENABLED,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.IS_NOT_ENABLED}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun isSelected() = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(ViewMatchers.isSelected()),
                name = "IsSelected of '${getInteractionMatcher()}'",
                type = EspressoAssertionType.IS_SELECTED,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.IS_SELECTED}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun isNotSelected() = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(Matchers.not(ViewMatchers.isSelected())),
                name = "IsNotSelected of '${getInteractionMatcher()}'",
                type = EspressoAssertionType.IS_NOT_SELECTED,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.IS_NOT_SELECTED}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun isClickable() = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(ViewMatchers.isClickable()),
                name = "IsClickable of '${getInteractionMatcher()}'",
                type = EspressoAssertionType.IS_CLICKABLE,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.IS_CLICKABLE}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun isNotClickable() = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(Matchers.not(ViewMatchers.isClickable())),
                name = "IsNotClickable of '${getInteractionMatcher()}'",
                type = EspressoAssertionType.IS_NOT_CLICKABLE,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.IS_NOT_CLICKABLE}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun isChecked() = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(ViewMatchers.isChecked()),
                name = "IsChecked of '${getInteractionMatcher()}'",
                type = EspressoAssertionType.IS_CHECKED,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.IS_CHECKED}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun isNotChecked() = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(ViewMatchers.isNotChecked()),
                name = "IsNotChecked of '${getInteractionMatcher()}'",
                type = EspressoAssertionType.IS_NOT_CHECKED,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.IS_NOT_CHECKED}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun isFocusable() = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(ViewMatchers.isFocusable()),
                name = "IsFocusable of '${getInteractionMatcher()}'",
                type = EspressoAssertionType.IS_FOCUSABLE,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.IS_FOCUSABLE}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun isNotFocusable() = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(Matchers.not(ViewMatchers.isFocusable())),
                name = "IsNotFocusable of '${getInteractionMatcher()}'",
                type = EspressoAssertionType.IS_NOT_FOCUSABLE,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.IS_NOT_FOCUSABLE}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun hasFocus() = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(ViewMatchers.hasFocus()),
                name = "HasFocus of '${getInteractionMatcher()}'",
                type = EspressoAssertionType.HAS_FOCUS,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.HAS_FOCUS}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun isJavascriptEnabled() = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(ViewMatchers.isJavascriptEnabled()),
                name = "IsJavascriptEnabled of '${getInteractionMatcher()}'",
                type = EspressoAssertionType.IS_JS_ENABLED,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.IS_JS_ENABLED}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun hasText(text: String) = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(ViewMatchers.withText(text)),
                name = "HasText '$text' in '${getInteractionMatcher()}'",
                type = EspressoAssertionType.HAS_TEXT,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.HAS_TEXT}' '$text' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun hasText(resourceId: Int) = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(ViewMatchers.withText(resourceId)),
                name = "HasText with resourceId '$resourceId' in '${getInteractionMatcher()}'",
                type = EspressoAssertionType.HAS_TEXT,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.HAS_TEXT}' with resourceId '$resourceId' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun hasText(stringMatcher: Matcher<String>) = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(ViewMatchers.withText(stringMatcher)),
                name = "HasText with matcher '$stringMatcher' in '${getInteractionMatcher()}'",
                type = EspressoAssertionType.HAS_TEXT,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.HAS_TEXT}' with matcher '$stringMatcher' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun textContains(text: String) = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(
                    ViewMatchers.withText(
                        Matchers.containsString(
                            text
                        )
                    )
                ),
                name = "ContainsText '$text' in '${getInteractionMatcher()}'",
                type = EspressoAssertionType.CONTAINS_TEXT,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.CONTAINS_TEXT}' '$text' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun hasContentDescription(text: String) = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(
                    ViewMatchers.withContentDescription(
                        text
                    )
                ),
                name = "HasContentDescription '$text' in '${getInteractionMatcher()}'",
                type = EspressoAssertionType.HAS_CONTENT_DESCRIPTION,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.HAS_CONTENT_DESCRIPTION}' '$text' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun hasContentDescription(resourceId: Int) = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(
                    ViewMatchers.withContentDescription(
                        resourceId
                    )
                ),
                name = "HasContentDescription resourceId = '$resourceId' in '${getInteractionMatcher()}'",
                type = EspressoAssertionType.HAS_CONTENT_DESCRIPTION,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.HAS_CONTENT_DESCRIPTION}' resourceId = '$resourceId' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun hasContentDescription(charSequenceMatcher: Matcher<CharSequence>) = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(
                    ViewMatchers.withContentDescription(
                        charSequenceMatcher
                    )
                ),
                name = "HasContentDescription charSequenceMatcher = '$charSequenceMatcher' in '${getInteractionMatcher()}'",
                type = EspressoAssertionType.HAS_CONTENT_DESCRIPTION,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.HAS_CONTENT_DESCRIPTION}' charSequenceMatcher = '$charSequenceMatcher' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun contentDescriptionContains(text: String) = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(
                    ViewMatchers.withContentDescription(
                        Matchers.containsString(text)
                    )
                ),
                name = "ContentDescriptionContains text '$text' in '${getInteractionMatcher()}'",
                type = EspressoAssertionType.CONTENT_DESCRIPTION_CONTAINS_TEXT,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.CONTENT_DESCRIPTION_CONTAINS_TEXT}' text '$text' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
        )
    }

    fun assertMatches(condition: Matcher<View>) = apply {
        executeAssertion(
            UltronEspressoOperation(
                operationBlock = getInteractionAssertionBlock(condition),
                name = "Custom assertion with '$condition' of '${getInteractionMatcher()}'",
                type = EspressoAssertionType.ASSERT_MATCHES,
                description = "${interaction!!::class.java.simpleName} assertion '${EspressoAssertionType.ASSERT_MATCHES}' with '$condition' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeoutMs ms",
                timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
            ),
            resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
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
}


