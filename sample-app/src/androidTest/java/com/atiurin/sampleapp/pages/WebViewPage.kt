package com.atiurin.sampleapp.pages

import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElements.Companion.classNames
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement.Companion.className
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement.Companion.id
import com.atiurin.ultron.page.Page

class WebViewPage : Page<WebViewPage>() {
    companion object{
        const val BUTTON2_TITLE = "button2 clicked"
        const val APPLE_LINK_TEXT = "Apple"
        const val APPLE_LINK_HREF = "fake_link.html"

    }
    val textInput = id("text_input")
    val buttonUpdTitle = id("button1")
    val buttonSetTitle2 = id("button2")
    val buttonSetTitleActive = id("button3")
    val title = id("title")
    val titleWithCss = className("css_title")
    val appleLink = id("apple_link")
    val buttons = classNames("button")
}
