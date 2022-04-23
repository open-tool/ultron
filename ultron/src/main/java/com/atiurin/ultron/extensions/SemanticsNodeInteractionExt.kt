package com.atiurin.ultron.extensions

import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsSelector

fun SemanticsNodeInteraction.getDescription() = this.getProperty<SemanticsSelector>("selector")?.description

fun SemanticsNodeInteraction.getConfigField(name: String): Any? {
    for ((key, value) in this.fetchSemanticsNode().config) {
        if (key.name == name) {
            return value
        }
    }
    return null
}

fun SemanticsNodeInteraction.getOneOfConfigFields(names: List<String>): Any? {
    names.forEach { name ->
        val value = getConfigField(name)
        value?.let { return it }
    }
    return null
}

fun SemanticsNodeInteraction.requireSemantics(
    node: SemanticsNode,
    vararg properties: SemanticsPropertyKey<*>,
    errorMessage: () -> String
) {
    val missingProperties = properties.filter { it !in node.config }
    if (missingProperties.isNotEmpty()) {
        val msg = "${errorMessage()}, the node is missing [${missingProperties.joinToString()}]"
        throw AssertionError(msg)
    }
}