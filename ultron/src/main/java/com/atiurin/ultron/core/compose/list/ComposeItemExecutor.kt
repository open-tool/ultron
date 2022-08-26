package com.atiurin.ultron.core.compose.list

import androidx.compose.ui.test.SemanticsMatcher
import com.atiurin.ultron.core.compose.nodeinteraction.UltronComposeSemanticsNodeInteraction

interface ComposeItemExecutor {
    fun scrollToItem(offset: Int = 0)
    fun getItemInteraction(): UltronComposeSemanticsNodeInteraction
    fun getItemChildInteraction(childMatcher: SemanticsMatcher): UltronComposeSemanticsNodeInteraction
}