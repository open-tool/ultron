package com.atiurin.ultron.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.contrib.RecyclerViewActions
import com.atiurin.ultron.extensions.execute
import org.hamcrest.Matcher

class RecyclerViewItemMatchingExecutor(
    private val recyclerViewMatcher: Matcher<View>,
    private val itemViewMatcher: Matcher<View>
) : RecyclerViewItemExecutor {
    override fun scrollToItem() {
        //TODO work around this trade off
        recyclerViewMatcher.execute(
            RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(itemViewMatcher)
        )
    }

    override fun getItemMatcher(): Matcher<View> {
        return withRecyclerView(recyclerViewMatcher).atItem(itemViewMatcher)
    }

    override fun getItemChildMatcher(childMatcher: Matcher<View>): Matcher<View> {
        return withRecyclerView(recyclerViewMatcher).atItemChild(itemViewMatcher, childMatcher)
    }
}