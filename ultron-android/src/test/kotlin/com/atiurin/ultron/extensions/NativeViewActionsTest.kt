package com.atiurin.ultron.extensions

import android.os.Bundle
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.LinearLayout
import androidx.test.espresso.UiController
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
class NativeViewActionsTest {
    private val controller = mock(UiController::class.java)
    private val dismiss = AccessibilityNodeInfo.ACTION_DISMISS

    @Test
    fun dispatchesTheWidgetsActionInBothVerticalDirections() {
        for (action in listOf(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD, AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)) {
            val view = ActionView(action)
            NativeAccessibilityAction(action, "scroll").perform(controller, view)
            assertEquals(listOf(action), view.requests)
        }
    }

    @Test
    fun resolvesSingleDescendantAndNearestAncestor() {
        val sheet = ActionView(dismiss)
        val content = View(RuntimeEnvironment.getApplication())
        sheet.addView(content)
        NativeAccessibilityAction(dismiss, "dismiss").perform(controller, content)
        assertEquals(listOf(dismiss), sheet.requests)

        val container = ActionView(null)
        val child = ActionView(dismiss)
        container.addView(child)
        NativeAccessibilityAction(dismiss, "dismiss").perform(controller, container)
        assertEquals(listOf(dismiss), child.requests)
    }

    @Test
    fun prefersTheTargetOverItsDescendants() {
        val sheet = ActionView(dismiss)
        val child = ActionView(dismiss)
        sheet.addView(child)
        NativeAccessibilityAction(dismiss, "dismiss").perform(controller, sheet)
        assertEquals(listOf(dismiss), sheet.requests)
        assertTrue(child.requests.isEmpty())
    }

    @Test
    fun rejectsAmbiguousAndUnsupportedTargetsWithoutActing() {
        val container = ActionView(null)
        val children = listOf(ActionView(dismiss), ActionView(dismiss))
        children.forEach(container::addView)
        assertThrows(IllegalStateException::class.java) {
            NativeAccessibilityAction(dismiss, "dismiss").perform(controller, container)
        }
        assertTrue(children.all { it.requests.isEmpty() })
        assertThrows(IllegalStateException::class.java) {
            NativeAccessibilityAction(dismiss, "dismiss").perform(controller, ActionView(null))
        }
    }

    @Test
    fun rejectsHorizontalScrollAndRejectedWidgetAction() {
        val horizontal = ActionView(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD, vertical = false)
        assertThrows(IllegalStateException::class.java) {
            NativeAccessibilityAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD, "scroll down").perform(controller, horizontal)
        }
        assertTrue(horizontal.requests.isEmpty())
        assertThrows(IllegalStateException::class.java) {
            NativeAccessibilityAction(dismiss, "dismiss").perform(controller, ActionView(dismiss, accepted = false))
        }
    }

    private class ActionView(
        private val action: Int?,
        private val vertical: Boolean = true,
        private val accepted: Boolean = true,
    ) : LinearLayout(RuntimeEnvironment.getApplication()) {
        val requests = mutableListOf<Int>()
        override fun isShown() = true
        override fun canScrollVertically(direction: Int) = vertical
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
