package com.atiurin.sampleapp.framework.ultronext

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import com.atiurin.ultron.core.compose.nodeinteraction.UltronComposeSemanticsNodeInteraction
import com.atiurin.ultron.core.compose.operation.UltronComposeOperationParams
import com.atiurin.ultron.extensions.assertMatches

// ------ custom action definition ------
// step 1: extend base class [UltronComposeSemanticsNodeInteraction] with custom action logic
fun UltronComposeSemanticsNodeInteraction.getProgress() = execute(
    UltronComposeOperationParams(
        operationName = "Get '${elementInfo.name}' current progress",
        operationDescription = "Compose get current progress of '${elementInfo.name}' during $timeoutMs ms"
    )
) {
    val node = semanticsNodeInteraction.fetchSemanticsNode()
    node.config[SemanticsProperties.ProgressBarRangeInfo].current
}

// step 2: extend [SemanticsMatcher] class with new action method
fun SemanticsMatcher.getProgress(): Float = UltronComposeSemanticsNodeInteraction(this).getProgress()

// ------ custom assertion definition ------
// step 1: define custom matcher logic - use function, not a subclass (it's a compose way)
fun hasProgress(expected: Float) = SemanticsMatcher(
    description = "ProgressBarRangeInfo.current = [$expected]"
) { node ->
    val current = node.config[SemanticsProperties.ProgressBarRangeInfo].current
    current == expected
}
// step 2: extend [UltronComposeSemanticsNodeInteraction] class with extension function and [assertMatches] for easier validation
//fun UltronComposeSemanticsNodeInteraction.assertProgress(expected: Float) = assertMatches(hasProgress(expected))
// step 3: extend [SemanticsMatcher] class with new assertion method
//fun SemanticsMatcher.assertProgress(expected: Float) = UltronComposeSemanticsNodeInteraction(this).assertProgress(expected)

// ------ custom ui element definition ------
// just make a subclass of [UltronComposeSemanticsNodeInteraction] and add new method
open class ProgressBar(val matcher: SemanticsMatcher) : UltronComposeSemanticsNodeInteraction(matcher) {
    fun assertProgress(expected: Float) = matcher.assertMatches(hasProgress(expected))
}

fun progressBar(block: () -> SemanticsMatcher): Lazy<ProgressBar> = lazy { ProgressBar(block()) }