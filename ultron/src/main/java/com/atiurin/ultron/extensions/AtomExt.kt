package com.atiurin.ultron.extensions

import androidx.test.espresso.web.model.Atom
import androidx.test.espresso.web.model.ElementReference
import com.atiurin.ultron.core.config.UltronConfig.Espresso.Companion.webViewFinder
import com.atiurin.ultron.core.espressoweb.`$`


fun `$`.webClick(){
    webViewFinder().withElement(this.atom).withNoTimeout().webClick()
}

fun `$`.webKeys(text: String){
    { webViewFinder().withElement(this.atom).withNoTimeout()}.webKeys(text)
}

fun `$`.containsText(text: String){
    { webViewFinder().withElement(this.atom).withNoTimeout()}.containsText(text)
}

fun Atom<ElementReference>.webClick(){
    { webViewFinder().withElement(this).withNoTimeout() }.webClick()
}

fun Atom<ElementReference>.webKeys(text: String){
    { webViewFinder().withElement(this).withNoTimeout()}.webKeys(text)
}

fun script(script: String){
    {webViewFinder().withNoTimeout()}.script(script)
}

fun Atom<ElementReference>.containsText(text: String){
    {webViewFinder().withElement(this).withNoTimeout()}.containsText(text)
}

fun Atom<ElementReference>.hasText(text: String){
    {webViewFinder().withElement(this).withNoTimeout()}.hasText(text)
}

fun Atom<ElementReference>.getText(): String? {
    return {webViewFinder().withElement(this)}.getText()
}