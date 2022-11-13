package com.atiurin.sampleapp.tests.uiautomator

import com.atiurin.sampleapp.pages.UiObject2ElementsPage
import com.atiurin.sampleapp.pages.UiObjectElementsPage
import com.atiurin.sampleapp.tests.UiElementsTest
import org.junit.Test

class UiAutomatorCustomAssertionTest: UiElementsTest() {
    val uiObjectPage = UiObjectElementsPage()
    val uiObject2Page = UiObject2ElementsPage()

    @Test
    fun uiObjectValidAssertionTest(){
        uiObjectPage.button.click()
    }
}