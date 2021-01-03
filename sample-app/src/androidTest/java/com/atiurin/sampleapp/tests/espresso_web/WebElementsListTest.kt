package com.atiurin.sampleapp.tests.espresso_web

import com.atiurin.sampleapp.framework.Log
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.ultron.core.espressoweb.webelement.WebElementsList.Companion.classNames
import org.junit.Assert
import org.junit.Test

class WebElementsListTest : BaseWebViewTest() {
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
            .filter {
                it.isSuccess {
                    hasText("Apple", 1000)
                }
            }
            .forEach { it.webClick() }
        page.title.containsText("apple")
    }
}