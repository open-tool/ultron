package com.atiurin.sampleapp.tests.espresso_web

import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.web.assertion.WebViewAssertions.webContent
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.matcher.DomMatchers.elementById
import androidx.test.espresso.web.matcher.DomMatchers.withTextContent
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms.*
import androidx.test.espresso.web.webdriver.Locator
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebDocument
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebDocument.Companion.evalJS
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement.Companion.className
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement.Companion.id
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement.Companion.linkText
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement.Companion.xpath
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElements
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElements.Companion.classNames
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.Matchers.`is`
import org.junit.Assert
import org.junit.Test

class EspressoWebUiElementsTest : BaseWebViewTest() {

    @Test
    fun simpleWebViewTest() {
        val newTitle = "New title"
        onWebView()
            .forceJavascriptEnabled()
            .withElement(findElement(Locator.ID, "text_input"))
            .perform(webKeys(newTitle))
            .withElement(findElement(Locator.ID, "button1"))
            .perform(webClick())
            .withElement(findElement(Locator.ID, "title"))
            .check(webMatches(getText(), containsString("New title")))
        //findMultipleElements
//        onWebView().perform(findMultipleElements(Locator.CLASS_NAME, "button"))
//            .get()
//            .forEach {
//                onWebView().withElement(it).perform(webClick())
//            }
    }

    @Test
    fun multipleElementsWebViewTest() {
        UltronWebElements(Locator.CLASS_NAME, "button").getElements().forEach {
            it.webClick()
        }
        UltronWebElements(Locator.CLASS_NAME, "link").getElements()
            .filter {
                it.isSuccess {
                    withTimeout(100).hasText("Apple")
                }
            }
            .forEach { it.webClick() }
        classNames("link").getElements()
            .filter {
                it.isSuccess {
                    withTimeout(100).hasText("Apple")
                }
            }
            .forEach { it.webClick() }
        page.title.containsText("apple")
    }

    @Test
    fun pageMultipleElements() {
        page.buttons.getElements().forEach {
            it.webClick()
        }
    }

    @Test
    fun extVar2WebViewTest() {
        val newTitle = "New title"
        id("text_input").webKeys(newTitle).webKeys("and more").replaceText(newTitle)
        id("button1").webClick()
        id("title").hasText(newTitle)
        className("css_title").containsText(newTitle)
        linkText("Apple").containsText("Apple")
    }

    @Test
    fun jsEvaluationTest() {
        val jsTitle = "JS_TITLE"
        val jsTitleNew = "JS_TITLE_NEW"
        evalJS("document.getElementById(\"title\").innerHTML = '$jsTitle';")
        className("css_title", withId(R.id.webview)).containsText(jsTitle)
//        onWebView().script("document.getElementById(\"title\").innerHTML = '$jsTitleNew';")
//        WebElement(Locator.CLASS_NAME,"css_title", withId(R.id.webview)).containsText(jsTitleNew)
    }

    @Test
    fun webViewFinderTest() {
        val jsTitleNew = "JS_TITLE_NEW"
        UltronConfig.Espresso.webViewMatcher = withId(R.id.webview)
        evalJS("document.getElementById(\"title\").innerHTML = '$jsTitleNew';")
        className("css_title").containsText(jsTitleNew)
    }

    @Test
    fun pageVar3WebViewTest() {
        val newTitle = "New title"
        page.textInput.webKeys(newTitle)
        page.buttonUpdTitle.webClick()
        page.title.containsText(newTitle)
        page.titleWithCss.containsText(newTitle)
        AssertUtils.assertException { page.appleLink.withTimeout(100).containsText("Apple12312") }
    }

    @Test
    fun getText2Test() {
        val newTitle = "New title"
        page.textInput.webKeys(newTitle)
        page.buttonUpdTitle.webClick()
        page.title.hasText(newTitle)
        val titleText = page.title.getText()
        Assert.assertEquals(newTitle, titleText)
    }

    @Test
    fun checkButtonTextTest() {
        xpath(".//*[@id='button3']").apply {
            exists()
            hasAttribute("value", "Set title active")
        }
    }

    @Test
    fun webInteractionLyambda() {
        Assert.assertTrue(id("button2").getText().isBlank())
        Assert.assertEquals("Apple", id("apple_link").getText())
    }

    @Test
    fun elementNotPresentDefaultTimeout() {
        AssertUtils.assertExecTimeBetween(5_000, 7_000) { id("asdasdasd").getText() }
    }

    @Test
    fun elementNotPresentCustomTimeout() {
        AssertUtils.assertExecTimeBetween(1_000, 3_000) { id("asdasdasd").withTimeout(2000).getText() }
    }

    @Test
    fun customWebViewAssertionTest() {
        UltronWebDocument.assertThat(webContent(elementById("apple_link", withTextContent("Apple"))))
        id("apple_link").hasAttribute("href", `is`("fake_link.html"))
    }

    @Test
    fun customWebAssertionTest() {
        id("apple_link").assertThat(webMatches(getText(), `is`("Apple")))
    }

    @Test
    fun contextualElement() {
        id("student")
            .withContextual(className("person_name"))
            .hasText("Plato")
    }
}