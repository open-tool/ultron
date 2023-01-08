package com.atiurin.sampleapp.tests.uiautomator

import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.framework.Log
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

        AssertUtils.assertException { page.editTextContentDesc.withTimeout(100).withTimeout(100).hasText("invalid text") }
    }

    @Test
    fun hasText_notExisted(){
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).withTimeout(100).hasText("asd") }
    }

    //hasText matcher
    @Test
    fun hasText_matcher_CorrectText() {
        page.editTextContentDesc.hasText(containsString("content description"))
    }

    @Test
    fun hasText_matcher_InvalidText() {
        AssertUtils.assertException { page.editTextContentDesc.withTimeout(100).hasText(equalToIgnoringCase("invalid text") ) }
    }

    @Test
    fun hasText_matcher_notExisted(){
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).hasText(containsString("asd") ) }
    }

    //textContains
    @Test
    fun textContains_validText(){
        val substring = getTargetString(R.string.button_text).substring(0, 5)
        page.button.textContains(substring)
    }

    @Test
    fun textContains_invalidText(){
        AssertUtils.assertException { page.button.withTimeout(100).textContains("invalid substring" ) }
    }

    @Test
    fun textContains_notExisted(){
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).textContains("invalid substring" ) }
    }

    //textIsNullOrEmpty
    @Test
    fun textIsNullOrEmpty_nullText(){
        page.editTextContentDesc.clear().withTimeout(100).textIsNullOrEmpty()
    }

    @Test
    fun textIsNullOrEmpty_notEmptyText(){
        AssertUtils.assertException { page.editTextContentDesc.withTimeout(100).textIsNullOrEmpty() }
    }

    @Test
    fun textIsNullOrEmpty_notExisted(){
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).textIsNullOrEmpty() }
    }

    //textIsNotNullOrEmpty
    @Test
    fun textIsNotNullOrEmpty_notEmptyText(){
        page.editTextContentDesc.textIsNotNullOrEmpty()
    }

    @Test
    fun textIsNotNullOrEmpty_nullText(){
        AssertUtils.assertException { page.editTextContentDesc.clear().withTimeout(100).textIsNotNullOrEmpty() }
    }

    //contentDescription

    //hasContentDescription
    @Test
    fun hasContentDescription_CorrectText_withResourceId() {
        page.button.hasContentDescription(getTargetString(R.string.button_default_content_desc))
    }

    @Test
    fun hasContentDescription_InvalidText_withResourceId() {
        AssertUtils.assertException { page.button.withTimeout(100).hasContentDescription("invalid text" ) }
    }

    @Test
    fun hasContentDescription_notExisted(){
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).hasContentDescription("asd" ) }
    }

    //contentDescription matcher
    @Test
    fun hasContentDescription_matcher_CorrectText() {
        page.button.hasContentDescription(containsString(getTargetString(R.string.button_default_content_desc).substring(0, 5)))
    }

    @Test
    fun hasContentDescription_matcher_InvalidText() {
        AssertUtils.assertException { page.button.withTimeout(100).hasContentDescription(equalToIgnoringCase("invalid text") ) }
    }

    @Test
    fun hasContentDescription_matcher_notExisted(){
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).hasContentDescription(containsString("asd") ) }
    }

    //contentDescriptionContains
    @Test
    fun contentDescriptionContains_validText(){
        val substring = getTargetString(R.string.button_default_content_desc).substring(0, 5)
        page.button.contentDescriptionContains(substring)
    }

    @Test
    fun contentDescriptionContains_invalidText(){
        AssertUtils.assertException { page.button.withTimeout(100).contentDescriptionContains("invalid substring" ) }
    }

    @Test
    fun contentDescriptionContains_notExisted(){
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).contentDescriptionContains("invalid substring" ) }
    }

    //contentDescriptionIsNullOrEmpty
    @Test
    fun contentDescriptionIsNullOrEmpty_nullText(){
        page.editTextContentDesc.clear()
        page.button.contentDescriptionIsNullOrEmpty()
    }

    @Test
    fun contentDescriptionIsNullOrEmpty_notEmptyText(){
        AssertUtils.assertException { page.button.withTimeout(100).contentDescriptionIsNullOrEmpty() }
    }

    @Test
    fun contentDescriptionIsNullOrEmpty_notExisted(){
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).contentDescriptionIsNullOrEmpty() }
    }

    //contentDescriptionIsNotNullOrEmpty
    @Test
    fun contentDescriptionIsNotNullOrEmpty_notEmptyText(){
        page.button.contentDescriptionIsNotNullOrEmpty()
    }

    @Test
    fun contentDescriptionIsNotNullOrEmpty_nullText(){
        page.editTextContentDesc.clear()
        AssertUtils.assertException { page.button.withTimeout(100).contentDescriptionIsNotNullOrEmpty() }
    }
    //isDisplayed
    @Test
    fun isDisplayed_ofDisplayedObject() {
        page.button.isDisplayed()
    }

    @Test
    fun isDisplayed_ofInvisibleObject() {
        page.radioInvisibleButton.click()
        AssertUtils.assertException { page.button.withTimeout(100).isDisplayed() }
    }

    @Test
    fun isDisplayed_BooleanTrue_ofInvisibleObject() {
        val result = page.radioVisibleButton.isSuccess { withTimeout(100).isDisplayed() }
        Assert.assertTrue(result)
    }

    @Test
    fun isDisplayed_BooleanFalse_ofInvisibleObject() {
        page.radioInvisibleButton.click()
        val result = page.button.isSuccess { withTimeout(100).isDisplayed() }
        Assert.assertFalse(result)
    }
    //isNotDisplayed
    @Test
    fun isNotDisplayed_ofDisplayedObject() {
        AssertUtils.assertException { page.button.isDisplayed().withTimeout(100).isNotDisplayed() }
    }

    @Test
    fun isNotDisplayed_ofInvisibleObject() {
        page.radioInvisibleButton.click()
        page.button.isNotDisplayed()
    }

    //isCheckable
    @Test
    fun isCheckable_ofCheckable(){
        page.checkBoxClickable.isCheckable()
    }
    @Test
    fun isCheckable_ofNotCheckable(){
        AssertUtils.assertException { page.emptyImageView.withTimeout(100).isCheckable() }
    }

    //isNotCheckable
    @Test
    fun isNotCheckable_ofNotCheckable(){
        page.emptyImageView.isNotCheckable()
    }

    @Test
    fun isNotCheckable_ofCheckable(){
        AssertUtils.assertException { page.checkBoxClickable.withTimeout(100).isNotCheckable() }
    }

    //isChecked
    @Test
    fun isChecked_ofChecked(){
        page.checkBoxClickable.isChecked()
    }

    @Test
    fun isChecked_ofNotChecked(){
        AssertUtils.assertException { page.checkBoxSelected.withTimeout(100).isChecked() }
    }
    //isNotChecked
    @Test
    fun isNotChecked_ofNotChecked(){
        page.checkBoxSelected.isNotChecked()
    }

    @Test
    fun isNotChecked_ofChecked(){
        AssertUtils.assertException { page.checkBoxClickable.withTimeout(100).isNotChecked() }
    }

    //isClickable
    @Test
    fun isClickable_ofClickable(){
        page.button.isClickable()
    }

    @Test
    fun isClickable_ofNotClickable(){
        page.checkBoxClickable.click()
        AssertUtils.assertException { page.button.withTimeout(100).isClickable() }
    }

    //isNotClickable
    @Test
    fun isNotClickable_ofNotClickable(){
        page.checkBoxClickable.click()
        page.button.isNotClickable()
    }

    @Test
    fun isNotClickable_ofClickable(){
        AssertUtils.assertException { page.button.withTimeout(100).isNotClickable() }
    }

    //isEnabled
    @Test
    fun isEnabled_ofEnable(){
        page.button.isEnabled()
    }

    @Test
    fun isEnabled_ofNotEnable(){
        page.checkBoxEnabled.click()
        AssertUtils.assertException { page.button.withTimeout(100).isEnabled() }
    }

    //isNotEnabled
    @Test
    fun isNotEnabled_ofNotEnable(){
        page.checkBoxEnabled.click()
        page.button.isNotEnabled()
    }

    @Test
    fun isNotEnabled_ofEnable(){
        AssertUtils.assertException { page.button.withTimeout(100).isNotEnabled() }
    }

    //isFocusable
    @Test
    fun isFocusable_ofFocusable(){
        page.button.isFocusable()
    }

    @Test
    fun isFocusable_ofNotFocusable(){
        page.checkBoxFocusable.click()
        AssertUtils.assertException { page.button.withTimeout(100).isFocusable() }
    }

    //isNotFocusable
    @Test
    fun isNotFocusable_ofNotFocusable(){
        page.checkBoxFocusable.click()
        page.button.isNotFocusable()
    }

    @Test
    fun isNotFocusable_ofFocusable(){
        AssertUtils.assertException { page.button.withTimeout(100).isNotFocusable() }
    }

    //isFocused
    @Test
    fun isFocused_ofFocused(){
        page.editTextContentDesc.click().isFocused()
    }

    @Test
    fun isFocused_ofNotFocused(){
        AssertUtils.assertException { page.editTextContentDesc.withTimeout(100).isFocused() }
    }

    //isNotFocused
    @Test
    fun isNotFocused_ofNotFocused(){
        page.editTextContentDesc.isNotFocused()
    }

    @Test
    fun isNotFocused_ofFocused(){
        AssertUtils.assertException { page.editTextContentDesc.click().withTimeout(100).isNotFocused() }
    }

    //isLongClickable
    @Test
    fun isLongClickable_ofLongClickable(){
        page.button.isLongClickable()
    }

    @Test
    fun isLongClickable_ofNotLongClickable(){
        AssertUtils.assertException { page.emptyImageView.withTimeout(100).isLongClickable() }
    }

    //isNotLongClickable
    @Test
    fun isNotLongClickable_ofNotLongClickable(){
        page.emptyImageView.isNotLongClickable()
    }

    @Test
    fun isNotLongClickable_ofLongClickable(){
        AssertUtils.assertException { page.button.withTimeout(100).isNotLongClickable() }
    }

    //isSelected
    @Test
    fun isSelected_ofSelected(){
        page.checkBoxSelected.click()
        page.button.isSelected()
    }

    @Test
    fun isSelected_ofNotSelected(){
        AssertUtils.assertException { page.button.withTimeout(100).isSelected() }
    }

    //isNotSelected
    @Test
    fun isNotSelected_ofNotSelected(){
        page.button.isNotSelected()
    }

    @Test
    fun isNotSelected_ofSelected(){
        page.checkBoxSelected.click()
        AssertUtils.assertException { page.button.withTimeout(100).isNotSelected() }
    }

    //isNotScrollable
    @Test
    fun isNotScrollable_ofNotScrollable(){
        page.button.isNotScrollable()
    }

    @Test
    fun isScrollable_ofNotScrollable(){
        AssertUtils.assertException { page.button.withTimeout(100).isScrollable() }
    }

    //assertThat
    @Test
    fun assertThat_validAssertion_existedObject(){
        page.button.assertThat({ this.isClickable }, "Object is clickable")
    }

    @Test
    fun assertThat_invalidAssertion_existedObject(){
        AssertUtils.assertException { page.button.withTimeout(100).assertThat({ !this.isClickable }, "Object isn't clickable") }
    }

    @Test
    fun assertThat_notExistedObject(){
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).assertThat({ !this.isClickable }, "Should fail") }
    }

    @Test
    fun resultHandlerTest(){
        page.editTextContentDesc.withResultHandler {
            Assert.assertFalse(it.success)
            Assert.assertTrue(it.description.isNotEmpty())
        }.withTimeout(100).hasText("invalid text")
        AssertUtils.assertException { page.editTextContentDesc.withTimeout(100).isNotDisplayed() }
    }
}