package com.atiurin.ultron.core.espresso

import android.view.View
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Root
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.EspressoKey
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import com.atiurin.ultron.core.common.OperationType
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.action.*
import com.atiurin.ultron.core.espresso.action.EspressoOperationExecutor
import com.atiurin.ultron.core.espresso.assertion.EspressoAssertionType
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.extensions.getDataMatcher
import com.atiurin.ultron.extensions.getRootMatcher
import com.atiurin.ultron.extensions.getViewMatcher
import org.hamcrest.Matcher
import org.hamcrest.Matchers

class UltronInteraction<T>(
    private val interaction: T,
    private val timeoutMs: Long? = null,
    private val resultHandler: ((EspressoOperationResult<EspressoOperation>) -> Unit)? = null
) {
    fun withResultHandler(resultHandler: (EspressoOperationResult<EspressoOperation>) -> Unit): UltronInteraction<T> {
        return UltronInteraction(this.interaction, this.timeoutMs, resultHandler)
    }

    fun withTimeout(timeoutMs: Long): UltronInteraction<T> {
        return UltronInteraction(this.interaction, timeoutMs, this.resultHandler)
    }

    fun click() = apply {
        executeAction(
            viewAction = ViewActions.click(),
            name = "Click to ${getInteractionMatcher()}",
            type = EspressoActionType.CLICK,
            description = "${interaction!!::class.java} action '${EspressoActionType.CLICK}' to ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT
        )
    }

    fun doubleClick() = apply {
        executeAction(
            viewAction = ViewActions.doubleClick(),
            name = "DoubleClick to ${getInteractionMatcher()}",
            type = EspressoActionType.CLICK,
            description = "${interaction!!::class.java} action '${EspressoActionType.DOUBLE_CLICK}' to ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT
        )
    }

    fun longClick() = apply {
        executeAction(
            viewAction = ViewActions.longClick(),
            name = "LongClick to ${getInteractionMatcher()}",
            type = EspressoActionType.LONG_CLICK,
            description = "${interaction!!::class.java} action '${EspressoActionType.LONG_CLICK}' to ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT
        )
    }

    fun typeText(text: String) = apply {
        executeAction(
            viewAction = ViewActions.typeText(text),
            name = "Type text '$text' to ${getInteractionMatcher()}",
            type = EspressoActionType.TYPE_TEXT,
            description = "${interaction!!::class.java} action '${EspressoActionType.TYPE_TEXT}' '$text' to ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT
        )
    }

    fun replaceText(text: String) = apply {
        executeAction(
            viewAction = ViewActions.replaceText(text),
            name = "Replace text '$text' to ${getInteractionMatcher()}",
            type = EspressoActionType.REPLACE_TEXT,
            description = "${interaction!!::class.java} action '${EspressoActionType.REPLACE_TEXT}' '$text' to ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT
        )
    }

    fun clearText() = apply {
        executeAction(
            viewAction = ViewActions.clearText(),
            name = "Clear text in ${getInteractionMatcher()}",
            type = EspressoActionType.CLEAR_TEXT,
            description = "${interaction!!::class.java} action '${EspressoActionType.CLEAR_TEXT}' in ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT
        )
    }

    fun pressKey(keyCode: Int) = apply {
        executeAction(
            viewAction = ViewActions.pressKey(keyCode),
            name = "PressKey code '$keyCode' with ${getInteractionMatcher()}",
            type = EspressoActionType.PRESS_KEY,
            description = "${interaction!!::class.java} action '${EspressoActionType.PRESS_KEY}' '$keyCode' with ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT
        )
    }

    fun pressKey(key: EspressoKey) = apply {
        executeAction(
            viewAction = ViewActions.pressKey(key),
            name = "Press EspressoKey '$key' with ${getInteractionMatcher()}",
            type = EspressoActionType.PRESS_KEY,
            description = "${interaction!!::class.java} action '${EspressoActionType.PRESS_KEY}' EspressoKey '$key' with ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT
        )
    }

    fun closeSoftKeyboard() = apply {
        executeAction(
            viewAction = ViewActions.closeSoftKeyboard(),
            name = "CloseSoftKeyboard with ${getInteractionMatcher()}",
            type = EspressoActionType.CLOSE_SOFT_KEYBOARD,
            description = "${interaction!!::class.java} action '${EspressoActionType.CLOSE_SOFT_KEYBOARD}' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT
        )
    }

    fun swipeLeft() = apply {
        executeAction(
            viewAction = ViewActions.swipeLeft(),
            name = "SwipeLeft with ${getInteractionMatcher()}",
            type = EspressoActionType.SWIPE_LEFT,
            description = "${interaction!!::class.java} action '${EspressoActionType.SWIPE_LEFT}' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT
        )
    }

    fun swipeRight() = apply {
        executeAction(
            viewAction = ViewActions.swipeRight(),
            name = "SwipeRight with ${getInteractionMatcher()}",
            type = EspressoActionType.SWIPE_RIGHT,
            description = "${interaction!!::class.java} action '${EspressoActionType.SWIPE_RIGHT}' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT
        )
    }

    fun swipeUp() = apply {
        executeAction(
            viewAction = ViewActions.swipeUp(),
            name = "SwipeUp with ${getInteractionMatcher()}",
            type = EspressoActionType.SWIPE_UP,
            description = "${interaction!!::class.java} action '${EspressoActionType.SWIPE_UP}' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT
        )
    }

    fun swipeDown() = apply {
        executeAction(
            viewAction = ViewActions.swipeDown(),
            name = "SwipeDown with ${getInteractionMatcher()}",
            type = EspressoActionType.SWIPE_DOWN,
            description = "${interaction!!::class.java} action '${EspressoActionType.SWIPE_DOWN}' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT
        )
    }

    fun scrollTo() = apply {
        executeAction(
            viewAction = ViewActions.scrollTo(),
            name = "ScrollTo with ${getInteractionMatcher()}",
            type = EspressoActionType.SCROLL,
            description = "${interaction!!::class.java} action '${EspressoActionType.SCROLL}' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT
        )
    }

    fun perform(viewAction: ViewAction) = apply {
        executeAction(
            viewAction = viewAction,
            name = "Custom action to ${getInteractionMatcher()}",
            type = EspressoActionType.CUSTOM,
            description = "${interaction!!::class.java} action '${EspressoActionType.CUSTOM}' of '${viewAction.description}' to ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT
        )
    }

    //assertion
    fun isDisplayed() = apply {
        executeAssertion(
            matcher = ViewMatchers.isDisplayed(),
            name = "IsDisplayed of ${getInteractionMatcher()}",
            type = EspressoAssertionType.IS_DISPLAYED,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.IS_DISPLAYED}' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }

    fun isNotDisplayed() = apply {
        executeAssertion(
            matcher = Matchers.not(ViewMatchers.isDisplayed()),
            name = "IsNotDisplayed of ${getInteractionMatcher()}",
            type = EspressoAssertionType.IS_NOT_DISPLAYED,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.IS_NOT_DISPLAYED}' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }

    fun isCompletelyDisplayed() = apply {
        executeAssertion(
            matcher = ViewMatchers.isCompletelyDisplayed(),
            name = "IsCompletelyDisplayed of ${getInteractionMatcher()}",
            type = EspressoAssertionType.IS_COMPLETELY_DISPLAYED,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.IS_COMPLETELY_DISPLAYED}' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }

    fun isDisplayingAtLeast(percentage: Int) = apply {
        executeAssertion(
            matcher = ViewMatchers.isDisplayingAtLeast(percentage),
            name = "IsDisplayingAtLeast '$percentage'% of ${getInteractionMatcher()}",
            type = EspressoAssertionType.IS_DISPLAYING_AT_LEAST,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.IS_DISPLAYING_AT_LEAST}' '$percentage'% of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }

    fun isEnabled() = apply {
        executeAssertion(
            matcher = ViewMatchers.isEnabled(),
            name = "IsEnabled of ${getInteractionMatcher()}",
            type = EspressoAssertionType.IS_ENABLED,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.IS_ENABLED}' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }

    fun isNotEnabled() = apply {
        executeAssertion(
            matcher = Matchers.not(ViewMatchers.isEnabled()),
            name = "IsNotEnabled of ${getInteractionMatcher()}",
            type = EspressoAssertionType.IS_NOT_ENABLED,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.IS_NOT_ENABLED}' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }

    fun isSelected() = apply {
        executeAssertion(
            matcher = ViewMatchers.isSelected(),
            name = "IsSelected of ${getInteractionMatcher()}",
            type = EspressoAssertionType.IS_SELECTED,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.IS_SELECTED}' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }

    fun isNotSelected() = apply {
        executeAssertion(
            matcher = Matchers.not(ViewMatchers.isSelected()),
            name = "IsNotSelected of ${getInteractionMatcher()}",
            type = EspressoAssertionType.IS_NOT_SELECTED,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.IS_NOT_SELECTED}' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }

    fun isClickable() = apply {
        executeAssertion(
            matcher = ViewMatchers.isClickable(),
            name = "IsClickable of ${getInteractionMatcher()}",
            type = EspressoAssertionType.IS_CLICKABLE,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.IS_CLICKABLE}' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }

    fun isNotClickable() = apply {
        executeAssertion(
            matcher = Matchers.not(ViewMatchers.isClickable()),
            name = "IsNotClickable of ${getInteractionMatcher()}",
            type = EspressoAssertionType.IS_NOT_CLICKABLE,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.IS_NOT_CLICKABLE}' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }

    fun isChecked() = apply {
        executeAssertion(
            matcher = ViewMatchers.isChecked(),
            name = "IsChecked of ${getInteractionMatcher()}",
            type = EspressoAssertionType.IS_CHECKED,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.IS_CHECKED}' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }

    fun isNotChecked() = apply {
        executeAssertion(
            matcher = ViewMatchers.isNotChecked(),
            name = "IsNotChecked of ${getInteractionMatcher()}",
            type = EspressoAssertionType.IS_NOT_CHECKED,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.IS_NOT_CHECKED}' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }

    fun isFocusable() = apply {
        executeAssertion(
            matcher = ViewMatchers.isFocusable(),
            name = "IsFocusable of ${getInteractionMatcher()}",
            type = EspressoAssertionType.IS_FOCUSABLE,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.IS_FOCUSABLE}' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }

    fun isNotFocusable() = apply {
        executeAssertion(
            matcher = Matchers.not(ViewMatchers.isFocusable()),
            name = "IsNotFocusable of ${getInteractionMatcher()}",
            type = EspressoAssertionType.IS_NOT_FOCUSABLE,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.IS_NOT_FOCUSABLE}' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }

    fun hasFocus() = apply {
        executeAssertion(
            matcher = ViewMatchers.hasFocus(),
            name = "HasFocus of ${getInteractionMatcher()}",
            type = EspressoAssertionType.HAS_FOCUS,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.HAS_FOCUS}' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }

    fun isJavascriptEnabled() = apply {
        executeAssertion(
            matcher = ViewMatchers.isJavascriptEnabled(),
            name = "IsJavascriptEnabled of ${getInteractionMatcher()}",
            type = EspressoAssertionType.IS_JS_ENABLED,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.IS_JS_ENABLED}' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }

    fun hasText(text: String) = apply {
        executeAssertion(
            matcher = ViewMatchers.withText(text),
            name = "HasText '$text' in ${getInteractionMatcher()}",
            type = EspressoAssertionType.HAS_TEXT,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.HAS_TEXT}' '$text' in ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }

    fun hasText(resourceId: Int) = apply {
        executeAssertion(
            matcher = ViewMatchers.withText(resourceId),
            name = "HasText with resourceId '$resourceId' in ${getInteractionMatcher()}",
            type = EspressoAssertionType.HAS_TEXT,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.HAS_TEXT}' with resourceId '$resourceId' in ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }

    fun hasText(stringMatcher: Matcher<String>) = apply {
        executeAssertion(
            matcher = ViewMatchers.withText(stringMatcher),
            name = "HasText with matcher '$stringMatcher' in ${getInteractionMatcher()}",
            type = EspressoAssertionType.HAS_TEXT,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.HAS_TEXT}' with matcher '$stringMatcher' in ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }

    fun textContains(text: String) = apply {
        executeAssertion(
            matcher = ViewMatchers.withText(Matchers.containsString(text)),
            name = "ContainsText '$text' in ${getInteractionMatcher()}",
            type = EspressoAssertionType.CONTAINS_TEXT,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.CONTAINS_TEXT}' '$text' in ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }

    fun hasContentDescription(text: String) = apply {
        executeAssertion(
            matcher = ViewMatchers.withContentDescription(text),
            name = "HasContentDescription '$text' in ${getInteractionMatcher()}",
            type = EspressoAssertionType.HAS_CONTENT_DESCRIPTION,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.HAS_CONTENT_DESCRIPTION}' '$text' in ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }
    fun hasContentDescription(resourceId: Int) = apply {
        executeAssertion(
            matcher = ViewMatchers.withContentDescription(resourceId),
            name = "HasContentDescription resourceId = '$resourceId' in ${getInteractionMatcher()}",
            type = EspressoAssertionType.HAS_CONTENT_DESCRIPTION,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.HAS_CONTENT_DESCRIPTION}' resourceId = '$resourceId' in ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }
    fun hasContentDescription(charSequenceMatcher: Matcher<CharSequence>) = apply {
        executeAssertion(
            matcher = ViewMatchers.withContentDescription(charSequenceMatcher),
            name = "HasContentDescription charSequenceMatcher = '$charSequenceMatcher' in ${getInteractionMatcher()}",
            type = EspressoAssertionType.HAS_CONTENT_DESCRIPTION,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.HAS_CONTENT_DESCRIPTION}' charSequenceMatcher = '$charSequenceMatcher' in ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }
    fun contentDescriptionContains(text: String) = apply {
        executeAssertion(
            matcher = ViewMatchers.withContentDescription(Matchers.containsString(text)),
            name = "ContentDescriptionContains text '$text' in ${getInteractionMatcher()}",
            type = EspressoAssertionType.CONTENT_DESCRIPTION_CONTAINS_TEXT,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.CONTENT_DESCRIPTION_CONTAINS_TEXT}' text '$text' in ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }
    fun assertMatches(condition: Matcher<View>) = apply {
        executeAssertion(
            matcher = condition,
            name = "Custom assertion with '$condition' of ${getInteractionMatcher()}",
            type = EspressoAssertionType.ASSERT_MATCHES,
            description = "${interaction!!::class.java} assertion '${EspressoAssertionType.ASSERT_MATCHES}' with '$condition' of ${getInteractionMatcher()} with root ${getInteractionRootMatcher()} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        )
    }


    private fun executeAction(
        viewAction: ViewAction,
        name: String,
        type: OperationType,
        description: String,
        timeoutMs: Long
    ) {
        var block = {}
        block = when (interaction) {
            is ViewInteraction -> {
                { interaction.perform(viewAction) }
            }
            is DataInteraction -> {
                { interaction.perform(viewAction) }
            }
            else -> throw UltronException("Unknown type of interaction provided!")
        }
        EspressoOperationLifecycle.execute(
            EspressoOperationExecutor(
                EspressoOperation(
                    operationBlock = block,
                    name = name,
                    type = type,
                    description = description,
                    timeoutMs = timeoutMs
        )
            ),
            this.resultHandler ?: UltronConfig.Espresso.ViewActionConfig.resultHandler
        )
    }

    private fun executeAssertion(
        matcher: Matcher<View>,
        name: String,
        type: OperationType,
        description: String,
        timeoutMs: Long
    ) {
        var block = {}
        block = when (interaction) {
            is ViewInteraction -> {
                { interaction.check(matches(matcher)) }
            }
            is DataInteraction -> {
                { interaction.check(matches(matcher)) }
            }
            else -> throw UltronException("Unknown type of interaction provided!")
        }
        EspressoOperationLifecycle.execute(
            EspressoOperationExecutor(
                EspressoOperation(
                    operationBlock = block,
                    name = name,
                    type = type,
                    description = description,
                    timeoutMs = timeoutMs
        )
            ),
            this.resultHandler ?: UltronConfig.Espresso.ViewActionConfig.resultHandler
        )
    }

    private fun getInteractionMatcher(): Matcher<View>? {
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

    private fun getInteractionRootMatcher(): Matcher<Root>? {
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


