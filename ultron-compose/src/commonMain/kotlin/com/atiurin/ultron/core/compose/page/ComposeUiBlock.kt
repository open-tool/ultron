package com.atiurin.ultron.core.compose.page

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasParent

open class ComposeUiBlock(val blockMatcher: SemanticsMatcher) {
    fun _deepSearch(childMatcher: SemanticsMatcher): SemanticsMatcher {
        return hasAnyAncestor(blockMatcher) and childMatcher
    }
    fun _parentSearch(childMatcher: SemanticsMatcher): SemanticsMatcher {
        return hasParent(blockMatcher) and childMatcher
    }

    fun child(matcher: SemanticsMatcher, deepSearch: Boolean = true): SemanticsMatcher {
        return when (deepSearch) {
            true -> _deepSearch(matcher)
            false -> _parentSearch(matcher)
        }
    }

    fun <T : ComposeUiBlock> childBlock(
        childMatcher: SemanticsMatcher,
        uiBlockFactory: (SemanticsMatcher) -> T,
        deepSearch: Boolean = true
    ): T {
        val newMatcher = when (deepSearch) {
            true -> _deepSearch(childMatcher)
            false -> _parentSearch(childMatcher)
        }
        return uiBlockFactory(newMatcher)
    }
}
