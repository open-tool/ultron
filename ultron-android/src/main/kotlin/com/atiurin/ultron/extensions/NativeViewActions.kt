package com.atiurin.ultron.extensions

import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.core.espresso.recyclerview.UltronRecyclerView
import com.atiurin.ultron.core.espresso.recyclerview.UltronRecyclerViewItem
import org.hamcrest.Matcher

fun Matcher<View>.dismiss() = UltronEspressoInteraction(onView(this)).dismiss()
fun Matcher<View>.scrollDown() = UltronEspressoInteraction(onView(this)).scrollDown()
fun Matcher<View>.scrollUp() = UltronEspressoInteraction(onView(this)).scrollUp()
fun ViewInteraction.dismiss() = UltronEspressoInteraction(this).dismiss()
fun ViewInteraction.scrollDown() = UltronEspressoInteraction(this).scrollDown()
fun ViewInteraction.scrollUp() = UltronEspressoInteraction(this).scrollUp()
fun DataInteraction.dismiss() = UltronEspressoInteraction(this).dismiss()
fun DataInteraction.scrollDown() = UltronEspressoInteraction(this).scrollDown()
fun DataInteraction.scrollUp() = UltronEspressoInteraction(this).scrollUp()
fun UltronRecyclerView.dismiss() = apply { recyclerViewInteraction.dismiss() }
fun UltronRecyclerView.scrollDown() = apply { recyclerViewInteraction.scrollDown() }
fun UltronRecyclerView.scrollUp() = apply { recyclerViewInteraction.scrollUp() }
fun UltronRecyclerViewItem.dismiss() = apply { getInteraction().dismiss() }
fun UltronRecyclerViewItem.scrollDown() = apply { getInteraction().scrollDown() }
fun UltronRecyclerViewItem.scrollUp() = apply { getInteraction().scrollUp() }

fun <T> UltronEspressoInteraction<T>.dismiss() =
    perform(viewAction = NativeAccessibilityAction(AccessibilityNodeInfo.ACTION_DISMISS, "dismiss"))
fun <T> UltronEspressoInteraction<T>.scrollDown() =
    perform(viewAction = NativeAccessibilityAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD, "scroll down"))
fun <T> UltronEspressoInteraction<T>.scrollUp() =
    perform(viewAction = NativeAccessibilityAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD, "scroll up"))

/** Uses the native widget's action, including BottomSheetBehavior and nested scrolling lists. */
internal class NativeAccessibilityAction(private val actionId: Int, private val label: String) : ViewAction {
    override fun getConstraints(): Matcher<View> = isDisplayed()
    override fun getDescription(): String = label

    override fun perform(uiController: UiController, view: View) {
        val target = findTarget(view)
            ?: error("No visible View exposes accessibility action '$label' within or above the target")
        check(target.performAccessibilityAction(actionId, null)) { "View rejected accessibility action '$label'" }
        uiController.loopMainThreadUntilIdle()
    }

    private fun findTarget(view: View): View? {
        if (supports(view)) return view
        val descendants = descendants(view).filter(::supports).toList()
        check(descendants.size <= 1) { "Multiple Views expose '$label'; select the intended widget" }
        descendants.singleOrNull()?.let { return it }
        return generateSequence(view.parent as? View) { it.parent as? View }.firstOrNull(::supports)
    }

    private fun descendants(view: View): Sequence<View> = sequence {
        if (view is ViewGroup) {
            for (index in 0 until view.childCount) {
                val child = view.getChildAt(index)
                yield(child)
                yieldAll(descendants(child))
            }
        }
    }

    private fun supports(view: View): Boolean {
        if (!view.isShown) return false
        // Forward/backward actions on horizontal widgets must not implement vertical scrolling.
        if (actionId == AccessibilityNodeInfo.ACTION_SCROLL_FORWARD && !view.canScrollVertically(1)) return false
        if (actionId == AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD && !view.canScrollVertically(-1)) return false
        val info = view.createAccessibilityNodeInfo()
        return try {
            info.actionList.any { it.id == actionId }
        } finally {
            @Suppress("DEPRECATION")
            info.recycle()
        }
    }
}
