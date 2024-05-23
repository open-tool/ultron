package com.atiurin.ultron.core.compose.operation

import androidx.compose.ui.test.SemanticsMatcher
import com.atiurin.ultron.core.compose.SemanticsNodeInteractionProviderContainer
import com.atiurin.ultron.core.compose.nodeinteraction.UltronComposeSemanticsNodeInteraction

class UltronComposeCollectionInteraction(
    val matcher: SemanticsMatcher,
    val useUnmergedTree: Boolean = false
) {
    companion object {
        fun allNodes(matcher: SemanticsMatcher, useUnmergedTree: Boolean = false): UltronComposeCollectionInteraction {
            return UltronComposeCollectionInteraction(matcher, useUnmergedTree)
        }
    }

    fun get(index: Int) = UltronComposeSemanticsNodeInteraction(
        SemanticsNodeInteractionProviderContainer.getProvider().onAllNodes(matcher, useUnmergedTree)[index]
    )

    fun fetchSemanticNodes() = SemanticsNodeInteractionProviderContainer.getProvider().onAllNodes(matcher, useUnmergedTree).fetchSemanticsNodes()
}