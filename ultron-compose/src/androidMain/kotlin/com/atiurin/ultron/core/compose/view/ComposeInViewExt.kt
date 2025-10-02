package com.atiurin.ultron.core.compose.view

import android.view.View
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.test.InternalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.createTestContext

/**
 * Select Compose Node by [matcher] in a particular View
 */
@OptIn(InternalTestApi::class)
fun View.semanticsNodeInteraction(
    matcher: SemanticsMatcher,
    useUnmergedTree: Boolean = true
): SemanticsNodeInteraction {
    val composeView = requireNotNull(this as? AbstractComposeView) { "View is not subtype of AbstractComposeView. It is ${this::class.java}" }
    return SemanticsNodeInteraction(
        createTestContext(ViewAndroidTestOwner(composeView)),
        useUnmergedTree,
        matcher
    )
}