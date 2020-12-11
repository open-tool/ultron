package com.atiurin.sampleapp.tests.espresso_web

import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.DriverAtoms.webClick
import androidx.test.espresso.web.webdriver.Locator
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.WebViewPage
import com.atiurin.sampleapp.tests.UiElementsTest
import com.atiurin.ultron.core.espressoweb.*
import com.atiurin.ultron.extensions.*
import org.hamcrest.CoreMatchers.containsString
import org.junit.Test
import java.lang.AssertionError

class EspressoWebUiElementsTest: UiElementsTest() {
    val page = WebViewPage()
    @Test
    fun simpleWebViewTest(){
        onWebView().withElement(findElement(Locator.ID, "button1"))
            .perform(webClick())
        onWebView().withElement(findElement(Locator.ID, "title"))
            .check(webMatches(DriverAtoms.getText(), containsString("New title")))
    }

    @Test
    fun extWebViewTest(){
        `$`(Locator.ID, "button1").webClick()
        `$`(Locator.ID, "title").containsText("New title")
    }

    @Test
    fun extVar2WebViewTest(){
        val newTitle = "New title"
        id("text_input").webKeys(newTitle)
        id("button1").webClick()
        id("title").containsText(newTitle)
        className("css_title").containsText(newTitle)
        linkText("Apple").containsText("Apple")
    }

    @Test
    fun jsEvaluationTest(){
        val jsTitle = "JS_TITLE"
        id("button1").evalJS("document.getElementById(\"title\").innerHTML = '$jsTitle';")
        className("css_title").containsText(jsTitle)
    }

    @Test
    fun pageVar3WebViewTest(){
        val newTitle = "New title"
        page.textInput.webKeys(newTitle)
        page.button.webClick()
        page.title.containsText(newTitle)
        page.titleWithCss.containsText(newTitle)
        AssertUtils.assertException { page.appleLink.containsText("Apple12312") }
    }
}