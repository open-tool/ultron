package com.atiurin.ultron.core.compose.view

import android.view.View
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.node.InteroperableComposeUiNode
import androidx.compose.ui.test.SemanticsNodeInteraction
import com.atiurin.ultron.core.compose.nodeinteraction.UltronComposeSemanticsNodeInteraction
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Extract view from [androidx.compose.ui.viewinterop.AndroidView] in Compose
 */
fun UltronComposeSemanticsNodeInteraction.fetchView(): View? = semanticsNodeInteraction.fetchView()

/**
 * Extract view from [androidx.compose.ui.viewinterop.AndroidView] in Compose
 */
@OptIn(InternalComposeUiApi::class)
fun SemanticsNodeInteraction.fetchView(): View? = (fetchSemanticsNode()
    .layoutInfo as? InteroperableComposeUiNode)?.getInteropView()

/**
 * Extract view from [androidx.compose.ui.viewinterop.AndroidView] in Compose and wrap with Matcher
 */
fun UltronComposeSemanticsNodeInteraction.onView(): Matcher<View> = semanticsNodeInteraction.onView()

/**
 * Extract view from [androidx.compose.ui.viewinterop.AndroidView] in Compose and wrap with Matcher
 */
@OptIn(InternalComposeUiApi::class)
fun SemanticsNodeInteraction.onView(): Matcher<View> = AndroidViewMatcher((fetchSemanticsNode()
    .layoutInfo as? InteroperableComposeUiNode)?.getInteropView())

private class AndroidViewMatcher(private val interopView: View?) : TypeSafeMatcher<View>() {
    override fun describeTo(description: Description) {
        description.appendText("View is InteropView in Compose")
    }
    override fun matchesSafely(view: View): Boolean = view == interopView
}
