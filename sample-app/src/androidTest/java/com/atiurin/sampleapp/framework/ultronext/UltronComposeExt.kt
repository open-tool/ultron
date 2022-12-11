package com.atiurin.sampleapp.framework.ultronext

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import com.atiurin.sampleapp.compose.GetProgress
import com.atiurin.ultron.core.common.UltronOperationType
import com.atiurin.ultron.core.compose.nodeinteraction.UltronComposeSemanticsNodeInteraction
import com.atiurin.ultron.core.compose.operation.UltronComposeOperation
import com.atiurin.ultron.extensions.getDescription


fun hasProgress(value: Float): SemanticsMatcher = SemanticsMatcher.expectValue(GetProgress, value)

enum class ComposeOperationTypeExt : UltronOperationType { ASSERT_PROGRESS }

fun UltronComposeSemanticsNodeInteraction.assertProgress(expected: Float) = apply {
    executeOperation(
        operationBlock = { semanticsNodeInteraction.assert(hasProgress(expected)) },
        name = "Assert '${semanticsNodeInteraction.getDescription()}' has progress $expected",
        type = ComposeOperationTypeExt.ASSERT_PROGRESS, //it's not required, you can skip this param. DEFAULT one will be used
        description = "Compose assertProgress = $expected in '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
    )
}

fun SemanticsMatcher.assertProgress(expected: Float) = UltronComposeSemanticsNodeInteraction(this).assertProgress(expected)