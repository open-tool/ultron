package com.atiurin.sampleapp.tests.espresso

import androidx.test.espresso.matcher.ViewMatchers.*
import com.atiurin.ultron.custom.espresso.matcher.hasAnyDrawable
import com.atiurin.ultron.custom.espresso.matcher.withDrawable
import com.atiurin.ultron.extensions.*
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.framework.utils.TestDataUtils.getResourceString
import com.atiurin.sampleapp.pages.UiElementsPage
import com.atiurin.sampleapp.tests.UiElementsTest
import com.atiurin.ultron.custom.espresso.assertion.hasCurrentHintTextColor
import com.atiurin.ultron.custom.espresso.assertion.hasCurrentTextColor
import com.atiurin.ultron.custom.espresso.assertion.hasHighlightColor
import com.atiurin.ultron.custom.espresso.assertion.hasShadowColor
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.junit.Assert
import org.junit.Test

class ViewInteractionAssertionsTest : UiElementsTest() {
    val page = UiElementsPage

    //displayed
    @Test
    fun isDisplayed_ofDisplayedObject() {
        page.button.isDisplayed()
    }

    @Test
    fun isDisplayed_ofNotDisplayedObject() {
        page.radioInvisibleButton.click()
        AssertUtils.assertException { page.button.withTimeout(100).isDisplayed() }
    }

    @Test
    fun isNotDisplayed_ofDisplayedObject() {
        page.radioVisibleButton.click()
        AssertUtils.assertException { page.radioVisibleButton.withTimeout(100).isNotDisplayed() }
    }

    @Test
    fun isNotDisplayed_ofNotDisplayedObject() {
        page.radioInvisibleButton.click()
        page.button.isNotDisplayed()
    }

    //doesNotExist
    @Test
    fun doesNotExist_notExisted() {
        page.notExistElement.doesNotExist()
    }

    @Test
    fun doesNotExist_existed() {
        AssertUtils.assertException { page.button.withTimeout(100).doesNotExist() }
    }

    //exists
    @Test
    fun exists_ExistedHiddenView() {
        page.hiddenButton.exists()
    }

    @Test
    fun exists_NotExisted() {
        AssertUtils.assertException { page.notExistElement.withTimeout(100).exists() }
    }

    //checked
    @Test
    fun isChecked_ofChecked() {
        page.checkBoxEnabled.isChecked()
    }

    @Test
    fun isChecked_ofNotChecked() {
        AssertUtils.assertException { page.checkBoxSelected.withTimeout(100).isChecked() }
    }

    @Test
    fun isNotChecked_ofChecked() {
        AssertUtils.assertException { page.checkBoxClickable.withTimeout(100).isNotChecked() }
    }

    @Test
    fun isNotChecked_ofNotChecked() {
        page.checkBoxSelected.isNotChecked()
    }

    // selected
    @Test
    fun isSelected_ofSelected() {
        page.checkBoxSelected.click()
        page.button.isSelected()
    }

    @Test
    fun isSelected_ofNotSelected() {
        AssertUtils.assertException { page.button.withTimeout(100).isSelected() }
    }

    @Test
    fun isNotSelected_ofSelected() {
        page.checkBoxSelected.click()
        AssertUtils.assertException { page.button.withTimeout(100).isNotSelected() }
    }

    @Test
    fun isNotSelected_ofNotSelected() {
        page.button.isNotSelected()
    }

    // enabled
    @Test
    fun isEnabled_ofEnabled() {
        page.button.isEnabled()
    }

    @Test
    fun isEnabled_ofNotEnabled() {
        page.checkBoxEnabled.click()
        AssertUtils.assertException { page.button.withTimeout(100).isEnabled() }
    }

    @Test
    fun isNotEnabled_ofEnabled() {
        AssertUtils.assertException { page.button.withTimeout(100).isNotEnabled() }
    }

    @Test
    fun isNotEnabled_ofNotEnabled() {
        page.checkBoxEnabled.click()
        page.button.isNotEnabled()
    }

    //clickable
    @Test
    fun isClickable_ofClickable() {
        page.button.isClickable()
    }

    @Test
    fun isClickable_ofNotClickable() {
        page.checkBoxClickable.click()
        AssertUtils.assertException { page.button.withTimeout(100).isClickable() }
    }

    @Test
    fun isNotClickable_ofClickable() {
        AssertUtils.assertException { page.button.withTimeout(100).isNotClickable() }
    }

