package com.atiurin.ultron.extensions

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.SemanticsSelector
import androidx.compose.ui.test.TestContext
import com.atiurin.ultron.exceptions.UltronException

actual fun SemanticsNodeInteraction.getSelectorDescription(): String  = this.getSemanticsSelector().description

fun SemanticsNodeInteractionCollection.getTestContext() = this.getProperty<TestContext>("testContext")
    ?: throw UltronException("Couldn't get testContext from $this")

fun SemanticsNodeInteractionCollection.getSemanticsSelector() = this.getProperty<SemanticsSelector>("selector")
    ?: throw UltronException("Couldn't get selector from $this")

fun SemanticsNodeInteraction.getTestContext() = this.getProperty<TestContext>("testContext")
    ?: throw UltronException("Couldn't get testContext from $this")

fun SemanticsNodeInteraction.getSemanticsSelector() = this.getProperty<SemanticsSelector>("selector")
    ?: throw UltronException("Couldn't get selector from $this")

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