package com.atiurin.ultron.core.espressoweb
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.Locator
import com.atiurin.ultron.extensions.containsText
import com.atiurin.ultron.extensions.webClick
import com.atiurin.ultron.extensions.webKeys

data class `$` (val locator: Locator, val matcher: String){
    val atom = findElement(locator, matcher)

    fun webClick(){
        WebInteractionProvider.getElementBlock(this.atom).webClick()
    }

    fun webKeys(text: String){
        WebInteractionProvider.getElementBlock(this.atom).webKeys(text)
    }

    fun containsText(text: String){
        WebInteractionProvider.getElementBlock(this.atom).containsText(text)
    }

    companion object {
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
    }
}




