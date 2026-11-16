package com.atiurin.ultron.custom.espresso.action

import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.view.ScrollingView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.util.HumanReadables
import org.hamcrest.Matcher

/**
 * Performs an accessibility action exposed by the matched View itself,
 * e.g. a page scroll of a RecyclerView or the dismissal of a Snackbar.
 */
internal class NativeAccessibilityAction(private val actionId: Int, private val label: String) : ViewAction {
    override fun getConstraints(): Matcher<View> = isDisplayed()
    override fun getDescription(): String = label

    override fun perform(uiController: UiController, view: View) {
        // Widgets stop exposing a scroll action at the boundary, so the direction is checked first.
        // It also keeps forward/backward of a horizontal widget from passing as vertical scrolling.
        val direction = when (actionId) {
            AccessibilityNodeInfo.ACTION_SCROLL_FORWARD -> 1
            AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD -> -1
            else -> null
        }
        if (direction != null && !view.canScrollVertically(direction)) {
            fail(view, "View cannot scroll vertically in the requested direction ('$label')")
        }
        if (!exposesAction(view)) fail(view, "View does not expose accessibility action '$label'")
        if (!view.performAccessibilityAction(actionId, null)) fail(view, "View rejected accessibility action '$label'")
        uiController.loopMainThreadUntilIdle()
        if (direction != null) waitForScrollToSettle(uiController, view)
    }

    /** Widgets like RecyclerView and NestedScrollView animate this scroll, so wait until the offset stops changing. */
    private fun waitForScrollToSettle(uiController: UiController, view: View) {
        var offset = scrollOffset(view)
        repeat(SETTLE_MAX_CHECKS) {
            uiController.loopMainThreadForAtLeast(SETTLE_CHECK_INTERVAL_MS)
            val current = scrollOffset(view)
            if (current == offset) return
            offset = current
        }
    }

    private fun scrollOffset(view: View): Int =
        if (view is ScrollingView) view.computeVerticalScrollOffset() else view.scrollY

    private fun exposesAction(view: View): Boolean {
        val info = view.createAccessibilityNodeInfo()
        return try {
            info.actionList.any { it.id == actionId }
        } finally {
            @Suppress("DEPRECATION")
            info.recycle()
        }
    }

    private fun fail(view: View, message: String): Nothing = throw PerformException.Builder()
        .withActionDescription(description)
        .withViewDescription(HumanReadables.describe(view))
        .withCause(RuntimeException(message))
        .build()

    private companion object {
        const val SETTLE_CHECK_INTERVAL_MS = 100L
        const val SETTLE_MAX_CHECKS = 30
    }
}
