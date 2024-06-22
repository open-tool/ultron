package com.atiurin.ultron.extensions

import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.SemanticsSelector
import androidx.compose.ui.test.TestContext
import androidx.compose.ui.test.assert
import com.atiurin.ultron.exceptions.UltronException

fun SemanticsNodeInteraction.getDescription() = this.getProperty<SemanticsSelector>("selector")?.description

fun SemanticsNodeInteractionCollection.getTestContext() = this.getProperty<TestContext>("testContext")
    ?: throw UltronException("Couldn't get testContext from $this")

fun SemanticsNodeInteractionCollection.getSemanticsSelector() = this.getProperty<SemanticsSelector>("selector")
    ?: throw UltronException("Couldn't get selector from $this")

fun SemanticsNodeInteraction.getTestContext() = this.getProperty<TestContext>("testContext")
    ?: throw UltronException("Couldn't get testContext from $this")

fun SemanticsNodeInteraction.getSemanticsSelector() = this.getProperty<SemanticsSelector>("selector")
    ?: throw UltronException("Couldn't get selector from $this")

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

fun SemanticsNodeInteraction.findNodeInTree(
    matcher: SemanticsMatcher,
    useUnmergedTree: Boolean,
): SemanticsNodeInteraction {
    return SemanticsNodeInteraction(
        testContext = this.getTestContext(),
        useUnmergedTree = useUnmergedTree,
        selector = this.getSemanticsSelector().addFindNodeInTreeSelector("findNodeInTree", matcher)
    )
}

fun SemanticsNodeInteraction.assertIsIndeterminate(): SemanticsNodeInteraction =
    assert(isIndeterminate())
