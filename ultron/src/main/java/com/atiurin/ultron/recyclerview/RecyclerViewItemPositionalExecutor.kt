package com.atiurin.ultron.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.contrib.RecyclerViewActions
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.extensions.withTimeout
import org.hamcrest.Matcher

class RecyclerViewItemPositionalExecutor(
    private val ultronRecyclerView: UltronRecyclerView,
    private val position: Int,
    private val scrollTimeoutMs: Long
) : RecyclerViewItemExecutor {

    init {
        if (position < 0){
            throw UltronException("Position value can't be negative: '$position'")
        }
    }
    override fun scrollToItem() {
        ultronRecyclerView.assertHasItemAtPosition(position, scrollTimeoutMs)
        ultronRecyclerView.recyclerViewMatcher.withTimeout(scrollTimeoutMs).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(position)
        )
    }

    override fun getItemMatcher(): Matcher<View> {
        return ultronRecyclerView.atPosition(position)
    }

    override fun getItemChildMatcher(childMatcher: Matcher<View>): Matcher<View> {
        return ultronRecyclerView.atPositionItemChild(position, childMatcher)
    }
}