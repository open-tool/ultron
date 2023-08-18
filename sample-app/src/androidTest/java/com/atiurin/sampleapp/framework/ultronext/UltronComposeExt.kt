package com.atiurin.sampleapp.framework.ultronext

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import com.atiurin.sampleapp.compose.GetProgress
import com.atiurin.ultron.core.compose.nodeinteraction.UltronComposeSemanticsNodeInteraction
import com.atiurin.ultron.core.compose.operation.UltronComposeOperationParams
import com.atiurin.ultron.extensions.getDescription

fun hasProgress(value: Float) = SemanticsMatcher.expectValue(GetProgress, value)
fun UltronComposeSemanticsNodeInteraction.assertProgress(expected: Float) = perform (
    UltronComposeOperationParams(
        operationName = "Assert '${semanticsNodeInteraction.getDescription()}' has progress $expected",
        operationDescription = "Compose assertProgress = $expected in '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms"
    )
) {
    semanticsNodeInteraction.assert(hasProgress(expected))
}

fun SemanticsMatcher.assertProgress(expected: Float) = UltronComposeSemanticsNodeInteraction(this).assertProgress(expected)