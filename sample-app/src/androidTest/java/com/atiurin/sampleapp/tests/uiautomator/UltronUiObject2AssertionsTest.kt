package com.atiurin.sampleapp.tests.uiautomator

import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.UiObject2ElementsPage
import com.atiurin.sampleapp.tests.UiElementsTest
import com.atiurin.ultron.utils.getTargetString
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalToIgnoringCase
import org.junit.Assert
import org.junit.Test

class UltronUiObject2AssertionsTest: UiElementsTest() {
    val page = UiObject2ElementsPage()

    //hasText
    @Test
    fun hasText_CorrectText_withResourceId() {
        page.editTextContentDesc.hasText(getTargetString(R.string.button_default_content_desc))
    }

    @Test
    fun hasText_InvalidText_withResourceId() {
        AssertUtils.assertException { page.editTextContentDesc.hasText("invalid text", 100) }
    }

    @Test
    fun hasText_notExisted(){
        AssertUtils.assertException { page.notExistedObject.hasText("asd", 100) }
    }

    //hasText matcher
    @Test
    fun hasText_matcher_CorrectText() {
        page.editTextContentDesc.hasText(containsString("content description"))
    }

    @Test
    fun hasText_matcher_InvalidText() {
        AssertUtils.assertException { page.editTextContentDesc.hasText(equalToIgnoringCase("invalid text"), 100) }
    }

    @Test
    fun hasText_matcher_notExisted(){
        AssertUtils.assertException { page.notExistedObject.hasText(containsString("asd"), 100) }
    }

    //textContains
    @Test
    fun textContains_validText(){
        val substring = getTargetString(R.string.button_text).substring(0, 5)
        page.button.textContains(substring)
    }

    @Test
    fun textContains_invalidText(){
        AssertUtils.assertException { page.button.textContains("invalid substring", 100) }
    }

    @Test
    fun textContains_notExisted(){
        AssertUtils.assertException { page.notExistedObject.textContains("invalid substring", 100) }
    }

    //isNullOrEmpty
    @Test
    fun textIsNullOrEmpty_nullText(){
        page.editTextContentDesc.clear().textIsNullOrEmpty()
    }

    @Test
    fun textIsNullOrEmpty_invalidText(){
        AssertUtils.assertException { page.editTextContentDesc.textIsNullOrEmpty(100) }
    }

    @Test
    fun textIsNullOrEmpty_notExisted(){
        AssertUtils.assertException { page.notExistedObject.textIsNullOrEmpty(100) }
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