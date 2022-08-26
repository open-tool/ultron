package com.atiurin.ultron.core.compose.list

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import com.atiurin.ultron.core.compose.nodeinteraction.UltronComposeSemanticsNodeInteraction

class MatcherComposeItemExecutor(
    val ultronComposeList: UltronComposeList,
    val itemMatcher: SemanticsMatcher
) : ComposeItemExecutor {
    override fun scrollToItem(offset: Int) {
        ultronComposeList.scrollToNode(itemMatcher)
    }

    override fun getItemInteraction(): UltronComposeSemanticsNodeInteraction {
        scrollToItem()
        return ultronComposeList.onItem(itemMatcher)
    }

    override fun getItemChildInteraction(childMatcher: SemanticsMatcher): UltronComposeSemanticsNodeInteraction {
        scrollToItem()
        return ultronComposeList.onItemChild(itemMatcher, childMatcher)
    }
}