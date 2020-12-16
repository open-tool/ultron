package com.atiurin.ultron.extensions

import androidx.test.espresso.web.model.Atom
import androidx.test.espresso.web.model.ElementReference
import com.atiurin.ultron.core.config.UltronConfig.Espresso.Companion.webViewFinder
import com.atiurin.ultron.core.espressoweb.WebInteractionProvider

fun Atom<ElementReference>.webClick(){
    WebInteractionProvider.getElementBlock(this).webClick()
}

fun Atom<ElementReference>.webKeys(text: String){
    WebInteractionProvider.getElementBlock(this).webKeys(text)
}

fun Atom<ElementReference>.containsText(text: String){
    WebInteractionProvider.getElementBlock(this).containsText(text)
}

fun Atom<ElementReference>.hasText(text: String){
    WebInteractionProvider.getElementBlock(this).hasText(text)
}

fun Atom<ElementReference>.getText(): String? {
    return WebInteractionProvider.getElementBlock(this).getText()
}

fun ElementReference.webClick(){
    WebInteractionProvider.getElementBlock(this).webClick()
}

fun ElementReference.webKeys(text: String){
    WebInteractionProvider.getElementBlock(this).webKeys(text)
}

fun ElementReference.containsText(text: String){
    WebInteractionProvider.getElementBlock(this).containsText(text)
}

fun ElementReference.hasText(text: String){
    WebInteractionProvider.getElementBlock(this).hasText(text)
}

fun ElementReference.getText(): String? {
    return WebInteractionProvider.getElementBlock(this).getText()
}

fun script(script: String){
    WebInteractionProvider.getWebViewBlock().script(script)
}

