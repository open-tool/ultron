package com.atiurin.sampleapp.tests.uiautomator

import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.framework.utils.TestDataUtils
import com.atiurin.sampleapp.pages.BySelectorUiElementsPage
import com.atiurin.sampleapp.tests.UiElementsTest
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.extensions.containsText
import com.atiurin.ultron.extensions.hasText
import org.junit.Test

class UiElementsUiAutomatorTest: UiElementsTest() {
    val page = BySelectorUiElementsPage()

    @Test
    fun click_onClickable() {
        page.button.click()
        page.eventStatus.containsText(TestDataUtils.getResourceString(R.string.button_event_click))
    }

    @Test
    fun hasText_CorrectText_withResourceId() {
        page.editTextContentDesc.hasText("Default content description")
    }
}