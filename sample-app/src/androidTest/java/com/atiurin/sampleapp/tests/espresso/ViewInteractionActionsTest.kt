package com.atiurin.sampleapp.tests.espresso

import android.os.SystemClock
import android.view.KeyEvent
import androidx.test.espresso.action.EspressoKey
import androidx.test.espresso.action.ViewActions.click
import com.atiurin.ultron.extensions.*
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.framework.utils.TestDataUtils.getResourceString
import com.atiurin.sampleapp.pages.UiElementsPage
import com.atiurin.sampleapp.tests.UiElementsTest
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.action.EspressoActionType
import org.junit.Assert
import org.junit.Test

class ViewInteractionActionsTest : UiElementsTest() {
    val page = UiElementsPage

    @Test
    fun click_onClickable() {
        page.button.click()
        page.eventStatus.containsText(getResourceString(R.string.button_event_click))
    }

    @Test
    fun longClick_onLongClickable() {
        page.button.longClick()
        page.eventStatus.containsText(getResourceString(R.string.button_event_long_click))
    }

    @Test
    fun doubleClick_onClickable() {
        page.button.doubleClick()
        var success = false
        with(page.eventStatus){
            containsText(getResourceString(R.string.button_event_click))
            success = isSuccess { containsText("1", 3000) } ||  isSuccess { containsText("2", 3000) }
        }
        Assert.assertTrue(success)
    }

    @Test
    fun typeText_onEditable() {
        val text1 = "begin"
        val text2 = "simple text"
        page.editTextContentDesc.replaceText(text1).typeText(text2).hasText("$text1$text2")
    }

    @Test
    fun typeText_onNotEditable() {
        AssertUtils.assertException { page.eventStatus.typeText("simple text", 1000) }
    }

    @Test
    fun replaceText_onEditable() {
        val text = "simple text"
        page.editTextContentDesc.replaceText(text).hasText(text)
    }

    @Test
    fun clearText_onEditable() {
        page.editTextContentDesc.clearText().hasText("")
    }

    @Test
    fun pressKey_onEditable() {
        val text = "simple text"
        val expectedText = text.substring(0, text.length - 1)
        page.editTextContentDesc
            .replaceText(text)
            .click()
            .pressKey(KeyEvent.KEYCODE_DEL)
            .hasText(expectedText)
    }

    @Test
    fun pressEspressoKey_onEditable() {
        val text = "simple text"
        val expectedText = text.substring(0, text.length - 1)
        page.editTextContentDesc
            .replaceText(text)
            .click()
            .pressKey(EspressoKey.Builder().withKeyCode(KeyEvent.KEYCODE_DEL).build())
            .hasText(expectedText)
    }

    @Test
    fun closeSoftKeyboard_whenItOpened(){
        page.editTextContentDesc.click()
        SystemClock.sleep(500)
        page.editTextContentDesc.closeSoftKeyboard()
    }

    @Test
    fun executeCustomClick_onClickable(){
        page.button.execute(click())
        page.eventStatus.containsText(getResourceString(R.string.button_event_click))
    }
}