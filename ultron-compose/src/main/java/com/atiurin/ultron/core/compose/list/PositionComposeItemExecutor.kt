package com.atiurin.ultron.core.compose.list

import androidx.compose.ui.test.SemanticsMatcher
import com.atiurin.ultron.core.compose.nodeinteraction.UltronComposeSemanticsNodeInteraction
import com.atiurin.ultron.exceptions.UltronException

class PositionComposeItemExecutor (
    val ultronComposeList: UltronComposeList,
    val position: Int
) : ComposeItemExecutor {
    private val positionKey = ultronComposeList.positionPropertyKey
        ?: throw UltronException("[positionPropertyKey] parameter is not specified for Compose List")
    private val positionMatcher = SemanticsMatcher.expectValue(positionKey, position)

    override fun scrollToItem(offset: Int) {
        ultronComposeList.scrollToNode(positionMatcher)
    }
    override fun getItemInteraction() : UltronComposeSemanticsNodeInteraction {
        return ultronComposeList.onItem(positionMatcher)
    }
    override fun getItemChildInteraction(childMatcher: SemanticsMatcher): UltronComposeSemanticsNodeInteraction {
        return ultronComposeList.onItemChild(positionMatcher, childMatcher)
    }
}