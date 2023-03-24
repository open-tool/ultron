package com.atiurin.ultron.extensions

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import com.atiurin.ultron.extensions.getProperty

internal fun SemanticsNodeInteraction.getUseMergedTree(): Boolean? {
    return this.getProperty("useUnmergedTree")
}
internal fun SemanticsNodeInteractionCollection.getUseMergedTree(): Boolean? {
    return this.getProperty("useUnmergedTree")
}
