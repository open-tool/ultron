package com.atiurin.ultron.extensions

import android.view.View
import androidx.annotation.DrawableRes
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.EspressoKey
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import org.hamcrest.Matcher

fun DataInteraction.isSuccess(
    action: DataInteraction.() -> Unit
): Boolean {
    var success = true
    try {
        action()
    }catch (th: Throwable){
        success = false
    }
    return success
}
fun DataInteraction.withTimeout(timeoutMs: Long) = UltronEspressoInteraction(this).withTimeout(timeoutMs)
fun DataInteraction.withResultHandler(resultHandler: (EspressoOperationResult<UltronEspressoOperation>) -> Unit) = UltronEspressoInteraction(this).withResultHandler(resultHandler)
//actions
fun DataInteraction.click() = UltronEspressoInteraction(this).click()
fun DataInteraction.doubleClick() = UltronEspressoInteraction(this).doubleClick()
fun DataInteraction.longClick() = UltronEspressoInteraction(this).longClick()
fun DataInteraction.typeText(text: String) = UltronEspressoInteraction(this).typeText(text)
fun DataInteraction.replaceText(text: String) = UltronEspressoInteraction(this).replaceText(text)
fun DataInteraction.clearText() = UltronEspressoInteraction(this).clearText()
fun DataInteraction.pressKey(keyCode: Int) = UltronEspressoInteraction(this).pressKey(keyCode)
fun DataInteraction.pressKey(key: EspressoKey) = UltronEspressoInteraction(this).pressKey(key)
fun DataInteraction.closeSoftKeyboard() = UltronEspressoInteraction(this).closeSoftKeyboard()
fun DataInteraction.swipeLeft() = UltronEspressoInteraction(this).swipeLeft()
fun DataInteraction.swipeRight() = UltronEspressoInteraction(this).swipeRight()
fun DataInteraction.swipeUp() = UltronEspressoInteraction(this).swipeUp()
fun DataInteraction.swipeDown() = UltronEspressoInteraction(this).swipeDown()
fun DataInteraction.scrollTo() = UltronEspressoInteraction(this).scrollTo()
fun DataInteraction.perform(viewAction: ViewAction) = UltronEspressoInteraction(this).perform(viewAction)

//assertions
fun DataInteraction.isDisplayed() = UltronEspressoInteraction(this).isDisplayed()
fun DataInteraction.isNotDisplayed() = UltronEspressoInteraction(this).isNotDisplayed()
fun DataInteraction.doesNotExist() = UltronEspressoInteraction(this).doesNotExist()
fun DataInteraction.isCompletelyDisplayed() = UltronEspressoInteraction(this).isCompletelyDisplayed()
fun DataInteraction.isDisplayingAtLeast(percentage: Int) =
    UltronEspressoInteraction(this).isDisplayingAtLeast(percentage)
fun DataInteraction.isEnabled() = UltronEspressoInteraction(this).isEnabled()
fun DataInteraction.isNotEnabled() = UltronEspressoInteraction(this).isNotEnabled()
fun DataInteraction.isSelected() = UltronEspressoInteraction(this).isSelected()
fun DataInteraction.isNotSelected() = UltronEspressoInteraction(this).isNotSelected()
fun DataInteraction.isClickable() = UltronEspressoInteraction(this).isClickable()
fun DataInteraction.isNotClickable() = UltronEspressoInteraction(this).isNotClickable()
fun DataInteraction.isChecked() = UltronEspressoInteraction(this).isChecked()
fun DataInteraction.isNotChecked() = UltronEspressoInteraction(this).isNotChecked()
fun DataInteraction.isFocusable() = UltronEspressoInteraction(this).isFocusable()
fun DataInteraction.isNotFocusable() = UltronEspressoInteraction(this).isNotFocusable()
fun DataInteraction.hasFocus() = UltronEspressoInteraction(this).hasFocus()
fun DataInteraction.isJavascriptEnabled() = UltronEspressoInteraction(this).isJavascriptEnabled()
fun DataInteraction.hasText(text: String) = UltronEspressoInteraction(this).hasText(text)
fun DataInteraction.hasText(resourceId: Int) = UltronEspressoInteraction(this).hasText(resourceId)
fun DataInteraction.hasText(stringMatcher: Matcher<String>) =
    UltronEspressoInteraction(this).hasText(stringMatcher)
fun DataInteraction.textContains(text: String) = UltronEspressoInteraction(this).textContains(text)
fun DataInteraction.hasContentDescription(text: String) =
    UltronEspressoInteraction(this).hasContentDescription(text)
fun DataInteraction.hasContentDescription(resourceId: Int) =
    UltronEspressoInteraction(this).hasContentDescription(resourceId)
fun DataInteraction.hasContentDescription(charSequenceMatcher: Matcher<CharSequence>) =
    UltronEspressoInteraction(this).hasContentDescription(charSequenceMatcher)
fun DataInteraction.contentDescriptionContains(text: String) =
    UltronEspressoInteraction(this).contentDescriptionContains(text)
fun DataInteraction.assertMatches(condition: Matcher<View>) =
    UltronEspressoInteraction(this).assertMatches(condition)
