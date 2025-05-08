package com.atiurin.ultron.core.compose

import com.atiurin.ultron.core.compose.page.ComposeUiBlock

inline fun <reified T : ComposeUiBlock> ComposeUiBlock.childBlock(
    uiBlock: T,
    deepSearch: Boolean = true
): T {
    val newMatcher = when (deepSearch) {
        true -> _deepSearch(uiBlock.blockMatcher)
        false -> _parentSearch(uiBlock.blockMatcher)
    }

    return T::class.constructors.first { it.parameters.size == 1 }.call(newMatcher)
}