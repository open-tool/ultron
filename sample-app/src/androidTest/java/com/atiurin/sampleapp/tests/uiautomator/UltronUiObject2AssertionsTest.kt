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

    //textIsNullOrEmpty
    @Test
    fun textIsNullOrEmpty_nullText(){
        page.editTextContentDesc.clear().textIsNullOrEmpty()
    }

    @Test
    fun textIsNullOrEmpty_notEmptyText(){
        AssertUtils.assertException { page.editTextContentDesc.textIsNullOrEmpty(100) }
    }

    @Test
    fun textIsNullOrEmpty_notExisted(){
        AssertUtils.assertException { page.notExistedObject.textIsNullOrEmpty(100) }
    }

    //textIsNotNullOrEmpty
    @Test
    fun textIsNotNullOrEmpty_notEmptyText(){
        page.editTextContentDesc.textIsNotNullOrEmpty()
    }

    @Test
    fun textIsNotNullOrEmpty_nullText(){
        AssertUtils.assertException { page.editTextContentDesc.clear().textIsNotNullOrEmpty(100) }
    }

    //contentDescription

    //hasContentDescription
    @Test
    fun hasContentDescription_CorrectText_withResourceId() {
        page.button.hasContentDescription(getTargetString(R.string.button_default_content_desc))
    }

    @Test
    fun hasContentDescription_InvalidText_withResourceId() {
        AssertUtils.assertException { page.button.hasContentDescription("invalid text", 100) }
    }

    @Test
    fun hasContentDescription_notExisted(){
        AssertUtils.assertException { page.notExistedObject.hasContentDescription("asd", 100) }
    }

    //contentDescription matcher
    @Test
    fun hasContentDescription_matcher_CorrectText() {
        page.button.hasContentDescription(containsString(getTargetString(R.string.button_default_content_desc).substring(0, 5)))
    }

    @Test
    fun hasContentDescription_matcher_InvalidText() {
        AssertUtils.assertException { page.button.hasContentDescription(equalToIgnoringCase("invalid text"), 100) }
    }

    @Test
    fun hasContentDescription_matcher_notExisted(){
        AssertUtils.assertException { page.notExistedObject.hasContentDescription(containsString("asd"), 100) }
    }

    //contentDescriptionContains
    @Test
    fun contentDescriptionContains_validText(){
        val substring = getTargetString(R.string.button_default_content_desc).substring(0, 5)
        page.button.contentDescriptionContains(substring)
    }

    @Test
    fun contentDescriptionContains_invalidText(){
        AssertUtils.assertException { page.button.contentDescriptionContains("invalid substring", 100) }
    }

    @Test
    fun contentDescriptionContains_notExisted(){
        AssertUtils.assertException { page.notExistedObject.contentDescriptionContains("invalid substring", 100) }
    }

    //contentDescriptionIsNullOrEmpty
    @Test
    fun contentDescriptionIsNullOrEmpty_nullText(){
        page.editTextContentDesc.clear()
        page.button.contentDescriptionIsNullOrEmpty()
    }

    @Test
    fun contentDescriptionIsNullOrEmpty_notEmptyText(){
        AssertUtils.assertException { page.button.contentDescriptionIsNullOrEmpty(100) }
    }

    @Test
    fun contentDescriptionIsNullOrEmpty_notExisted(){
        AssertUtils.assertException { page.notExistedObject.contentDescriptionIsNullOrEmpty(100) }
    }

    //contentDescriptionIsNotNullOrEmpty
    @Test
    fun contentDescriptionIsNotNullOrEmpty_notEmptyText(){
        page.button.contentDescriptionIsNotNullOrEmpty()
    }

    @Test
    fun contentDescriptionIsNotNullOrEmpty_nullText(){
        page.editTextContentDesc.clear()
        AssertUtils.assertException { page.button.contentDescriptionIsNotNullOrEmpty(100) }
    }
    //isDisplayed
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
    //isNotDisplayed
    @Test
    fun isNotDisplayed_ofDisplayedObject() {
        AssertUtils.assertException { page.button.isDisplayed().isNotDisplayed(100) }
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
        AssertUtils.assertException { page.emptyImageView.isCheckable(100) }
    }

    //isNotCheckable
    @Test
    fun isNotCheckable_ofNotCheckable(){
        page.emptyImageView.isNotCheckable()
    }

    @Test
    fun isNotCheckable_ofCheckable(){
        AssertUtils.assertException { page.checkBoxClickable.isNotCheckable(100) }
    }

    //isChecked
    @Test
    fun isChecked_ofChecked(){
        page.checkBoxClickable.isChecked()
    }

    @Test
    fun isChecked_ofNotChecked(){
        AssertUtils.assertException { page.checkBoxUnavailable.isChecked(100) }
    }
    //isNotChecked
    @Test
    fun isNotChecked_ofNotChecked(){
        page.checkBoxUnavailable.isNotChecked()
    }

    @Test
    fun isNotChecked_ofChecked(){
        AssertUtils.assertException { page.checkBoxClickable.isNotChecked(100) }
    }

    //isClickable
    @Test
    fun isClickable_ofClickable(){
        page.button.isClickable()
    }

    @Test
    fun isClickable_ofNotClickable(){
        page.checkBoxClickable.click()
        AssertUtils.assertException { page.button.isClickable(100) }
    }

    //isNotClickable
    @Test
    fun isNotClickable_ofNotClickable(){
        page.checkBoxClickable.click()
        page.button.isNotClickable()
    }

    @Test
    fun isNotClickable_ofClickable(){
        AssertUtils.assertException { page.button.isNotClickable(100) }
    }

    //isEnabled
    @Test
    fun isEnabled_ofEnable(){
        page.button.isEnabled()
    }

    @Test
    fun isEnabled_ofNotEnable(){
        page.checkBoxEnabled.click()
        AssertUtils.assertException { page.button.isEnabled(100) }
    }

    //isNotEnabled
    @Test
    fun isNotEnabled_ofNotEnable(){
        page.checkBoxEnabled.click()
        page.button.isNotEnabled()
    }

    @Test
    fun isNotEnabled_ofEnable(){
        AssertUtils.assertException { page.button.isNotEnabled(100) }
    }

    //isFocusable
    @Test
    fun isFocusable_ofFocusable(){
        page.button.isFocusable()
    }

    @Test
    fun isFocusable_ofNotFocusable(){
        page.checkBoxFocusable.click()
        AssertUtils.assertException { page.button.isFocusable(100) }
    }

    //isNotFocusable
    @Test
    fun isNotFocusable_ofNotFocusable(){
        page.checkBoxFocusable.click()
        page.button.isNotFocusable()
    }

    @Test
    fun isNotFocusable_ofFocusable(){
        AssertUtils.assertException { page.button.isNotFocusable(100) }
    }

    //isFocused
    @Test
    fun isFocused_ofFocused(){
        page.editTextContentDesc.click().isFocused()
    }

    @Test
    fun isFocused_ofNotFocused(){
        AssertUtils.assertException { page.editTextContentDesc.isFocused(100) }
    }

    //isNotFocused
    @Test
    fun isNotFocused_ofNotFocused(){
        page.editTextContentDesc.isNotFocused()
    }

    @Test
    fun isNotFocused_ofFocused(){
        AssertUtils.assertException { page.editTextContentDesc.click().isNotFocused(100) }
    }

    //isLongClickable
    @Test
    fun isLongClickable_ofLongClickable(){
        page.button.isLongClickable()
    }

    @Test
    fun isLongClickable_ofNotLongClickable(){
        AssertUtils.assertException { page.emptyImageView.isLongClickable(100) }
    }

    //isNotLongClickable
    @Test
    fun isNotLongClickable_ofNotLongClickable(){
        page.emptyImageView.isNotLongClickable()
    }

    @Test
    fun isNotLongClickable_ofLongClickable(){
        AssertUtils.assertException { page.button.isNotLongClickable(100) }
    }

    //isSelected
    @Test
    fun isSelected_ofSelected(){
        page.checkBoxSelected.click()
        page.button.isSelected()
    }

    @Test
    fun isSelected_ofNotSelected(){
        AssertUtils.assertException { page.button.isSelected(100) }
    }

    //isNotSelected
    @Test
    fun isNotSelected_ofNotSelected(){
        page.button.isNotSelected()
    }

    @Test
    fun isNotSelected_ofSelected(){
        page.checkBoxSelected.click()
        AssertUtils.assertException { page.button.isNotSelected(100) }
    }

    //isNotScrollable
    @Test
    fun isNotScrollable_ofNotScrollable(){
        page.button.isNotScrollable()
    }

    @Test
    fun isScrollable_ofNotScrollable(){
        AssertUtils.assertException { page.button.isScrollable(100) }
    }

    //assertThat
    @Test
    fun assertThat_validAssertion_existedObject(){
        page.button.assertThat({ this.isClickable }, "Object is clickable")
    }

    @Test
    fun assertThat_invalidAssertion_existedObject(){
        AssertUtils.assertException { page.button.assertThat({ !this.isClickable }, "Object isn't clickable", 100) }
    }

    @Test
    fun assertThat_notExistedObject(){
        AssertUtils.assertException { page.notExistedObject.assertThat({ !this.isClickable }, "Should fail", 100) }
    }
}