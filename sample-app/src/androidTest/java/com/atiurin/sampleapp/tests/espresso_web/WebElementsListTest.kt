package com.atiurin.sampleapp.tests.espresso_web

import androidx.test.espresso.web.webdriver.Locator
import com.atiurin.ultron.core.espressoweb.webelement.WebElementWithId
import com.atiurin.ultron.core.espressoweb.webelement.WebElementsList
import com.atiurin.ultron.core.espressoweb.webelement.WebElementsList.Companion.classNames
import com.atiurin.ultron.core.espressoweb.webelement.WebElementsList.Companion.ids
import org.junit.Assert
import org.junit.Test

class WebElementsListTest : BaseWebViewTest() {
    @Test
    fun getSizeText() {
        val buttonsAmount = WebElementsList(Locator.CLASS_NAME, "button").getSize()
        Assert.assertTrue(buttonsAmount == 3)
    }

    @Test
    fun getListElementText(){
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
    fun idsTest(){
        ids("list_element_1").getElements().forEach {
            (it as WebElementWithId).hasAttribute("desc", "some desc 1")
        }
    }
}