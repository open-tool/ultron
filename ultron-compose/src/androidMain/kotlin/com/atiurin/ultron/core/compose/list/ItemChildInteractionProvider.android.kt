package com.atiurin.ultron.core.compose.list

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.performScrollToNode
import com.atiurin.ultron.core.compose.ComposeTestContainer.withComposeTestEnvironment
import com.atiurin.ultron.extensions.findNodeInTree

actual fun getItemChildInteractionProvider(): ItemChildInteractionProvider {
    return AndroidItemChildInteractionProvider()
}

class AndroidItemChildInteractionProvider : ItemChildInteractionProvider {
    override fun onItemChild(
        listMatcher: SemanticsMatcher,
        itemMatcher: SemanticsMatcher,
        childMatcher: SemanticsMatcher,
        useUnmergedTree: Boolean
    ): () -> SemanticsNodeInteraction = {
        withComposeTestEnvironment { testEnvironment ->
            testEnvironment.provider.onNode(listMatcher, useUnmergedTree)
                .performScrollToNode(itemMatcher)
                .onChildren().filterToOne(itemMatcher)
                .findNodeInTree(childMatcher, useUnmergedTree)
        }
    }

    override fun onVisibleItemChild(
        listMatcher: SemanticsMatcher,
        index: Int,
        childMatcher: SemanticsMatcher,
        useUnmergedTree: Boolean
    ): () -> SemanticsNodeInteraction = {
        withComposeTestEnvironment { testEnvironment ->
            testEnvironment.provider.onNode(listMatcher, useUnmergedTree)
                .onChildAt(index)
                .findNodeInTree(childMatcher, useUnmergedTree)
        }
    }
}