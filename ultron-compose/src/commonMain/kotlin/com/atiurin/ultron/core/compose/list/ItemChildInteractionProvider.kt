package com.atiurin.ultron.core.compose.list

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.printToLog
import com.atiurin.ultron.core.compose.SemanticsNodeInteractionProviderContainer
import com.atiurin.ultron.core.compose.config.UltronComposeConfig
import com.atiurin.ultron.core.config.UltronCommonConfig
import com.atiurin.ultron.exceptions.UltronAssertionException
import com.atiurin.ultron.log.UltronLog

interface ItemChildInteractionProvider {
    fun onItemChild(
        listMatcher: SemanticsMatcher,
        itemMatcher: SemanticsMatcher,
        childMatcher: SemanticsMatcher,
        useUnmergedTree: Boolean
    ): () -> SemanticsNodeInteraction = {
        SemanticsNodeInteractionProviderContainer.withSemanticsProvider {
            it.onNode(listMatcher).performScrollToNode(itemMatcher)
            it.onNode(
                hasAnyAncestor(listMatcher)
                    .and(hasAnyAncestor(itemMatcher))
                    .and(childMatcher),
                useUnmergedTree
            )
        }
    }

    fun onVisibleItemChild(listMatcher: SemanticsMatcher, index: Int, childMatcher: SemanticsMatcher, useUnmergedTree: Boolean): () -> SemanticsNodeInteraction = {
        SemanticsNodeInteractionProviderContainer.withSemanticsProvider {
            val itemInteraction = it.onNode(listMatcher, useUnmergedTree).onChildAt(index)
            if (childMatcher.matches(itemInteraction.fetchSemanticsNode())) {
                UltronLog.warn("Child matcher matches item itself. Ultron will return item interaction instead of child interaction")
                itemInteraction.onParent().printToLog("Ultron")
            }
            getItemChildInteraction(itemInteraction, childMatcher)
        }
    }
}

internal fun getItemChildInteraction(nodeInteraction: SemanticsNodeInteraction, childMatcher: SemanticsMatcher): SemanticsNodeInteraction {
    val node = nodeInteraction.fetchSemanticsNode()
    if (childMatcher.matches(node)) return nodeInteraction
    if (node.children.any { childMatcher.matches(it) }) {
        return nodeInteraction.onChildren().filterToOne(childMatcher)
    }
    node.children.forEachIndexed { index, semanticsNode ->
        if (hasAnyDescendant(childMatcher).matches(semanticsNode)) {
            return getItemChildInteraction(nodeInteraction.onChildAt(index), childMatcher)
        }
    }
    throw UltronAssertionException("Can't find child with matcher $childMatcher")
}


expect fun getItemChildInteractionProvider(): ItemChildInteractionProvider