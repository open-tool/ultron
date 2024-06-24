package com.atiurin.ultron.extensions

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection

internal fun SemanticsNodeInteraction.getUseMergedTree(): Boolean? {
    return this.getProperty("useUnmergedTree")
}
internal fun SemanticsNodeInteractionCollection.getUseMergedTree(): Boolean? {
    return this.getProperty("useUnmergedTree")
}