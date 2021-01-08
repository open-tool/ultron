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
import com.atiurin.ultron.core.config.UltronConfig
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.junit.Assert
import org.junit.Before
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
        AssertUtils.assertException { page.button.isDisplayed(100) }
    }

    @Test
    fun isNotDisplayed_ofDisplayedObject() {
        page.radioVisibleButton.click()
        AssertUtils.assertException { page.button.isNotDisplayed(100) }
    }

    @Test
    fun isNotDisplayed_ofNotDisplayedObject() {
        page.radioInvisibleButton.click()
        page.button.isNotDisplayed()
    }


    //checked
    @Test
    fun isChecked_ofChecked() {
        page.checkBoxEnabled.isChecked()
    }

    @Test
    fun isChecked_ofNotChecked() {
        AssertUtils.assertException { page.checkBoxSelected.isChecked(100) }
    }

    @Test
    fun isNotChecked_ofChecked() {
        AssertUtils.assertException { page.checkBoxClickable.isNotChecked(100) }
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
        AssertUtils.assertException { page.button.isSelected(100) }
    }

    @Test
    fun isNotSelected_ofSelected() {
        page.checkBoxSelected.click()
        AssertUtils.assertException { page.button.isNotSelected(100) }
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
        AssertUtils.assertException { page.button.isEnabled(100) }
    }

    @Test
    fun isNotEnabled_ofEnabled() {
        AssertUtils.assertException { page.button.isNotEnabled(100) }
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
        AssertUtils.assertException { page.button.isClickable(100) }
    }

    @Test
    fun isNotClickable_ofClickable() {
        AssertUtils.assertException { page.button.isNotClickable(100) }
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
        AssertUtils.assertException { page.button.isFocusable(100) }
    }

    @Test
    fun isNotFocusable_ofFocusable() {
        AssertUtils.assertException { page.button.isNotFocusable(100) }
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
        AssertUtils.assertException { page.editTextContentDesc.hasFocus(100) }
    }

    //hasText
    @Test
    fun hasText_CorrectText_withResourceId() {
        page.editTextContentDesc.hasText(R.string.button_default_content_desc)
    }

    @Test
    fun hasText_InvalidSubstringText() {
        val text = getResourceString(R.string.button_default_content_desc)
        AssertUtils.assertException { page.editTextContentDesc.hasText(text.substring(3), 100) }
    }

    @Test
    fun hasText_InvalidText_withResourceId() {
        AssertUtils.assertException {
            page.editTextContentDesc.hasText(
                R.string.action_clear_history,
                1000
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
            page.editTextContentDesc.hasText(
                "$text to be invalid",
                1000
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
            page.editTextContentDesc.hasText(
                containsString("$text to be invalid"),
                1000
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
            page.editTextContentDesc.textContains(
                "${text.substring(3)} to be invalid",
                1000
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
        AssertUtils.assertException { page.button.hasContentDescription(text.substring(3), 100) }
    }

    @Test
    fun hasContentDescription_InvalidText_withResourceId() {
        AssertUtils.assertException {
            page.button.hasContentDescription(
                R.string.action_clear_history,
                1000
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
            page.button.hasContentDescription(
                "$text to be invalid",
                1000
            )
        }
    }

    //contentDescriptionContains
    @Test
    fun contentDescriptionContains_CorrectText_withString() {
        val text = getResourceString(R.string.button_default_content_desc)
        page.button.contentDescriptionContains(text.substring(2), 100)
    }

    @Test
    fun contentDescriptionContains_InvalidText_withString() {
        val text = getResourceString(R.string.button_default_content_desc)
        AssertUtils.assertException {
            page.button.contentDescriptionContains(
                "${text.substring(2)} to be invalid",
                1000
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
            page.button.assertMatches(
                allOf(
                    isDisplayed(),
                    isEnabled(),
                    withText(R.string.button_text)
                ), 1000
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
        AssertUtils.assertException { page.webView.isJavascriptEnabled(100) }
    }
    //isSuccess

    @Test
    fun isSuccess_FalseTest() {
        val success = page.button.isSuccess { isNotDisplayed(100) }
        Assert.assertFalse(success)
    }

    @Test
    fun isSuccess_TrueTest() {
        val success = page.button.isSuccess { isDisplayed(100) }
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
        AssertUtils.assertException { page.emptyImageView.assertMatches(hasAnyDrawable(), 100) }
    }

    @Test
    fun withDrawable_correctDrawable() {
        page.imageView.assertMatches(withDrawable(R.drawable.ic_account))
    }

    @Test
    fun withDrawable_invalidDrawable() {
        AssertUtils.assertException {
            page.imageView.assertMatches(
                withDrawable(R.drawable.ic_attach_file),
                100
            )
        }
    }


}