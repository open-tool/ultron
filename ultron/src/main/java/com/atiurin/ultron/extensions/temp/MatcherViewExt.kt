package com.atiurin.ultron.extensions

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.EspressoKey
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.config.UltronConfig.Espresso.ViewActionConfig
import com.atiurin.ultron.core.config.UltronConfig.Espresso.ViewAssertionConfig
import com.atiurin.ultron.core.config.UltronConfig.Espresso.Companion.ACTION_TIMEOUT
import com.atiurin.ultron.core.config.UltronConfig.Espresso.Companion.ASSERTION_TIMEOUT
import com.atiurin.ultron.core.espresso.UltronInteraction
import com.atiurin.ultron.core.espresso.action.EspressoOperation
import com.atiurin.ultron.core.espresso.action.ViewInteractionEspressoAction
import com.atiurin.ultron.core.espresso.assertion.ViewInteractionEspressoAssertion
import org.hamcrest.Matcher

fun Matcher<View>.isSuccess(
    action: Matcher<View>.() -> Unit
): Boolean {
    return onView(this).isSuccess { action() }
}

fun Matcher<View>.withTimeout(timeoutMs: Long) = UltronInteraction(onView(this)).withTimeout(timeoutMs)
fun Matcher<View>.withResultHandler(resultHandler: (EspressoOperationResult<EspressoOperation>) -> Unit) = UltronInteraction(onView(this)).withResultHandler(resultHandler)
//actions
fun Matcher<View>.click() = UltronInteraction(onView(this)).click()
fun Matcher<View>.doubleClick() = UltronInteraction(onView(this)).doubleClick()
fun Matcher<View>.longClick() = UltronInteraction(onView(this)).longClick()
fun Matcher<View>.typeText(text: String) = UltronInteraction(onView(this)).typeText(text)
fun Matcher<View>.replaceText(text: String) = UltronInteraction(onView(this)).replaceText(text)
fun Matcher<View>.clearText() = UltronInteraction(onView(this)).clearText()
fun Matcher<View>.pressKey(keyCode: Int) = UltronInteraction(onView(this)).pressKey(keyCode)
fun Matcher<View>.pressKey(key: EspressoKey) = UltronInteraction(onView(this)).pressKey(key)
fun Matcher<View>.closeSoftKeyboard() = UltronInteraction(onView(this)).closeSoftKeyboard()
fun Matcher<View>.swipeLeft() = UltronInteraction(onView(this)).swipeLeft()
fun Matcher<View>.swipeRight() = UltronInteraction(onView(this)).swipeRight()
fun Matcher<View>.swipeUp() = UltronInteraction(onView(this)).swipeUp()
fun Matcher<View>.swipeDown() = UltronInteraction(onView(this)).swipeDown()
fun Matcher<View>.scrollTo() = UltronInteraction(onView(this)).scrollTo()
fun Matcher<View>.perform(viewAction: ViewAction) = UltronInteraction(onView(this)).perform(viewAction)

//assertions
fun Matcher<View>.isDisplayed() = UltronInteraction(onView(this)).isDisplayed()
fun Matcher<View>.isNotDisplayed() = UltronInteraction(onView(this)).isNotDisplayed()
fun Matcher<View>.isCompletelyDisplayed() = UltronInteraction(onView(this)).isCompletelyDisplayed()
fun Matcher<View>.isDisplayingAtLeast(percentage: Int) =
    UltronInteraction(onView(this)).isDisplayingAtLeast(percentage)
fun Matcher<View>.isEnabled() = UltronInteraction(onView(this)).isEnabled()
fun Matcher<View>.isNotEnabled() = UltronInteraction(onView(this)).isNotEnabled()
fun Matcher<View>.isSelected() = UltronInteraction(onView(this)).isSelected()
fun Matcher<View>.isNotSelected() = UltronInteraction(onView(this)).isNotSelected()
fun Matcher<View>.isClickable() = UltronInteraction(onView(this)).isClickable()
fun Matcher<View>.isNotClickable() = UltronInteraction(onView(this)).isNotClickable()
fun Matcher<View>.isChecked() = UltronInteraction(onView(this)).isChecked()
fun Matcher<View>.isNotChecked() = UltronInteraction(onView(this)).isNotChecked()
fun Matcher<View>.isFocusable() = UltronInteraction(onView(this)).isFocusable()
fun Matcher<View>.isNotFocusable() = UltronInteraction(onView(this)).isNotFocusable()
fun Matcher<View>.hasFocus() = UltronInteraction(onView(this)).hasFocus()
fun Matcher<View>.isJavascriptEnabled() = UltronInteraction(onView(this)).isJavascriptEnabled()
fun Matcher<View>.hasText(text: String) = UltronInteraction(onView(this)).hasText(text)
fun Matcher<View>.hasText(resourceId: Int) = UltronInteraction(onView(this)).hasText(resourceId)
fun Matcher<View>.hasText(stringMatcher: Matcher<String>) =
    UltronInteraction(onView(this)).hasText(stringMatcher)
fun Matcher<View>.textContains(text: String) = UltronInteraction(onView(this)).textContains(text)
fun Matcher<View>.hasContentDescription(text: String) =
    UltronInteraction(onView(this)).hasContentDescription(text)
fun Matcher<View>.hasContentDescription(resourceId: Int) =
    UltronInteraction(onView(this)).hasContentDescription(resourceId)
fun Matcher<View>.hasContentDescription(charSequenceMatcher: Matcher<CharSequence>) =
    UltronInteraction(onView(this)).hasContentDescription(charSequenceMatcher)
fun Matcher<View>.contentDescriptionContains(text: String) =
    UltronInteraction(onView(this)).contentDescriptionContains(text)
fun Matcher<View>.assertMatches(condition: Matcher<View>) =
    UltronInteraction(onView(this)).assertMatches(condition)