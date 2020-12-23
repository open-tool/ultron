package com.atiurin.sampleapp.pages

import com.atiurin.ultron.core.espressoweb.webelement.WebElementsList.Companion.classNames
import com.atiurin.ultron.core.espressoweb.webelement.WebElement.Companion.className
import com.atiurin.ultron.core.espressoweb.webelement.WebElement.Companion.id
import com.atiurin.ultron.core.espressoweb.webelement.WebElement.Companion.linkText
import com.atiurin.ultron.page.Page

class WebViewPage : Page<WebViewPage>() {
    val textInput = id("text_input")
    val button = id("button1")
    val title = id("title")
    val titleWithCss = className("css_title")
    val appleLink = linkText("Apple")
    val buttons = classNames("button")
}