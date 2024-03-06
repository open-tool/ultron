package com.atiurin.ultron.extensions

import androidx.compose.ui.test.SelectionResult
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsSelector

fun SemanticsSelector.addFindNodeInTreeSelector(
    selectorName: String,
    matcher: SemanticsMatcher
): SemanticsSelector {
    return SemanticsSelector(
        "(${this.description}).$selectorName(${matcher.description})",
        requiresExactlyOneNode = false,
        chainedInputSelector = this
    ) { nodes ->
        SelectionResult(nodes.findNodeInTree(matcher))
    }
}

