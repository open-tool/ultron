package com.atiurin.ultron.extensions

import android.view.View
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.EspressoKey
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import com.atiurin.ultron.core.espresso.UltronInteraction
import org.hamcrest.Matcher

fun ViewInteraction.isSuccess(
    action: ViewInteraction.() -> Unit
): Boolean {
    var success = true
    try {
        action()
    } catch (th: Throwable) {
        success = false
    }
    return success
}
fun ViewInteraction.withTimeout(timeoutMs: Long) = UltronInteraction(this).withTimeout(timeoutMs)
fun ViewInteraction.withResultHandler(resultHandler: (EspressoOperationResult<UltronEspressoOperation>) -> Unit) = UltronInteraction(this).withResultHandler(resultHandler)

//actions
fun ViewInteraction.click() = UltronInteraction(this).click()
fun ViewInteraction.doubleClick() = UltronInteraction(this).doubleClick()
fun ViewInteraction.longClick() = UltronInteraction(this).longClick()
fun ViewInteraction.typeText(text: String) = UltronInteraction(this).typeText(text)
fun ViewInteraction.replaceText(text: String) = UltronInteraction(this).replaceText(text)
fun ViewInteraction.clearText() = UltronInteraction(this).clearText()
fun ViewInteraction.pressKey(keyCode: Int) = UltronInteraction(this).pressKey(keyCode)
fun ViewInteraction.pressKey(key: EspressoKey) = UltronInteraction(this).pressKey(key)
fun ViewInteraction.closeSoftKeyboard() = UltronInteraction(this).closeSoftKeyboard()
fun ViewInteraction.swipeLeft() = UltronInteraction(this).swipeLeft()
fun ViewInteraction.swipeRight() = UltronInteraction(this).swipeRight()
fun ViewInteraction.swipeUp() = UltronInteraction(this).swipeUp()
fun ViewInteraction.swipeDown() = UltronInteraction(this).swipeDown()
fun ViewInteraction.scrollTo() = UltronInteraction(this).scrollTo()
fun ViewInteraction.perform(viewAction: ViewAction) = UltronInteraction(this).perform(viewAction)

//assertions
fun ViewInteraction.isDisplayed() = UltronInteraction(this).isDisplayed()
fun ViewInteraction.doesNotExist() = UltronInteraction(this).doesNotExist()
fun ViewInteraction.isNotDisplayed() = UltronInteraction(this).isNotDisplayed()
fun ViewInteraction.isCompletelyDisplayed() = UltronInteraction(this).isCompletelyDisplayed()
fun ViewInteraction.isDisplayingAtLeast(percentage: Int) =
    UltronInteraction(this).isDisplayingAtLeast(percentage)
fun ViewInteraction.isEnabled() = UltronInteraction(this).isEnabled()
fun ViewInteraction.isNotEnabled() = UltronInteraction(this).isNotEnabled()
fun ViewInteraction.isSelected() = UltronInteraction(this).isSelected()
fun ViewInteraction.isNotSelected() = UltronInteraction(this).isNotSelected()
fun ViewInteraction.isClickable() = UltronInteraction(this).isClickable()
fun ViewInteraction.isNotClickable() = UltronInteraction(this).isNotClickable()
fun ViewInteraction.isChecked() = UltronInteraction(this).isChecked()
fun ViewInteraction.isNotChecked() = UltronInteraction(this).isNotChecked()
fun ViewInteraction.isFocusable() = UltronInteraction(this).isFocusable()
fun ViewInteraction.isNotFocusable() = UltronInteraction(this).isNotFocusable()
fun ViewInteraction.hasFocus() = UltronInteraction(this).hasFocus()
fun ViewInteraction.isJavascriptEnabled() = UltronInteraction(this).isJavascriptEnabled()
fun ViewInteraction.hasText(text: String) = UltronInteraction(this).hasText(text)
fun ViewInteraction.hasText(resourceId: Int) = UltronInteraction(this).hasText(resourceId)
fun ViewInteraction.hasText(stringMatcher: Matcher<String>) =
    UltronInteraction(this).hasText(stringMatcher)
fun ViewInteraction.textContains(text: String) = UltronInteraction(this).textContains(text)
fun ViewInteraction.hasContentDescription(text: String) =
    UltronInteraction(this).hasContentDescription(text)
fun ViewInteraction.hasContentDescription(resourceId: Int) =
    UltronInteraction(this).hasContentDescription(resourceId)
fun ViewInteraction.hasContentDescription(charSequenceMatcher: Matcher<CharSequence>) =
    UltronInteraction(this).hasContentDescription(charSequenceMatcher)
fun ViewInteraction.contentDescriptionContains(text: String) =
    UltronInteraction(this).contentDescriptionContains(text)
fun ViewInteraction.assertMatches(condition: Matcher<View>) =
    UltronInteraction(this).assertMatches(condition)