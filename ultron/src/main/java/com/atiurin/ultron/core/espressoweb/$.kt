package com.atiurin.ultron.core.espressoweb
import androidx.test.espresso.web.model.Atom
import androidx.test.espresso.web.model.ElementReference
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.Locator

class `$` (locator: Locator, matcher: String){
    val atom = findElement(locator, matcher)
}

fun className(value: String): `$` {
    return `$`(Locator.CLASS_NAME, value)
}

fun cssSelector(value: String): `$` {
    return `$`(Locator.CSS_SELECTOR, value)
}

fun id(value: String): `$` {
    return  `$`(Locator.ID, value)
}

fun linkText(value: String): `$` {
    return `$`(Locator.LINK_TEXT, value)
}

fun name(value: String): `$` {
    return `$`(Locator.NAME, value)
}

fun partialLinkText(value: String): `$` {
    return `$`(Locator.PARTIAL_LINK_TEXT, value)
}

fun tagName(value: String): `$` {
    return `$`(Locator.TAG_NAME, value)
}

fun xpath(value: String): `$` {
    return `$`(Locator.XPATH, value)
}