    @Test
    fun isNotClickable_ofNotClickable() {
        page.checkBoxClickable.click()
        page.button.isNotClickable()
    }

    //focusable
    @Test
    fun isFocusable_ofFocusable() {
        page.button.isFocusable()
    }

    @Test
    fun isFocusable_ofNotFocusable() {
        page.checkBoxFocusable.click()
        AssertUtils.assertException { page.button.withTimeout(100).isFocusable() }
    }

    @Test
    fun isNotFocusable_ofFocusable() {
        AssertUtils.assertException { page.button.withTimeout(100).isNotFocusable() }
    }

    @Test
    fun isNotFocusable_ofNotFocusable() {
        page.checkBoxFocusable.click()
        page.button.isNotFocusable()
    }

    //hasFocus
    @Test
    fun hasFocus_ofFocused() {
        page.editTextContentDesc.click()
        page.editTextContentDesc.hasFocus()
    }

    @Test
    fun hasFocus_ofNotFocused() {
        AssertUtils.assertException { page.editTextContentDesc.withTimeout(100).hasFocus() }
    }

    //hasText
    @Test
    fun hasText_CorrectText_withResourceId() {
        page.editTextContentDesc.hasText(R.string.button_default_content_desc)
    }

    @Test
    fun hasText_InvalidSubstringText() {
        val text = getResourceString(R.string.button_default_content_desc)
        AssertUtils.assertException { page.editTextContentDesc.withTimeout(100).hasText(text.substring(3)) }
    }

    @Test
    fun hasText_InvalidText_withResourceId() {
        AssertUtils.assertException {
            page.editTextContentDesc.withTimeout(100).hasText(
                R.string.action_clear_history
            )
        }
    }

    @Test
    fun hasText_CorrectText_withString() {
        val text = getResourceString(R.string.button_default_content_desc)
        page.editTextContentDesc.hasText(text)
    }

    @Test
    fun hasText_InvalidText_withString() {
        val text = getResourceString(R.string.button_default_content_desc)
        AssertUtils.assertException {
            page.editTextContentDesc.withTimeout(100).hasText(
                "$text to be invalid"
            )
        }
    }

    @Test
    fun hasText_CorrectText_withStringMatcher() {
        val text = getResourceString(R.string.button_default_content_desc)
        page.editTextContentDesc.hasText(containsString(text.substring(2)))
    }

    @Test
    fun hasText_InvalidText_withStringMatcher() {
        val text = getResourceString(R.string.button_default_content_desc)
        AssertUtils.assertException {
            page.editTextContentDesc.withTimeout(100).hasText(
                containsString("$text to be invalid")
            )
        }
    }

    //containsText
    @Test
    fun containsText_CorrectText_withResourceId() {
        val text = getResourceString(R.string.button_default_content_desc)
        page.editTextContentDesc.textContains(text.substring(3))
    }

    @Test
    fun containsText_InvalidSubstringText() {
        val text = getResourceString(R.string.button_default_content_desc)
        AssertUtils.assertException {
            page.editTextContentDesc.withTimeout(100).textContains(
                "${text.substring(3)} to be invalid"
            )
        }
    }

    //hasContentDescription
    @Test
    fun hasContentDescription_CorrectText_withResourceId() {
        page.button.hasContentDescription(R.string.button_default_content_desc)
    }

    @Test
    fun hasContentDescription_InvalidSubstringText() {
        val text = getResourceString(R.string.button_default_content_desc)
        AssertUtils.assertException { page.button.withTimeout(100).hasContentDescription(text.substring(3)) }
    }

    @Test
    fun hasContentDescription_InvalidText_withResourceId() {
        AssertUtils.assertException {
            page.button.withTimeout(100).hasContentDescription(
                R.string.action_clear_history
            )
        }
    }

    @Test
    fun hasContentDescription_CorrectText_withString() {
        val text = getResourceString(R.string.button_default_content_desc)
        page.button.hasContentDescription(text)
    }

    @Test
    fun hasContentDescription_InvalidText_withString() {
        val text = getResourceString(R.string.button_default_content_desc)
        AssertUtils.assertException {
            page.button.withTimeout(100).hasContentDescription(
                "$text to be invalid"
            )
        }
    }

    //contentDescriptionContains
    @Test
    fun contentDescriptionContains_CorrectText_withString() {
        val text = getResourceString(R.string.button_default_content_desc)
        page.button.withTimeout(100).contentDescriptionContains(text.substring(2))
    }

