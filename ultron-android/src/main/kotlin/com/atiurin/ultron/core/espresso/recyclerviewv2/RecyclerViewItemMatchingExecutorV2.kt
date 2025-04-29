package com.atiurin.ultron.core.espresso.recyclerviewv2

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.atiurin.ultron.core.espresso.recyclerview.RecyclerViewItemExecutor
import org.hamcrest.Matcher

class RecyclerViewItemMatchingExecutorV2(
    private val ultronRecyclerView: UltronRecyclerViewV2,
    private val itemViewMatcher: Matcher<View>,
) : RecyclerViewItemExecutor {
    override fun scrollToItem(offset: Int) {
        ultronRecyclerView.scrollToItem(itemViewMatcher, offset = offset)
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