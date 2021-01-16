package com.atiurin.ultron.extensions

import android.view.View
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.EspressoKey
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.EspressoOperation
import com.atiurin.ultron.core.espresso.UltronInteraction
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
fun DataInteraction.withTimeout(timeoutMs: Long) = UltronInteraction(this).withTimeout(timeoutMs)
fun DataInteraction.withResultHandler(resultHandler: (EspressoOperationResult<EspressoOperation>) -> Unit) = UltronInteraction(this).withResultHandler(resultHandler)
//actions
fun DataInteraction.click() = UltronInteraction(this).click()
fun DataInteraction.doubleClick() = UltronInteraction(this).doubleClick()
fun DataInteraction.longClick() = UltronInteraction(this).longClick()
fun DataInteraction.typeText(text: String) = UltronInteraction(this).typeText(text)
fun DataInteraction.replaceText(text: String) = UltronInteraction(this).replaceText(text)
fun DataInteraction.clearText() = UltronInteraction(this).clearText()
fun DataInteraction.pressKey(keyCode: Int) = UltronInteraction(this).pressKey(keyCode)
fun DataInteraction.pressKey(key: EspressoKey) = UltronInteraction(this).pressKey(key)
fun DataInteraction.closeSoftKeyboard() = UltronInteraction(this).closeSoftKeyboard()
fun DataInteraction.swipeLeft() = UltronInteraction(this).swipeLeft()
fun DataInteraction.swipeRight() = UltronInteraction(this).swipeRight()
fun DataInteraction.swipeUp() = UltronInteraction(this).swipeUp()
fun DataInteraction.swipeDown() = UltronInteraction(this).swipeDown()
fun DataInteraction.scrollTo() = UltronInteraction(this).scrollTo()
fun DataInteraction.perform(viewAction: ViewAction) = UltronInteraction(this).perform(viewAction)

//assertions
fun DataInteraction.isDisplayed() = UltronInteraction(this).isDisplayed()
fun DataInteraction.isNotDisplayed() = UltronInteraction(this).isNotDisplayed()
fun DataInteraction.doesNotExist() = UltronInteraction(this).doesNotExist()
fun DataInteraction.isCompletelyDisplayed() = UltronInteraction(this).isCompletelyDisplayed()
fun DataInteraction.isDisplayingAtLeast(percentage: Int) =
    UltronInteraction(this).isDisplayingAtLeast(percentage)
fun DataInteraction.isEnabled() = UltronInteraction(this).isEnabled()
fun DataInteraction.isNotEnabled() = UltronInteraction(this).isNotEnabled()
fun DataInteraction.isSelected() = UltronInteraction(this).isSelected()
fun DataInteraction.isNotSelected() = UltronInteraction(this).isNotSelected()
fun DataInteraction.isClickable() = UltronInteraction(this).isClickable()
fun DataInteraction.isNotClickable() = UltronInteraction(this).isNotClickable()
fun DataInteraction.isChecked() = UltronInteraction(this).isChecked()
fun DataInteraction.isNotChecked() = UltronInteraction(this).isNotChecked()
fun DataInteraction.isFocusable() = UltronInteraction(this).isFocusable()
fun DataInteraction.isNotFocusable() = UltronInteraction(this).isNotFocusable()
fun DataInteraction.hasFocus() = UltronInteraction(this).hasFocus()
fun DataInteraction.isJavascriptEnabled() = UltronInteraction(this).isJavascriptEnabled()
fun DataInteraction.hasText(text: String) = UltronInteraction(this).hasText(text)
fun DataInteraction.hasText(resourceId: Int) = UltronInteraction(this).hasText(resourceId)
fun DataInteraction.hasText(stringMatcher: Matcher<String>) =
    UltronInteraction(this).hasText(stringMatcher)
fun DataInteraction.textContains(text: String) = UltronInteraction(this).textContains(text)
fun DataInteraction.hasContentDescription(text: String) =
    UltronInteraction(this).hasContentDescription(text)
fun DataInteraction.hasContentDescription(resourceId: Int) =
    UltronInteraction(this).hasContentDescription(resourceId)
fun DataInteraction.hasContentDescription(charSequenceMatcher: Matcher<CharSequence>) =
    UltronInteraction(this).hasContentDescription(charSequenceMatcher)
fun DataInteraction.contentDescriptionContains(text: String) =
    UltronInteraction(this).contentDescriptionContains(text)
fun DataInteraction.assertMatches(condition: Matcher<View>) =
    UltronInteraction(this).assertMatches(condition)