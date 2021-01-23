package com.atiurin.ultron.recyclerview

import android.view.View
import com.atiurin.ultron.core.config.UltronConfig
import org.hamcrest.Matcher

interface RecyclerViewItemExecutor {
    fun scrollToItem()
    fun getItemMatcher(): Matcher<View>
    fun getItemChildMatcher(childMatcher: Matcher<View>): Matcher<View>
}