package com.atiurin.ultron.core.espresso.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import org.hamcrest.Matcher

interface RecyclerViewItemExecutor {
    fun scrollToItem(offset: Int = 0)
    fun getItemMatcher(): Matcher<View>
    fun getItemViewHolder(): RecyclerView.ViewHolder?
    fun getItemInteraction(): UltronEspressoInteraction<ViewInteraction> = UltronEspressoInteraction(onView(getItemMatcher()))
    fun getItemChildMatcher(childMatcher: Matcher<View>): Matcher<View>
    fun getItemChildInteraction(childInteraction: UltronEspressoInteraction<ViewInteraction>): UltronEspressoInteraction<ViewInteraction> = UltronEspressoInteraction(
        onView((getItemChildMatcher(childInteraction.getInteractionMatcher()!!)))
    )
}