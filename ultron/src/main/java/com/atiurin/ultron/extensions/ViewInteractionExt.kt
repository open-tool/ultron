package com.atiurin.ultron.extensions

import android.view.View
import androidx.annotation.DrawableRes
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.EspressoKey
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
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
fun ViewInteraction.withTimeout(timeoutMs: Long) = UltronEspressoInteraction(this).withTimeout(timeoutMs)
fun ViewInteraction.withResultHandler(resultHandler: (EspressoOperationResult<UltronEspressoOperation>) -> Unit) = UltronEspressoInteraction(this).withResultHandler(resultHandler)

//actions
fun ViewInteraction.click() = UltronEspressoInteraction(this).click()
fun ViewInteraction.doubleClick() = UltronEspressoInteraction(this).doubleClick()
fun ViewInteraction.longClick() = UltronEspressoInteraction(this).longClick()
fun ViewInteraction.typeText(text: String) = UltronEspressoInteraction(this).typeText(text)
fun ViewInteraction.replaceText(text: String) = UltronEspressoInteraction(this).replaceText(text)
fun ViewInteraction.clearText() = UltronEspressoInteraction(this).clearText()
fun ViewInteraction.pressKey(keyCode: Int) = UltronEspressoInteraction(this).pressKey(keyCode)
fun ViewInteraction.pressKey(key: EspressoKey) = UltronEspressoInteraction(this).pressKey(key)
fun ViewInteraction.closeSoftKeyboard() = UltronEspressoInteraction(this).closeSoftKeyboard()
fun ViewInteraction.swipeLeft() = UltronEspressoInteraction(this).swipeLeft()
fun ViewInteraction.swipeRight() = UltronEspressoInteraction(this).swipeRight()
fun ViewInteraction.swipeUp() = UltronEspressoInteraction(this).swipeUp()
fun ViewInteraction.swipeDown() = UltronEspressoInteraction(this).swipeDown()
fun ViewInteraction.scrollTo() = UltronEspressoInteraction(this).scrollTo()
fun ViewInteraction.perform(viewAction: ViewAction) = UltronEspressoInteraction(this).perform(viewAction)

//assertions
fun ViewInteraction.isDisplayed() = UltronEspressoInteraction(this).isDisplayed()
fun ViewInteraction.isNotDisplayed() = UltronEspressoInteraction(this).isNotDisplayed()
fun ViewInteraction.exists() = UltronEspressoInteraction(this).exists()
fun ViewInteraction.doesNotExist() = UltronEspressoInteraction(this).doesNotExist()
fun ViewInteraction.isCompletelyDisplayed() = UltronEspressoInteraction(this).isCompletelyDisplayed()
fun ViewInteraction.isDisplayingAtLeast(percentage: Int) =
    UltronEspressoInteraction(this).isDisplayingAtLeast(percentage)
fun ViewInteraction.isEnabled() = UltronEspressoInteraction(this).isEnabled()
fun ViewInteraction.isNotEnabled() = UltronEspressoInteraction(this).isNotEnabled()
fun ViewInteraction.isSelected() = UltronEspressoInteraction(this).isSelected()
fun ViewInteraction.isNotSelected() = UltronEspressoInteraction(this).isNotSelected()
fun ViewInteraction.isClickable() = UltronEspressoInteraction(this).isClickable()
fun ViewInteraction.isNotClickable() = UltronEspressoInteraction(this).isNotClickable()
fun ViewInteraction.isChecked() = UltronEspressoInteraction(this).isChecked()
fun ViewInteraction.isNotChecked() = UltronEspressoInteraction(this).isNotChecked()
fun ViewInteraction.isFocusable() = UltronEspressoInteraction(this).isFocusable()
fun ViewInteraction.isNotFocusable() = UltronEspressoInteraction(this).isNotFocusable()
fun ViewInteraction.hasFocus() = UltronEspressoInteraction(this).hasFocus()
fun ViewInteraction.isJavascriptEnabled() = UltronEspressoInteraction(this).isJavascriptEnabled()
fun ViewInteraction.hasText(text: String) = UltronEspressoInteraction(this).hasText(text)
fun ViewInteraction.hasText(resourceId: Int) = UltronEspressoInteraction(this).hasText(resourceId)
fun ViewInteraction.hasText(stringMatcher: Matcher<String>) =
    UltronEspressoInteraction(this).hasText(stringMatcher)
fun ViewInteraction.textContains(text: String) = UltronEspressoInteraction(this).textContains(text)
fun ViewInteraction.hasContentDescription(text: String) =
    UltronEspressoInteraction(this).hasContentDescription(text)
fun ViewInteraction.hasContentDescription(resourceId: Int) =
    UltronEspressoInteraction(this).hasContentDescription(resourceId)
fun ViewInteraction.hasContentDescription(charSequenceMatcher: Matcher<CharSequence>) =
    UltronEspressoInteraction(this).hasContentDescription(charSequenceMatcher)
fun ViewInteraction.contentDescriptionContains(text: String) =
    UltronEspressoInteraction(this).contentDescriptionContains(text)
fun ViewInteraction.assertMatches(condition: Matcher<View>) =
    UltronEspressoInteraction(this).assertMatches(condition)
