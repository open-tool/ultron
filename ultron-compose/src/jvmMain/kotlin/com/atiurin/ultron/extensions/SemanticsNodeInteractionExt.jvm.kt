package com.atiurin.ultron.extensions

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.SemanticsSelector
import com.atiurin.ultron.exceptions.UltronException

actual fun SemanticsNodeInteraction.getSelectorDescription(): String = this.getSemanticsSelector().description

fun SemanticsNodeInteractionCollection.getSemanticsSelector() = this.getProperty<SemanticsSelector>("selector")
    ?: throw UltronException("Couldn't get selector from $this")

fun SemanticsNodeInteraction.getSemanticsSelector() = this.getProperty<SemanticsSelector>("selector")
    ?: throw UltronException("Couldn't get selector from $this")

