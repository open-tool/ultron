package com.atiurin.ultron.core.espresso.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.util.HumanReadables
import com.atiurin.ultron.utils.runOnUiThread
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

@Suppress("UNCHECKED_CAST")
internal fun <VH : RecyclerView.ViewHolder> itemsMatching(
    recyclerView: RecyclerView,
    viewHolderMatcher: Matcher<VH>,
    maxItemsCount: Int = -1,
    itemSearchLimit: Int = -1
): List<MatchedItem> {
    val matchedItems = mutableListOf<MatchedItem>()
    val adapter = recyclerView.adapter ?: return matchedItems
    val layoutManager = recyclerView.layoutManager ?: return matchedItems

    val itemCount = adapter.itemCount
    if (itemCount <= 0) return matchedItems

    val searchLimit = if (itemSearchLimit in 1 until itemCount) itemSearchLimit else itemCount
    if (maxItemsCount == 0) return matchedItems

    fun addMatch(position: Int, viewHolder: VH) {
        matchedItems.add(
            MatchedItem(
                position,
                HumanReadables.getViewHierarchyErrorMessage(
                    viewHolder.itemView,
                    null,
                    "\n\n*** Matched ViewHolder at position: $position ***",
                    null
                )
            )
        )
    }

    runOnUiThread {
        val visibleHolders = mutableSetOf<Int>()
        for (i in 0 until layoutManager.childCount) {
            val child = layoutManager.getChildAt(i) ?: continue
            val position = recyclerView.getChildAdapterPosition(child)
            if (position in 0 until searchLimit) {
                val viewHolder = recyclerView.getChildViewHolder(child) as? VH ?: continue
                if (viewHolderMatcher.matches(viewHolder)) {
                    addMatch(position, viewHolder)
                    visibleHolders.add(position)
                    if (maxItemsCount > 0 && matchedItems.size >= maxItemsCount) return@runOnUiThread
                }
            }
        }

        for (position in 0 until searchLimit) {
            if (maxItemsCount > 0 && matchedItems.size >= maxItemsCount) break
            if (position in visibleHolders) continue
            if (position >= adapter.itemCount) continue

            val itemType = adapter.getItemViewType(position)
            val tempHolder = adapter.createViewHolder(recyclerView, itemType) as VH
            adapter.bindViewHolder(tempHolder, position)
            if (viewHolderMatcher.matches(tempHolder)) {
                addMatch(position, tempHolder)
            }
        }
    }
    return matchedItems
}

/**
 * Wrapper for matched items in recycler view which contains position and description of matched
 * view.
 */
internal class MatchedItem(val position: Int, val description: String) {

    override fun toString(): String {
        return description
    }
}

/**
 * Creates matcher for view holder with given item view matcher.
 *
 * @param itemViewMatcher a item view matcher which is used to match item.
 * @return a matcher which matches a view holder containing item matching itemViewMatcher.
 */
internal fun <VH : RecyclerView.ViewHolder> viewHolderMatcher(
    itemViewMatcher: Matcher<View>
): Matcher<VH> {
    return object : TypeSafeMatcher<VH>() {
        override fun matchesSafely(item: VH): Boolean {
            return itemViewMatcher.matches(item.itemView)
        }

        override fun describeTo(description: Description) {
            description.appendText("holder with view: ")
            itemViewMatcher.describeTo(description)
        }
    }
}
