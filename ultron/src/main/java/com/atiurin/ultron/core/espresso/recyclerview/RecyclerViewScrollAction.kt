package com.atiurin.ultron.core.espresso.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.exceptions.UltronOperationException
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

class RecyclerViewScrollAction(private val itemMatcher: Matcher<View>, private val itemSearchLimit: Int = -1, private val offset: Int = 0) : ViewAction {
    override fun getConstraints(): Matcher<View> {
        return allOf(
            ViewMatchers.isAssignableFrom(RecyclerView::class.java),
            ViewMatchers.isDisplayed()
        )
    }

    override fun getDescription(): String {
        return "Scroll RecyclerView to item $itemMatcher${if (itemSearchLimit >= 0) " with offset = $offset and search limit = $itemSearchLimit" else ""}"
    }

    override fun perform(uiController: UiController, view: View) {
        val recyclerView = view as RecyclerView
        val viewHolderMatcher: Matcher<RecyclerView.ViewHolder> = viewHolderMatcher(itemMatcher)
        val matchedItem = itemsMatching(recyclerView, viewHolderMatcher, 1, itemSearchLimit = itemSearchLimit).firstOrNull()
            ?: throw UltronOperationException("No RecyclerView item found matches '$itemMatcher'${if (itemSearchLimit >= 0) " with search limit = $itemSearchLimit" else ""} ")
        val itemCount = recyclerView.adapter?.itemCount ?: 0
        val positionToScroll = matchedItem.position + offset
        val finalPositionToScroll = when {
            positionToScroll in 1 until itemCount -> positionToScroll
            positionToScroll >= itemCount -> itemCount - 1
            else -> 0
        }
        recyclerView.scrollToPosition(finalPositionToScroll)
    }
}