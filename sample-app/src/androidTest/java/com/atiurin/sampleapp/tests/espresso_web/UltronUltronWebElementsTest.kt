package com.atiurin.sampleapp.tests.espresso_web

import com.atiurin.sampleapp.framework.Log
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElements.Companion.classNames
import org.junit.Assert
import org.junit.Test

class UltronUltronWebElementsTest : BaseWebViewTest() {
    @Test
    fun getSizeTest() {
        val buttonsAmount = classNames("button").getSize()
        Assert.assertTrue(buttonsAmount == 3)
    }

    @Test
    fun getSize_notExistedElement() {
        Log.debug(">>>" + classNames("not_existed_classname").getSize())
//        AssertUtils.assertException {  }
    }

    @Test
    fun getListElementTest(){
        classNames("link").getElements()
            .find { ultronWebElement ->
                ultronWebElement.isSuccess {
                    withTimeout(100).hasText("Apple")
                }
            }?.webClick()
        page.title.containsText("apple")
    }
}