package com.atiurin.ultron.extensions

import androidx.test.espresso.web.model.Atom
import androidx.test.espresso.web.model.ElementReference
import androidx.test.espresso.web.sugar.Web.onWebView
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.config.UltronConfig.Espresso.Companion.webViewFinder
import com.atiurin.ultron.core.espressoweb.`$`


fun `$`.webClick(){
    webViewFinder().withElement(this.atom).withNoTimeout().webClick()
}

fun `$`.containsText(text: String){
    webViewFinder().withElement(this.atom).withNoTimeout().containsText(text)
}

fun Atom<ElementReference>.webClick(){
    webViewFinder().withElement(this).withNoTimeout().webClick()
}

fun Atom<ElementReference>.webKeys(text: String){
    webViewFinder().withElement(this).withNoTimeout().webKeys(text)
}

fun evalJS(script: String){
    webViewFinder().withNoTimeout().evalJS(script)
}

fun Atom<ElementReference>.containsText(text: String){
    webViewFinder().withElement(this).withNoTimeout().containsText(text)
}