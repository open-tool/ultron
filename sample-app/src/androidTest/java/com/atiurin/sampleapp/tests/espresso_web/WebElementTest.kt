package com.atiurin.sampleapp.tests.espresso_web

import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.WebViewPage
import com.atiurin.ultron.core.espressoweb.webelement.WebElement.Companion.id
import org.junit.Test

class WebElementTest : BaseWebViewTest() {
    @Test
    fun webClick_onExistElement(){
        page.buttonSetTitle2.webClick()
        page.title.hasText(WebViewPage.BUTTON2_TITLE)
    }

    @Test
    fun webClick_onNotExistedElement(){
        AssertUtils.assertException {  id("notExistId").webClick(timeoutMs = 100) }
    }
}