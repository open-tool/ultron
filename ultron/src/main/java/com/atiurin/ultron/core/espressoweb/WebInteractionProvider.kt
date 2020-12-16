package com.atiurin.ultron.core.espressoweb

import androidx.test.espresso.web.model.Atom
import androidx.test.espresso.web.model.ElementReference
import androidx.test.espresso.web.sugar.Web
import com.atiurin.ultron.core.config.UltronConfig

class WebInteractionProvider {
    companion object {
        internal fun getElementBlock(atom: Atom<ElementReference>): () -> Web.WebInteraction<Void> {
            return { UltronConfig.Espresso.webViewFinder().withElement(atom).withNoTimeout()}
        }

        internal fun getElementBlock(elementReference: ElementReference): () -> Web.WebInteraction<Void> {
            return { UltronConfig.Espresso.webViewFinder().withElement(elementReference).withNoTimeout()}
        }

        internal fun getWebViewBlock(): () -> Web.WebInteraction<Void> {
            return { UltronConfig.Espresso.webViewFinder().withNoTimeout()}
        }
    }
}