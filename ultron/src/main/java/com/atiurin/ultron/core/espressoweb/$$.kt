package com.atiurin.ultron.core.espressoweb

import androidx.test.espresso.web.model.ElementReference
import androidx.test.espresso.web.webdriver.DriverAtoms.findMultipleElements
import androidx.test.espresso.web.webdriver.Locator
import com.atiurin.ultron.extensions.*

data class `$$` (val locator: Locator, val matcher: String){
    fun getElements(): List<ElementReference> {
        return WebInteractionProvider.getWebViewBlock().findMultipleElements(locator, matcher) ?: emptyList()
    }

    fun getSize(): Int {
        return getElements().size
    }

    companion object {
        fun classNames(value: String): `$$` {
            return `$$`(Locator.CLASS_NAME, value)
        }

        fun cssSelectors(value: String): `$$` {
            return `$$`(Locator.CSS_SELECTOR, value)
        }

        fun ids(value: String): `$$` {
            return  `$$`(Locator.ID, value)
        }

        fun linkTexts(value: String): `$$` {
            return `$$`(Locator.LINK_TEXT, value)
        }

        fun names(value: String): `$$` {
            return `$$`(Locator.NAME, value)
        }

        fun partialLinkTexts(value: String): `$$` {
            return `$$`(Locator.PARTIAL_LINK_TEXT, value)
        }

        fun tagNames(value: String): `$$` {
            return `$$`(Locator.TAG_NAME, value)
        }

        fun xpaths(value: String): `$$` {
            return `$$`(Locator.XPATH, value)
        }
    }
}