    @Test
    fun contentDescriptionContains_InvalidText_withString() {
        val text = getResourceString(R.string.button_default_content_desc)
        AssertUtils.assertException {
            page.button.withTimeout(100).contentDescriptionContains(
                "${text.substring(2)} to be invalid"
            )
        }
    }

    //assertMatches
    @Test
    fun assertMatches_ofMatched() {
        page.button.assertMatches(allOf(isDisplayed(), isEnabled(), withText(R.string.button_text)))
    }

    @Test
    fun assertMatches_ofNotMatched() {
        page.checkBoxEnabled.click()
        AssertUtils.assertException {
            page.button.withTimeout(100).assertMatches(
                allOf(
                    isDisplayed(),
                    isEnabled(),
                    withText(R.string.button_text)
                ),
            )
        }
    }

    //javascriptEnabled
    @Test
    fun jsEnabled_ofEnabled() {
        page.webView.isJavascriptEnabled()
    }

    @Test
    fun jsEnabled_ofNotEnabled() {
        page.checkBoxJsEnabled.click()
        AssertUtils.assertException { page.webView.withTimeout(100).isJavascriptEnabled() }
    }
    //isSuccess

    @Test
    fun isSuccess_FalseTest() {
        val success = page.radioVisibleButton.isSuccess { withTimeout(100).isNotDisplayed() }
        Assert.assertFalse(success)
    }

    @Test
    fun isSuccess_TrueTest() {
        val success = page.radioVisibleButton.isSuccess { withTimeout(100).isDisplayed() }
        Assert.assertTrue(success)
    }

    // withAppCompatTextView

    @Test
    fun appCompatTextView_assertText() {
        page.appCompatTextView.hasText(getResourceString(R.string.app_compat_text))
    }

    // hasDrawable

    @Test
    fun hasDrawable_viewHasDrawable() {
        page.imageView.assertMatches(hasAnyDrawable())
    }

    @Test
    fun hasDrawable_viewHasNoDrawable() {
        AssertUtils.assertException { page.emptyNotClickableImageView.withTimeout(100).assertMatches(hasAnyDrawable()) }
    }

    @Test
    fun withDrawable_correctDrawable() {
        page.imageView.assertMatches(withDrawable(R.drawable.ic_account))
    }

    @Test
    fun withDrawable_invalidDrawable() {
        AssertUtils.assertException {
            page.imageView.withTimeout(100).assertMatches(
                withDrawable(R.drawable.ic_attach_file)
            )
        }
    }

    @Test
    fun hasCurrentTextColor() {
        page.eventStatus.hasCurrentTextColor(R.color.colorPrimary)
    }

    @Test
    fun hasCurrentTextColor_invalidColor() {
        AssertUtils.assertException { page.eventStatus.withTimeout(100).hasCurrentTextColor(R.color.invalid) }
    }

    @Test
    fun hasCurrentHintTextColor() {
        page.eventStatus.hasCurrentHintTextColor(R.color.colorHint)
    }

    @Test
    fun hasCurrentHintTextColor_invalidColor() {
        AssertUtils.assertException { page.eventStatus.withTimeout(100).hasCurrentHintTextColor(R.color.invalid) }
    }

    @Test
    fun hasShadowColor() {
        page.eventStatus.hasShadowColor(R.color.colorShadow)
    }

    @Test
    fun hasShadowColor_invalidColor() {
        AssertUtils.assertException { page.eventStatus.withTimeout(100).hasShadowColor(R.color.invalid) }
    }

    @Test
    fun hasHighlightColor() {
        page.eventStatus.hasHighlightColor(R.color.colorHighlight)
    }

    @Test
    fun hasHighlightColor_invalidColor() {
        AssertUtils.assertException { page.eventStatus.withTimeout(100).hasHighlightColor(R.color.invalid) }
    }

    @Test
    fun textViewColors() {
        page.eventStatus
            .hasCurrentTextColor(R.color.colorPrimary)
            .hasCurrentHintTextColor(R.color.colorHint)
            .hasShadowColor(R.color.colorShadow)
            .hasHighlightColor(R.color.colorHighlight)
    }

    @Test
    fun appCompatTextViewTextColor() {
        page.appCompatTextView.hasCurrentTextColor(R.color.colorPrimary)
    }

    @Test
    fun appCompatTextViewTextColor_invalidColor() {
        AssertUtils.assertException { page.appCompatTextView.withTimeout(100).hasCurrentTextColor(R.color.invalid) }
    }
}