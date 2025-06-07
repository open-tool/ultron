package com.atiurin.sampleapp.pages.uiblock

import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement.Companion.className
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement.Companion.id
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElementUiBlock
import com.atiurin.ultron.page.Screen

object WebElementUiBlockScreen : Screen<WebElementUiBlockScreen>() {
    val teacherBlock = WebBlock(id("teacher"), "Teacher block")
    val studentWithoutDesc = WebBlockWithoutDesc(id("student"))
    val persons = WebPersonsBlock(id("persons"), "persons block")
}

class WebBlock(blockElement: UltronWebElement, blockDescription: String): UltronWebElementUiBlock(blockElement, blockDescription){
    val name = child(className("person_name")).withName("Name of $blockDescription")
}

class WebBlockWithoutDesc(blockElement: UltronWebElement): UltronWebElementUiBlock(blockElement){
    val name = child(className("person_name"))
}

class WebPersonsBlock(blockElement: UltronWebElement, blockDescription: String): UltronWebElementUiBlock(blockElement, blockDescription){
    val teacher = child(WebBlock(id("teacher"), "Teacher child of $blockDescription"))
    val studentWithoutDesc = child(WebBlockWithoutDesc(id("student")))
    val student = child(id("student")){ modifiedElement ->
        WebBlock(modifiedElement, "student child of $blockDescription")
    }
}