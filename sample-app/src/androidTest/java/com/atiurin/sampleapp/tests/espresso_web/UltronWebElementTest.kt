package com.atiurin.sampleapp.tests.espresso_web

import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.webdriver.DriverAtoms.getText
import com.atiurin.sampleapp.framework.DummyMetaObject
import com.atiurin.sampleapp.framework.ultronext.appendText
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.WebViewPage
import com.atiurin.sampleapp.pages.uiblock.WebElementUiBlockScreen
import com.atiurin.ultron.core.common.assertion.softAssertion
import com.atiurin.ultron.core.common.assertion.verifySoftAssertions
import com.atiurin.ultron.core.config.UltronCommonConfig
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement.Companion.className
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement.Companion.id
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement.Companion.xpath
import org.hamcrest.Matchers.`is`
import org.junit.Assert
import org.junit.Test

class UltronWebElementTest : BaseWebViewTest() {
    @Test
    fun webClick_onExistElement() {
        page.buttonSetTitle2.webClick()
        page.title.hasText(WebViewPage.BUTTON2_TITLE)
    }

    @Test
    fun webClick_onNotExistedElement() {
        AssertUtils.assertException { xpath("notExistId").withTimeout(100).withName("Custome name").webClick() }
    }

    @Test
    fun webClick_isSuccessTrueValue() {
        val success = page.buttonSetTitle2.isSuccess { webClick() }
        Assert.assertTrue(success)
    }

    @Test
    fun webClick_isSuccessFalseValue() {
        val success = id("notExistId").isSuccess { withTimeout(100).webClick() }
        Assert.assertFalse(success)
    }

    @Test
    fun exists_onExistElement() {
        page.buttonUpdTitle.exists()
    }

    @Test
    fun exists_onNotExistedElement() {
        AssertUtils.assertException { id("notExistId").withTimeout(100).exists() }
    }

    @Test
    fun exists_isSuccessTrueValue() {
        val success = page.buttonSetTitle2.isSuccess { exists() }
        Assert.assertTrue(success)
    }

    @Test
    fun exists_isSuccessFalseValue() {
        val success = id("notExistId").isSuccess { withTimeout(100).exists() }
        Assert.assertFalse(success)
    }

    @Test
    fun clearElement_onExistedEditableElement() {
        page.textInput.webKeys("initial text").clearElement().hasText("")
    }

    @Test
    fun clearElement_onExistedNotEditableElement() {
        AssertUtils.assertException { page.buttonUpdTitle.withTimeout(100).clearElement() }
    }

    @Test
    fun clearElement_onNotExistedElement() {
        AssertUtils.assertException { id("notExistId").withTimeout(100).clearElement() }
    }

    @Test
    fun getText_onExistedTextContainerElement() {
        val text = "some text 2"
        page.textInput.replaceText(text)
        page.buttonUpdTitle.webClick()
        val receivedText = page.title.getText()
        Assert.assertEquals(text, receivedText)
    }

    @Test
    fun getText_onNotExistedElement() {
        AssertUtils.assertException { id("notExistId").withTimeout(100).getText() }
    }

    @Test
    fun webKeys_onExistedTextContainerElement() {
        val text = "some text 3"
        page.textInput.clearElement().webKeys(text)
        page.buttonUpdTitle.webClick()
        page.title.hasText(text)
    }

    @Test
    fun webKeys_onNotExistedElement() {
        AssertUtils.assertException { id("notExistId").withTimeout(100).webKeys("asd") }
    }

    @Test
    fun replaceText_and_hasText_onExistedElement() {
        val text = "some text 3"
        page.textInput.replaceText(text)
        page.buttonUpdTitle.webClick()
        page.title.hasText(text)
    }

    @Test
    fun replaceText_onNotExistedElement() {
        AssertUtils.assertException { id("notExistId").withTimeout(100).replaceText("asd") }
    }

    @Test
    fun hasText_onNotExistedElement() {
        AssertUtils.assertException { id("notExistId").withTimeout(100).hasText("asd") }
    }

    @Test
    fun containsText_onExistedElement() {
        val text = "some text 3"
        page.textInput.replaceText(text + "additional")
        page.buttonUpdTitle.webClick()
        page.title.containsText(text)
    }

    @Test
    fun containsText_onNotExistedElement() {
        AssertUtils.assertException { id("notExistId").withTimeout(100).containsText("asd") }
    }

    @Test
    fun hasAttribute_withMatcher_onExistedElement() {
        page.appleLink.hasAttribute("href", `is`(WebViewPage.APPLE_LINK_HREF))
    }

