package com.atiurin.ultron.core.espresso.recyclerviewv2

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.atiurin.ultron.core.espresso.recyclerview.RecyclerViewItemExecutor
import com.atiurin.ultron.exceptions.UltronOperationException
import org.hamcrest.Matcher

class RecyclerViewItemPositionalExecutorV2(
    private val ultronRecyclerView: UltronRecyclerViewV2,
    private val position: Int,
) : RecyclerViewItemExecutor {
    init {
        if (position < 0) {
            throw UltronOperationException("Position value can't be negative: '$position'")
        }
    }

    override fun scrollToItem(offset: Int) {
        ultronRecyclerView.scrollToItem(position, offset)
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