package com.atiurin.sampleapp.framework.ultronext

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import com.atiurin.sampleapp.compose.GetProgress
import com.atiurin.ultron.core.common.UltronOperationType
import com.atiurin.ultron.core.compose.UltronComposeSemanticsMatcher
import com.atiurin.ultron.core.compose.nodeinteraction.UltronComposeSemanticsNodeInteraction
import com.atiurin.ultron.core.compose.operation.UltronComposeOperation
import com.atiurin.ultron.extensions.getDescription


fun hasProgress(value: Float): SemanticsMatcher = SemanticsMatcher.expectValue(
    GetProgress, value
)

enum class ComposeOperationTypeExt : UltronOperationType { ASSERT_PROGRESS }

fun UltronComposeSemanticsNodeInteraction.assertProgress(expected: Float) = apply {
    UltronComposeSemanticsNodeInteraction.executeOperation(
        operation = UltronComposeOperation(
            operationBlock = { semanticsNodeInteraction.assert(hasProgress(expected)) },
            name = "Assert '${semanticsNodeInteraction.getDescription()}' has progress $expected",
            type = ComposeOperationTypeExt.ASSERT_PROGRESS,
            description = "Compose assertProgress = $expected in '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
            timeoutMs = timeoutMs
        ),
        resultHandler = this.resultHandler
    )
}

fun UltronComposeSemanticsMatcher.assertProgress(expected: Float) = this.getUltronComposeInteraction().assertProgress(expected)
fun SemanticsMatcher.assertProgress(expected: Float) = UltronComposeSemanticsMatcher(this).assertProgress(expected)