package com.atiurin.sampleapp.tests.uiautomator

import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.UiObject2ElementsPage
import com.atiurin.sampleapp.tests.UiElementsTest
import com.atiurin.ultron.utils.getTargetString
import org.junit.Assert
import org.junit.Test

class UltronUiObject2AssertionsTest: UiElementsTest() {
    val page = UiObject2ElementsPage()

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

    @Test
    fun isNotDisplayed_ofDisplayedObject() {
        AssertUtils.assertException { page.button.isDisplayed().isNotDisplayed(100) }
    }

    @Test
    fun isNotDisplayed_ofInvisibleObject() {
        page.radioInvisibleButton.click()
        page.button.isNotDisplayed()
    }

    @Test
    fun isDisplayed_ofDisplayedObject() {
        page.button.isDisplayed()
    }

    @Test
    fun isDisplayed_ofInvisibleObject() {
        page.radioInvisibleButton.click()
        AssertUtils.assertException { page.button.isDisplayed(100) }
    }

    @Test
    fun isDisplayed_BooleanTrue_ofInvisibleObject() {
        val result = page.button.isSuccess { isDisplayed(100) }
        Assert.assertTrue(result)
    }

    @Test
    fun isDisplayed_BooleanFalse_ofInvisibleObject() {
        page.radioInvisibleButton.click()
        val result = page.button.isSuccess { isDisplayed(100) }
        Assert.assertFalse(result)
    }
}