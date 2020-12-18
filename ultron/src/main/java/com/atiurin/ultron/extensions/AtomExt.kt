package com.atiurin.ultron.extensions

import androidx.test.espresso.web.model.Atom
import androidx.test.espresso.web.model.ElementReference
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.DriverAtoms.FindElementTransformingAtom
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.config.UltronConfig.Espresso.Companion.webViewFinder
import com.atiurin.ultron.core.espressoweb.WebInteractionProvider

fun Atom<ElementReference>.getText(timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT): String {
    return WebInteractionProvider.getElementBlock(this).getText(timeoutMs)
}

fun Atom<ElementReference>.webClick(timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT) {
    WebInteractionProvider.getElementBlock(this).webClick(timeoutMs)
}

fun Atom<ElementReference>.webKeys(
    text: String,
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT
) {
    WebInteractionProvider.getElementBlock(this).webKeys(text, timeoutMs)
}

fun Atom<ElementReference>.containsText(
    text: String,
    timeoutMs: Long = UltronConfig.Espresso.ASSERTION_TIMEOUT
) {
    WebInteractionProvider.getElementBlock(this).containsText(text, timeoutMs)
}

fun Atom<ElementReference>.hasText(
    text: String,
    timeoutMs: Long = UltronConfig.Espresso.ASSERTION_TIMEOUT
) {
    WebInteractionProvider.getElementBlock(this).hasText(text, timeoutMs)
}

fun ElementReference.getText(timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT): String {
    return WebInteractionProvider.getElementBlock(this).getText(timeoutMs)
}

fun ElementReference.webClick(timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT) {
    WebInteractionProvider.getElementBlock(this).webClick(timeoutMs)
}

fun ElementReference.webKeys(text: String, timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT) {
    WebInteractionProvider.getElementBlock(this).webKeys(text, timeoutMs)
}

fun ElementReference.containsText(
    text: String,
    timeoutMs: Long = UltronConfig.Espresso.ASSERTION_TIMEOUT
) {
    WebInteractionProvider.getElementBlock(this).containsText(text, timeoutMs)
}

fun ElementReference.hasText(
    text: String,
    timeoutMs: Long = UltronConfig.Espresso.ASSERTION_TIMEOUT
) {
    WebInteractionProvider.getElementBlock(this).hasText(text, timeoutMs)
}

fun ElementReference.isSuccess(
    block: ElementReference.() -> Unit
): Boolean {
    var success = true
    try {
        block()
    } catch (th: Throwable) {
        success = false
    }
    return success
}

fun script(script: String, timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT) {
    WebInteractionProvider.getWebViewBlock().script(script, timeoutMs)
}

