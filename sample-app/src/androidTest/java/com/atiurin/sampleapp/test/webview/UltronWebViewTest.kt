package com.atiurin.sampleapp.test.webview

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.atiurin.sampleapp.activity.WebViewActivity
import com.atiurin.sampleapp.test.base.UltronBaseTest
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebDocument.Companion.evalJS
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement.Companion.id
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement.Companion.xpath
import com.atiurin.ultron.page.Screen
import org.junit.Rule
import org.junit.Test

class UltronWebViewTest : UltronBaseTest() {
    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(WebViewActivity::class.java)

    @Test
    fun interactWithWebViewTest() {
        UltronWebViewScreen {
            textInput.replaceText("Some new text")
            title.hasText("WebView title")
        }
    }

    @Test
    fun evalJSTest() {
        val newTitle = "New title from JS"
        evalJS(titleChangeScript(newTitle))
        UltronWebViewScreen.title.hasText(newTitle)
    }
}

object UltronWebViewScreen : Screen<UltronWebViewScreen>() {
    val textInput = id("text_input")
    val title = xpath("//h1[@id='title']")
}


fun titleChangeScript(title: String) = """
        function updateTitleFromJS() {
            var xpathResult = document.evaluate("//h1[@id='title']", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null);
            var titleElement = xpathResult.singleNodeValue;

            if (titleElement) {
                titleElement.innerHTML = '$title';
            } else {
                console.error("Element with id 'title' not found");
            }
        }
""".trimIndent()