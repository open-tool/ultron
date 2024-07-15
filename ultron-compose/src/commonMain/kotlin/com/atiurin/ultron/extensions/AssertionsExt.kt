package com.atiurin.ultron.extensions

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert

fun SemanticsNodeInteraction.assertIsIndeterminate(): SemanticsNodeInteraction = assert(isIndeterminate())