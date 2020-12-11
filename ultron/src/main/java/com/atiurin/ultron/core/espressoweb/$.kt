package com.atiurin.ultron.core.espressoweb
import androidx.test.espresso.web.model.Atom
import androidx.test.espresso.web.model.ElementReference
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.Locator

class `$` (locator: Locator, matcher: String){
    val atom = findElement(locator, matcher)
}

fun className(value: String): Atom<ElementReference> {
    return findElement(Locator.CLASS_NAME, value)
}

fun cssSelector(value: String): Atom<ElementReference> {
    return findElement(Locator.CSS_SELECTOR, value)
}

fun id(value: String): Atom<ElementReference> {
    return  findElement(Locator.ID, value)
}

fun linkText(value: String): Atom<ElementReference> {
    return findElement(Locator.LINK_TEXT, value)
}

fun name(value: String): Atom<ElementReference> {
    return findElement(Locator.NAME, value)
}

fun partialLinkText(value: String): Atom<ElementReference> {
    return findElement(Locator.PARTIAL_LINK_TEXT, value)
}

fun tagName(value: String): Atom<ElementReference> {
    return findElement(Locator.TAG_NAME, value)
}

fun xpath(value: String): Atom<ElementReference> {
    return findElement(Locator.XPATH, value)
}
