package com.atiurin.ultron.core.compose.option

import com.atiurin.ultron.core.compose.nodeinteraction.UltronComposeSemanticsNodeInteraction

data class ComposeSwipeOption(
    val startXOffset: Float = 0f,
    val endXOffset: Float = 0f,
    val startYOffset: Float = 0f,
    val endYOffset: Float = 0f,
    val durationMs: Long = UltronComposeSemanticsNodeInteraction.DEFAULT_SWIPE_DURATION
)