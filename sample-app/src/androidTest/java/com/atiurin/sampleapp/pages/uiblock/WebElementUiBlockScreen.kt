package com.atiurin.sampleapp.pages.uiblock

import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement.Companion.className
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement.Companion.id
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElementUiBlock
import com.atiurin.ultron.page.Screen

object WebElementUiBlockScreen : Screen<WebElementUiBlockScreen>() {
    val teacherBlock = WebBlock(id("teacher"))
    val studentBlock = WebBlock(id("student"))
    val persons = WebPersonsBlock(id("persons"))
}

class WebBlock(element: UltronWebElement): UltronWebElementUiBlock(element){
    val name = child(className("person_name"))
}

class WebPersonsBlock(element: UltronWebElement): UltronWebElementUiBlock(element){
    val teacher = child(WebBlock(id("teacher")))
    val student = child(id("student")){ modifiedElement ->
        WebBlock(modifiedElement)
    }
}