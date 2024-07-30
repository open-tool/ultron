package com.atiurin.ultron.extensions

import androidx.compose.ui.test.SemanticsNodeInteraction

actual fun SemanticsNodeInteraction.getSelectorDescription(): String =
    "[UI element description isn't implemented non Android platforms due to https://issuetracker.google.com/issues/342778294. Vote for this issue!]"