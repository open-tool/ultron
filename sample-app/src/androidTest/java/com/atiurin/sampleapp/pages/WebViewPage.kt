package com.atiurin.sampleapp.pages

import com.atiurin.ultron.core.espressoweb.webelement.WebElementsList.Companion.classNames
import com.atiurin.ultron.core.espressoweb.webelement.WebElement.Companion.className
import com.atiurin.ultron.core.espressoweb.webelement.WebElement.Companion.id
import com.atiurin.ultron.core.espressoweb.webelement.WebElement.Companion.linkText
import com.atiurin.ultron.page.Page

class WebViewPage : Page<WebViewPage>() {
    companion object{
        const val BUTTON2_TITLE = "button2 clicked"
        const val BUTTON3_TITLE = "button3 clicked"
    }
    val textInput = id("text_input")
    val buttonUpdTitle = id("button1")
    val buttonSetTitle2 = id("button2")
    val buttonSetTitle3 = id("button3")
    val title = id("title")
    val titleWithCss = className("css_title")
    val appleLink = linkText("Apple")
    val buttons = classNames("button")
}