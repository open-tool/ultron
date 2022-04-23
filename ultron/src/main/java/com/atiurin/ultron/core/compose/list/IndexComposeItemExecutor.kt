package com.atiurin.ultron.core.compose.list

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import com.atiurin.ultron.core.compose.nodeinteraction.UltronComposeSemanticsNodeInteraction

class IndexComposeItemExecutor(
    val ultronComposeList: UltronComposeList,
    val index: Int
) : ComposeItemExecutor {
    override fun scrollToItem(offset: Int) {
        ultronComposeList.scrollToIndex(index)
    }
    override fun getItemInteraction() : UltronComposeSemanticsNodeInteraction = ultronComposeList.onVisibleItem(index)
    override fun getItemChildInteraction(childMatcher: SemanticsMatcher): UltronComposeSemanticsNodeInteraction = ultronComposeList.onVisibleItemChild(index, childMatcher)
}