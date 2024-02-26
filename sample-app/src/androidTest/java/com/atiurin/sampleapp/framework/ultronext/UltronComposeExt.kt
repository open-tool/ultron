package com.atiurin.sampleapp.framework.ultronext

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import com.atiurin.sampleapp.compose.LikeCounter
import com.atiurin.ultron.core.common.UltronOperationType
import com.atiurin.ultron.core.compose.nodeinteraction.UltronComposeSemanticsNodeInteraction
import com.atiurin.ultron.core.compose.operation.UltronComposeOperationParams

// create SemanticsMatcher for custom SemanticsPropertyKey
fun hasLikesCount(expected: Int) = SemanticsMatcher.expectValue(LikeCounter, expected)

// assert operation
fun UltronComposeSemanticsNodeInteraction.assertLikesCount(expected: Int) = perform(
    UltronComposeOperationParams(
        operationName = "Assert likes counter value in ${elementInfo.name}",
        operationDescription = "Assert $LikeCounter value in ${elementInfo.name} during $timeoutMs ms",
        operationType = CustomOperationType.ASSERT_LIKES_COUNT
    )
) {
    it.assert(hasLikesCount(expected))
}

// get value operation
fun UltronComposeSemanticsNodeInteraction.getLikes() : Int = execute {
    getNodeConfigProperty(LikeCounter)
}

//extend SemanticsMatcher with your new operation
fun SemanticsMatcher.assertLikesCount(expected: Int) = UltronComposeSemanticsNodeInteraction(this).assertLikesCount(expected)
fun SemanticsMatcher.getLikes() = UltronComposeSemanticsNodeInteraction(this).getLikes()

enum class CustomOperationType : UltronOperationType { ASSERT_LIKES_COUNT, GET_LIKES  }