    @Test
    fun hasAttribute_withString_onExistedElement() {
        page.appleLink.hasAttribute("href", WebViewPage.APPLE_LINK_HREF)
    }

    @Test
    fun hasAttribute_invalidMatcher_onExistedElement() {
        AssertUtils.assertException {
            page.appleLink.withTimeout(100).hasAttribute("href", `is`("SomeInvalidValue"))
        }
    }

    @Test
    fun hasAttribute_onNotExistedElement() {
        AssertUtils.assertException { id("notExistId").withTimeout(100).hasAttribute("asd", `is`("asdasd")) }
    }

    @Test
    fun assertThat_onExistedElement() {
        page.appleLink.assertThat(webMatches(getText(), `is`("Apple")))
    }

    @Test
    fun assertThat_onNotExistedElement() {
        AssertUtils.assertException {
            id("notExistId").withTimeout(100).assertThat(
                webMatches(
                    getText(), `is`("Apple")
                )
            )
        }
    }

    @Test
    fun withContextual_hasText() {
        id("teacher").containsText("Teachers").withContextual(className("person_name")).hasText("Socrates")
    }

    @Test
    fun withContextual_containsText() {
        id("student").withContextual(className("person_name")).hasText("Plato")
    }

    @Test
    fun webUiBlock(){
        WebElementUiBlockScreen {
            teacherBlock.name.exists().hasText("Socrates")
            teacherBlock.blockElement
            persons.student.name.hasText("Plato")
        }
    }

    @Test
    fun webUiBlocksListTest(){

    }

    @Test
    fun scrollToWebElement() {
        id("list_element_12").webScrollIntoView().hasText("list_element_12").webClick()
    }

    @Test
    fun webScrollIntoViewBoolean() {
        val result = id("list_element_12").webScrollIntoViewBoolean()
        Assert.assertTrue(result)
    }

    @Test
    fun customActionTest() {
        val initText = "start"
        val finishText = "finish"
        page.textInput
            .replaceText(initText)
            .appendText(finishText)
            .exists()
        page.buttonUpdTitle.webClick()
        page.title.hasText(initText+finishText)
    }


    @Test
    fun validAssertion(){
        val text = "start"
        page.textInput.replaceText(text)
        page.buttonUpdTitle.withAssertion {
            page.title.hasText(text)
        }.webClick()
    }

    @Test
    fun invalidAssertion(){
        val text = "start"
        AssertUtils.assertException {
            page.textInput.replaceText(text)
            page.buttonUpdTitle.withTimeout(3000).withAssertion {
                page.title.withTimeout(500).hasText(text + "adas")
            }.webClick()
        }
    }


    @Test
    fun withName_inOperationProps_ultronInteraction() {
        val name = "ElementName"
        page.notExistedElement.withTimeout(100).withName(name).withResultHandler { result ->
            Assert.assertEquals(name, result.operation.elementInfo.name)
        }.exists()
    }

    @Test
    fun withName_inOperationProps_matcherExt() {
        val name = "ElementName"
        page.notExistedElement.withName(name).withTimeout(100).withResultHandler { result ->
            Assert.assertEquals(name, result.operation.elementInfo.name)
        }.exists()
    }

    @Test
    fun withName_inExceptionMessage() {
        val name = "ElementNameToBeInException"
        runCatching {
            page.notExistedElement.withTimeout(100).withName(name).exists()
        }.onFailure { exception ->
            Assert.assertTrue(exception.message!!.contains(name))
        }
    }

    @Test
    fun withMeta() {
        val meta = DummyMetaObject("ElementMetaInfo")
        page.notExistedElement.withTimeout(100).withMetaInfo(meta).withResultHandler { result ->
            Assert.assertEquals(meta, result.operation.elementInfo.meta)
        }.exists()
    }

    @Test
    fun verifySoftAssertionsTest() {
        UltronCommonConfig.testContext.softAnalyzer.clear()
        softAssertion(false) {
            id("NotExistText").withTimeout(100).webClick()
            id("NotExistTestTag").withTimeout(100).webClick()
        }
        runCatching {
            verifySoftAssertions()
        }.onFailure { exception ->
            val message = exception.message ?: throw RuntimeException("Empty exception message: $exception")
            Assert.assertTrue(message.contains("NotExistText"))
            Assert.assertTrue(message.contains("NotExistTestTag"))
        }
    }

    @Test
    fun softAssertionTest() {
        UltronCommonConfig.testContext.softAnalyzer.clear()
        AssertUtils.assertException {
            softAssertion {
                page.notExistedElement.withTimeout(100).webClick()
            }
        }
    }
}