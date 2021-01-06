package com.atiurin.sampleapp.tests.uiautomator

import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.framework.Log
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.framework.utils.TestDataUtils
import com.atiurin.sampleapp.pages.BySelectorUiElementsPage
import com.atiurin.sampleapp.tests.UiElementsTest
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.extensions.getParent
import com.atiurin.ultron.extensions.getText
import com.atiurin.ultron.extensions.hasText
import com.atiurin.ultron.utils.getTargetString
import junit.framework.Assert
import org.junit.Before
import org.junit.Test

class BySelectorTest: UiElementsTest() {
    val page = BySelectorUiElementsPage()

    @Before
    fun speedUpAutomator(){
        UltronConfig.UiAutomator.speedUp()
    }

    @Test
    fun getParent(){
        Log.debug(">>>> ${page.button.getParent()?.getResourceName()} ${page.button.getParent()?.getClassName()}")
    }
    @Test
    fun click_onClickable() {
        page.button.click()
        page.eventStatus.textContains(TestDataUtils.getResourceString(R.string.button_event_click))
    }

    @Test
    fun click_onNotExistedObject() {
        AssertUtils.assertException { page.notExistedObject.click(100) }
    }

    @Test
    fun hasText_CorrectText_withResourceId() {
        page.editTextContentDesc.hasText("Default content description")
    }

    @Test
    fun hasText_InvalidText_withResourceId() {
        AssertUtils.assertException { page.editTextContentDesc.hasText("invalid text", 100) }
    }

    @Test
    fun getTextTest(){
        Assert.assertEquals(getTargetString(R.string.button_text), page.button.getText())
    }
}