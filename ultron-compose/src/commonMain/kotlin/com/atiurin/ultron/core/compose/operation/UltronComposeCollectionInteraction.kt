package com.atiurin.ultron.core.compose.operation

import androidx.compose.ui.test.SemanticsMatcher
import com.atiurin.ultron.core.compose.ComposeTestContainer
import com.atiurin.ultron.core.compose.nodeinteraction.UltronComposeSemanticsNodeInteraction
import com.atiurin.ultron.utils.AssertUtils

class UltronComposeCollectionInteraction(
    val matcher: SemanticsMatcher,
    val useUnmergedTree: Boolean = false
) {

    companion object {
        fun allNodes(matcher: SemanticsMatcher, useUnmergedTree: Boolean = false): UltronComposeCollectionInteraction {
            return UltronComposeCollectionInteraction(matcher, useUnmergedTree)
        }
    }
    private fun getCollection() = ComposeTestContainer.getProvider().onAllNodes(matcher, useUnmergedTree)

    fun get(index: Int) = UltronComposeSemanticsNodeInteraction(
        getCollection()[index]
    )

    fun fetchSemanticNodes() = getCollection().fetchSemanticsNodes()
}

fun UltronComposeCollectionInteraction.assertSize(expected: Int, operationTimeoutMs: Long = 5_000){
    AssertUtils.assertTrueAndValueInDesc(
        valueBlock = { fetchSemanticNodes().size },
        assertionBlock = { value -> value == expected },
        timeoutMs = operationTimeoutMs,
        desc ={ value -> "UltronComposeCollection($matcher) size expected to be $expected (actual size = $value)" }
    )
}