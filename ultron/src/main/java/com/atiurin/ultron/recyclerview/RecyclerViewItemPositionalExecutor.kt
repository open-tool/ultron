package com.atiurin.ultron.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.contrib.RecyclerViewActions
import com.atiurin.ultron.extensions.execute
import org.hamcrest.Matcher

class RecyclerViewItemPositionalExecutor(
    private val recyclerViewMatcher: Matcher<View>,
    private val position: Int
) : RecyclerViewItemExecutor {

    init {
        if (position < 0){
            throw Exception("Position value can't be negative: '$position'")
        }
    }
    override fun scrollToItem() {
        //TODO work around this trade off
        recyclerViewMatcher.execute(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(position)
        )
    }

    override fun getItemMatcher(): Matcher<View> {
        return withRecyclerView(recyclerViewMatcher).atPosition(position)
    }

    override fun getItemChildMatcher(childMatcher: Matcher<View>): Matcher<View> {
        return withRecyclerView(recyclerViewMatcher).atPositionItemChild(position, childMatcher)
    }
}