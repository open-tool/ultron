package com.atiurin.ultron.extensions

import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.test.SemanticsMatcher

fun Iterable<SemanticsNode>.findNodeInTree(matcher: SemanticsMatcher): List<SemanticsNode> {
    val targetNodes = mutableListOf<SemanticsNode>()
    this.forEach { node ->
        targetNodes.addAll(node.findNodeInTree(matcher))
    }
    return targetNodes
}

fun SemanticsNode.findNodeInTree(matcher: SemanticsMatcher): List<SemanticsNode> {
    val targetNodes = mutableListOf<SemanticsNode>()
    if (matcher.matches(this)) {
        targetNodes.add(this)
        return targetNodes
    } else {
        targetNodes.addAll(this.children.findNodeInTree(matcher))
    }
    return targetNodes
}