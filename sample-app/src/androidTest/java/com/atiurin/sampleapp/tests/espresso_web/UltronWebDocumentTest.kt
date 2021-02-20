package com.atiurin.sampleapp.tests.espresso_web

import androidx.test.espresso.web.assertion.WebViewAssertions.webContent
import androidx.test.espresso.web.matcher.DomMatchers.elementById
import androidx.test.espresso.web.matcher.DomMatchers.withTextContent
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebDocument
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebDocument.Companion.evalJS
import org.junit.Assert
import org.junit.Test

class UltronWebDocumentTest : BaseWebViewTest() {
    @Test
    fun forceJS_Test() {
        UltronWebDocument.forceJavascriptEnabled()
    }

    @Test
    fun evalJS_Test() {
        val title = "SOME NEW TITLE"
        evalJS("document.getElementById(\"title\").innerHTML = '$title';")
        page.title.hasText(title)
    }

    @Test
    fun assertThat_validValueTest() {
        UltronWebDocument.assertThat(
            webContent(
                elementById(
                    "apple_link",
                    withTextContent("Apple")
                )
            )
        )
    }

    @Test
    fun assertThat_invalidValueTest() {
        AssertUtils.assertException {
            UltronWebDocument.assertThat(
                webContent(
                    elementById(
                        "apple_link",
                        withTextContent("Apple1123123")
                    )
                ),
                timeoutMs = 100
            )
        }
    }

    @Test
    fun setActiveElement(){
        page.buttonSetTitleActive.webClick()
        val elementRef = UltronWebDocument.selectActiveElement()
        Assert.assertNotNull(elementRef)
    }

    @Test
    fun selectFrame_validElement(){
        val frame = UltronWebDocument.selectFrameByIdOrName("iframe")
        Assert.assertNotNull(frame)
    }

    @Test
    fun selectFrame_invalidNameOrId(){
        AssertUtils.assertException { UltronWebDocument.selectFrameByIdOrName("asdhgasdlkjasdasd", timeoutMs = 100) }
    }
}