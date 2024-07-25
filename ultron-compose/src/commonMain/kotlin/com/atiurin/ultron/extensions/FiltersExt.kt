package com.atiurin.ultron.extensions

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.SemanticsMatcher

fun isIndeterminate(): SemanticsMatcher = SemanticsMatcher.expectValue(
    SemanticsProperties.ToggleableState, ToggleableState.Indeterminate
)