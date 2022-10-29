package com.atiurin.ultron.core.espresso.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.contrib.RecyclerViewActions
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.exceptions.UltronOperationException
import com.atiurin.ultron.extensions.perform
import org.hamcrest.Matcher

class RecyclerViewItemPositionalExecutor(
    private val ultronRecyclerView: UltronRecyclerView,
    private val position: Int
) : RecyclerViewItemExecutor {
    init {
        if (position < 0) {
            throw UltronOperationException("Position value can't be negative: '$position'")
        }
    }

    override fun scrollToItem(offset: Int) {
        ultronRecyclerView.assertHasItemAtPosition(position)
        val itemCount = ultronRecyclerView.getSize()
        val positionToScroll = position + offset
        val finalPositionToScroll = when {
            positionToScroll in 1 until itemCount -> positionToScroll
            positionToScroll >= itemCount -> itemCount - 1
            else -> 0
        }
        ultronRecyclerView.recyclerViewMatcher.perform(
            viewAction = RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(finalPositionToScroll),
            description = "RecyclerViewActions scrollToPosition $position with offset = $offset"
        )
    }

    override fun getItemMatcher(): Matcher<View> {
        return ultronRecyclerView.atPosition(position)
    }

    override fun getItemViewHolder(): RecyclerView.ViewHolder? {
        return ultronRecyclerView.getViewHolderAtPosition(position)
    }

    override fun getItemChildMatcher(childMatcher: Matcher<View>): Matcher<View> {
        return ultronRecyclerView.atPositionItemChild(position, childMatcher)
    }
}