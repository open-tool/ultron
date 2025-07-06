package com.atiurin.ultron.core.espresso.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import org.hamcrest.Matcher

class RecyclerViewItemMatchingExecutor(
    private val ultronRecyclerView: UltronRecyclerView,
    private val itemViewInteraction: UltronEspressoInteraction<ViewInteraction>,
) : RecyclerViewItemExecutor {
    override fun scrollToItem(offset: Int) {
        ultronRecyclerView.scrollToItem(itemViewInteraction, offset = offset)
    }

    override fun getItemMatcher(): Matcher<View> {
        return ultronRecyclerView.atItem(itemViewInteraction.getInteractionMatcher()!!)
    }

    override fun getItemViewHolder(): RecyclerView.ViewHolder? {
        return ultronRecyclerView.getViewHolderList(itemViewInteraction.getInteractionMatcher()!!).firstOrNull()
    }

    override fun getItemInteraction(): UltronEspressoInteraction<ViewInteraction> {
        return UltronEspressoInteraction(onView(getItemMatcher()))
            .withName("RecyclerViewItem with: '${itemViewInteraction.elementInfo.name}', RecyclerView: ${ultronRecyclerView.recyclerViewInteraction.elementInfo.name}")
    }

    override fun getItemChildMatcher(childMatcher: Matcher<View>): Matcher<View> {
        return ultronRecyclerView.atItemChild(itemViewInteraction.getInteractionMatcher()!!, childMatcher)
    }

    override fun getItemChildInteraction(childInteraction: UltronEspressoInteraction<ViewInteraction>): UltronEspressoInteraction<ViewInteraction> {
        return UltronEspressoInteraction(onView(getItemChildMatcher(childInteraction.getInteractionMatcher()!!)))
            .withName("'${childInteraction.elementInfo.name}' of RecyclerViewItem with: '${itemViewInteraction.elementInfo.name}', RecyclerView: ${ultronRecyclerView.recyclerViewInteraction.elementInfo.name}")
    }
}