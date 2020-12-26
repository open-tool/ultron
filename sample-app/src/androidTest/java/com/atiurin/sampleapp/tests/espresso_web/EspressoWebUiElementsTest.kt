package com.atiurin.sampleapp.tests.espresso_web

import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.web.assertion.WebViewAssertions.webContent
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.matcher.DomMatchers.elementById
import androidx.test.espresso.web.matcher.DomMatchers.withTextContent
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.DriverAtoms.*
import androidx.test.espresso.web.webdriver.Locator
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.WebViewPage
import com.atiurin.sampleapp.tests.UiElementsTest
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espressoweb.webelement.WebDocument
import com.atiurin.ultron.core.espressoweb.webelement.WebDocument.Companion.evalJS
import com.atiurin.ultron.core.espressoweb.webelement.WebElement
import com.atiurin.ultron.core.espressoweb.webelement.WebElement.Companion.className
import com.atiurin.ultron.core.espressoweb.webelement.WebElement.Companion.id
import com.atiurin.ultron.core.espressoweb.webelement.WebElement.Companion.linkText
import com.atiurin.ultron.core.espressoweb.webelement.WebElement.Companion.xpath
import com.atiurin.ultron.core.espressoweb.webelement.WebElementsList
import com.atiurin.ultron.core.espressoweb.webelement.WebElementsList.Companion.classNames
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.Matchers.`is`
import org.junit.Assert
import org.junit.Test

class EspressoWebUiElementsTest : UiElementsTest() {
    val page = WebViewPage()

    @Test
    fun simpleWebViewTest() {
        val newTitle = "New title"
        onWebView().withElement(findElement(Locator.ID, "text_input"))
            .perform(webKeys(newTitle))
        onWebView().withElement(findElement(Locator.ID, "button1"))
            .perform(webClick())
        onWebView().withElement(findElement(Locator.ID, "title"))
            .check(webMatches(DriverAtoms.getText(), containsString("New title")))
        onWebView().perform(findElement(Locator.ID, "title")).get()
        //findMultipleElements
        onWebView().perform(findMultipleElements(Locator.CLASS_NAME, "button"))
            .get()
            .forEach {
                onWebView().withElement(it).perform(webClick())
            }
    }

    @Test
    fun multipleElementsWebViewTest() {
        WebElementsList(Locator.CLASS_NAME, "button").getElements().forEach {
            it.webClick()
        }
        WebElementsList(Locator.CLASS_NAME, "link").getElements()
            .filter {
                it.isSuccess {
                    hasText("Apple", 1000)
                }
            }
            .forEach { it.webClick() }
        classNames("link").getElements()
            .filter {
                it.isSuccess {
                    hasText("Apple", 1000)
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

    //
    @Test
    fun extWebViewTest() {
        val newTitle = "New title"
        WebElement(Locator.ID, "text_input").webKeys(newTitle)
        WebElement(Locator.ID, "button1").webClick()
        id("title").webClick().containsText(newTitle)
    }

    @Test
    fun extVar2WebViewTest() {
        val newTitle = "New title"
        id("text_input").webKeys(newTitle).webKeys("and more").replaceText(newTitle)
        Thread.sleep(5000)
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
        page.button.webClick()
        page.title.containsText(newTitle)
        page.titleWithCss.containsText(newTitle)
        AssertUtils.assertException { page.appleLink.containsText("Apple12312") }
    }

    @Test
    fun getText2Test() {
        val newTitle = "New title"
        page.textInput.webKeys(newTitle)
        page.button.webClick()
        page.title.hasText(newTitle)
        val titleText = page.title.getText()
        Assert.assertEquals(newTitle, titleText)
    }

    @Test
    fun checkButtonTextTest() {
        val buttonText = "Button3"
        xpath("//form/input[@value='$buttonText']").exists()
    }

    @Test
    fun webInteractionLyambda() {
        Assert.assertTrue(id("button2").getText().isBlank())
        Assert.assertEquals("Apple", id("apple_link").getText())
    }

    @Test
    fun elementNotPresentDefaultTimeout() {
        AssertUtils.assertExecTimeMoreThen(5_000) { id("asdasdasd").getText() }
        AssertUtils.assertExecTimeLessThen(7_000) { id("asdasdasd").getText() }
    }

    @Test
    fun elementNotPresentCustomTimeout() {
        AssertUtils.assertExecTimeMoreThen(1_000) { id("asdasdasd").getText(2000) }
        AssertUtils.assertExecTimeLessThen(3_000) { id("asdasdasd").getText(2000) }
    }

    @Test
    fun customWebViewAssertionTest() {
        WebDocument.assertThat(webContent(elementById("apple_link", withTextContent("Apple"))))
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