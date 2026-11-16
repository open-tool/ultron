package com.atiurin.ultron.custom.espresso.action

import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.LinearLayout
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
class NativeAccessibilityActionTest {
    private val controller = mock(UiController::class.java)
    private val dismiss = AccessibilityNodeInfo.ACTION_DISMISS
    private val forward = AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
    private val backward = AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD

    @Test
    fun dispatchesTheActionToTheMatchedView() {
        for (action in listOf(forward, backward, dismiss)) {
            val view = ActionView(action)
            NativeAccessibilityAction(action, "action").perform(controller, view)
            assertEquals(listOf(action), view.requests)
        }
    }

    @Test
    fun doesNotFallBackToDescendantOrAncestor() {
        val parent = ActionView(dismiss)
        val target = ActionView(null)
        val child = ActionView(dismiss)
        parent.addView(target)
        target.addView(child)
        assertFailure("View does not expose accessibility action 'dismiss'") {
            NativeAccessibilityAction(dismiss, "dismiss").perform(controller, target)
        }
        assertTrue(parent.requests.isEmpty() && child.requests.isEmpty())
    }

    @Test
    fun reportsScrollBoundaryAndHorizontalOnlyViewSeparatelyFromMissingAction() {
        // A view at its boundary or a horizontal-only one cannot scroll vertically in the direction.
        val view = ActionView(forward, canScroll = false)
        assertFailure("View cannot scroll vertically in the requested direction ('scroll down')") {
            NativeAccessibilityAction(forward, "scroll down").perform(controller, view)
        }
        assertTrue(view.requests.isEmpty())
        assertFailure("View does not expose accessibility action 'scroll up'") {
            NativeAccessibilityAction(backward, "scroll up").perform(controller, ActionView(null))
        }
    }

    @Test
    fun reportsActionRejectedByTheView() {
        assertFailure("View rejected accessibility action 'dismiss'") {
            NativeAccessibilityAction(dismiss, "dismiss").perform(controller, ActionView(dismiss, accepted = false))
        }
    }

    private fun assertFailure(message: String, block: () -> Unit) {
        val exception = assertThrows(PerformException::class.java) { block() }
        assertEquals(message, exception.cause?.message)
    }

    private class ActionView(
        private val action: Int?,
        private val canScroll: Boolean = true,
        private val accepted: Boolean = true,
    ) : LinearLayout(RuntimeEnvironment.getApplication()) {
        val requests = mutableListOf<Int>()
        override fun canScrollVertically(direction: Int) = canScroll
        override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(info)
            action?.let { info.addAction(AccessibilityNodeInfo.AccessibilityAction(it, "test action")) }
        }
        override fun performAccessibilityAction(action: Int, arguments: Bundle?): Boolean {
            requests += action
            return accepted
        }
    }
}
