package com.atiurin.ultron.core.espresso.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.contrib.RecyclerViewActions
import com.atiurin.ultron.extensions.perform
import com.atiurin.ultron.extensions.withTimeout
import org.hamcrest.Matcher

class RecyclerViewItemMatchingExecutor(
    private val ultronRecyclerView: UltronRecyclerView,
    private val itemViewMatcher: Matcher<View>
) : RecyclerViewItemExecutor {
    override fun scrollToItem() {
        //TODO work around this trade off
        ultronRecyclerView.recyclerViewMatcher.withTimeout(ultronRecyclerView.getTimeout()).perform(
            viewAction = RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(itemViewMatcher),
            description = "RecyclerViewActions scrollTo $itemViewMatcher"
        )
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