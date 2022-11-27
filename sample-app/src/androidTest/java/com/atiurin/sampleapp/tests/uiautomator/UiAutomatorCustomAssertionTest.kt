package com.atiurin.sampleapp.tests.uiautomator

import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.framework.utils.TestDataUtils
import com.atiurin.sampleapp.pages.UiObject2ElementsPage
import com.atiurin.sampleapp.pages.UiObjectElementsPage
import com.atiurin.sampleapp.tests.UiElementsTest
import com.atiurin.ultron.core.uiautomator.uiobject.UltronUiObject.Companion.uiResId
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2.Companion.byResId
import org.junit.Test

class UiAutomatorCustomAssertionTest : UiElementsTest() {
    val uiObjectPage = UiObjectElementsPage()
    val uiObject2Page = UiObject2ElementsPage()

    @Test
    fun uiObjectValidAssertionTest() {
        uiObjectPage.button.withAssertion {
            uiObjectPage.eventStatus.textContains(TestDataUtils.getResourceString(R.string.button_event_click))
        }.click()
    }

    @Test
    fun uiObjectInvalidAssertionTest() {
        AssertUtils.assertException {
            uiObjectPage.button.withTimeout(1000).withAssertion {
                uiObjectPage.eventStatus.withTimeout(400).textContains("some invalid text")
            }.click()
        }
    }

    @Test
    fun uiObject2ValidAssertionTest() {
        uiObject2Page.button.withAssertion {
            uiObject2Page.eventStatus.textContains(TestDataUtils.getResourceString(R.string.button_event_click))
        }.click()
    }

    @Test
    fun uiObject2InvalidAssertionTest() {
        AssertUtils.assertException {
            uiObject2Page.button.withTimeout(1000).withAssertion {
                uiObject2Page.eventStatus.withTimeout(400).textContains("some invalid text")
            }.click()
        }
    }
}