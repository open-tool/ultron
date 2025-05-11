package com.atiurin.ultron.core.compose

import androidx.compose.ui.test.SemanticsMatcher
import com.atiurin.ultron.core.compose.page.ComposeUiBlock
import com.atiurin.ultron.exceptions.UltronException
import kotlin.reflect.typeOf

inline fun <reified T : ComposeUiBlock> ComposeUiBlock.child(
    uiBlock: T,
    descendantSearch: Boolean = true
): T {
    val newMatcher = when (descendantSearch) {
        true -> _descendantSearch(uiBlock.blockMatcher)
        false -> _parentSearch(uiBlock.blockMatcher)
    }

    val constructor = T::class.constructors.firstOrNull {
        it.parameters.size == 1 && it.parameters.first().type == typeOf<SemanticsMatcher>()
    } ?: throw UltronException("""
            |UiBlock subclass ${T::class.simpleName} doesn't has a constructor with single Matcher<View> argument. 
            |Try following code: 
            |class ${T::class.simpleName}(parent: SemanticsMatcher) : ComposeUiBlock(parent) {
        """.trimMargin()
    )

    return constructor.call(newMatcher)
}