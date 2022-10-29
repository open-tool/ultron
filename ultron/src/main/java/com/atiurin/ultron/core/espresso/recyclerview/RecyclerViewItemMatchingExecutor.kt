package com.atiurin.ultron.core.espresso.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.hamcrest.Matcher

class RecyclerViewItemMatchingExecutor(
    private val ultronRecyclerView: UltronRecyclerView,
    private val itemViewMatcher: Matcher<View>
) : RecyclerViewItemExecutor {
    override fun scrollToItem(offset: Int) {
        ultronRecyclerView.scrollToIem(itemViewMatcher, offset = offset)
    }

    override fun getItemMatcher(): Matcher<View> {
        return ultronRecyclerView.atItem(itemViewMatcher)
    }

    override fun getItemViewHolder(): RecyclerView.ViewHolder? {
        return ultronRecyclerView.getViewHolderList(itemViewMatcher).firstOrNull()
    }

    override fun getItemChildMatcher(childMatcher: Matcher<View>): Matcher<View> {
        return ultronRecyclerView.atItemChild(itemViewMatcher, childMatcher)
    }
}