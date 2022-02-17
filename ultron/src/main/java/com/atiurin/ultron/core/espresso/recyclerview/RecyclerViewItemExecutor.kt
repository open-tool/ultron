package com.atiurin.ultron.core.espresso.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.hamcrest.Matcher

interface RecyclerViewItemExecutor {
    fun scrollToItem(offset: Int = 0)
    fun getItemMatcher(): Matcher<View>
    fun getItemViewHolder(): RecyclerView.ViewHolder?
    fun getItemChildMatcher(childMatcher: Matcher<View>): Matcher<View>
}