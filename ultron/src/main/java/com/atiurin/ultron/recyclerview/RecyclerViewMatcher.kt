package com.atiurin.ultron.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.util.TreeIterables
import com.atiurin.ultron.extensions.assertMatches
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

open class RecyclerViewMatcher(val recyclerViewMatcher: Matcher<View>) {
    private var recyclerView: RecyclerView? = null
    var itemView: View? = null

    open fun atItem(itemMatcher: Matcher<View>): Matcher<View> {
        return object : TypeSafeMatcher<View>() {

            override fun describeMismatchSafely(item: View?, mismatchDescription: Description) {
                if (recyclerView == null) {
                    mismatchDescription.appendText("No matching RecyclerView with : [$recyclerViewMatcher]. ")
                    return
                }
                mismatchDescription.appendText("Found recycler view matches : [$recyclerViewMatcher]. ")
                if (itemView == null) {
                    mismatchDescription.appendText("No matching recycler view item with : [$itemMatcher]")
                    return
                }
//                super.describeMismatchSafely(item, mismatchDescription)
            }
            override fun describeTo(description: Description) {
                description.appendText("RecyclerViewItem recyclerViewMatcher: '$recyclerViewMatcher', itemMatcher: '$itemMatcher'")
            }

            override fun matchesSafely(view: View?): Boolean {
                if (itemView == null) itemView = findItemView(itemMatcher, view?.rootView)
                return if (itemView != null) {
                    itemView == view
                } else false
            }
        }
    }

    open fun atItemChild(itemMatcher: Matcher<View>, childMatcher: Matcher<View>): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var childView: View? = null


            override fun describeTo(description: Description) {
                description.appendText("RecyclerViewItem recyclerViewMatcher: '$recyclerViewMatcher', itemMatcher: '$itemMatcher', childMatcher: '$childMatcher'")
                return
            }

            override fun describeMismatchSafely(item: View?, mismatchDescription: Description) {
                if (recyclerView == null) {
                    mismatchDescription.appendText("No matching recycler view with : [$recyclerViewMatcher]. ")
                    return
                }
                mismatchDescription.appendText("Found recycler view matches : [$recyclerViewMatcher]. ")
                if (itemView == null) {
                    mismatchDescription.appendText("No matching recycler view item with : [$itemMatcher]")
                    return
                }
                mismatchDescription.appendText("Found recycler view item matches : [$itemMatcher]. ")
                if (childView == null) {
                    mismatchDescription.appendText("No matching item child view with : [$childMatcher]")
                    return
                }
//                super.describeMismatchSafely(item, mismatchDescription)
            }

            override fun matchesSafely(view: View?): Boolean {
                if (itemView == null) itemView = findItemView(itemMatcher, view?.rootView)
                if (itemView != null) {
                    if (childView == null) {
                        for (childView in TreeIterables.breadthFirstViewTraversal(itemView)) {
                            if (childMatcher.matches(childView)) {
                                this.childView = childView
                                break
                            }
                        }
                    }
                }
                return if (childView != null) {
                    childView == view
                } else false
            }
        }
    }

    open fun atPosition(position: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("RecyclerViewItem recyclerViewMatcher: '$recyclerViewMatcher', itemPosition: '$position'")
            }

            override fun describeMismatchSafely(item: View?, mismatchDescription: Description) {
                if (recyclerView == null) {
                    mismatchDescription.appendText("No matching recycler view with : [$recyclerViewMatcher]. ")
                    return
                }
                mismatchDescription.appendText("Found recycler view matches : [$recyclerViewMatcher]. ")
                if (itemView == null) {
                    mismatchDescription.appendText("No recycler view item at position : [$position]")
                    return
                }
                super.describeMismatchSafely(item, mismatchDescription)
            }

            override fun matchesSafely(view: View?): Boolean {
                if (itemView == null) itemView = findItemViewAtPosition(position, view?.rootView)
                return if (itemView != null) {
                    itemView == view
                } else false
            }
        }
    }

    open fun atPositionItemChild(position: Int, childMatcher: Matcher<View>): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var childView: View? = null
            override fun describeTo(description: Description) {
                description.appendText("RecyclerViewItem recyclerViewMatcher: '$recyclerViewMatcher', itemPosition: '$position', childMatcher: '$childMatcher'")
            }

            override fun describeMismatchSafely(item: View?, mismatchDescription: Description) {
                if (recyclerView == null) {
                    mismatchDescription.appendText("No matching recycler view with : [$recyclerViewMatcher]. ")
                    return
                }
                mismatchDescription.appendText("Found recycler view matches : [$recyclerViewMatcher]. ")
                if (itemView == null) {
                    mismatchDescription.appendText("No recycler view item at position : [$position]")
                    return
                }
                mismatchDescription.appendText("Found recycler view item at position : [$position]. ")
                if (childView == null) {
                    mismatchDescription.appendText("No matching item child view with : [$childMatcher]")
                    return
                }
                super.describeMismatchSafely(item, mismatchDescription)
            }
            override fun matchesSafely(view: View?): Boolean {
                if (itemView == null) itemView = findItemViewAtPosition(position, view?.rootView)
                if (itemView != null) {
                    if (childView == null) {
                        for (childView in TreeIterables.breadthFirstViewTraversal(itemView)) {
                            if (childMatcher.matches(childView)) {
                                this.childView = childView
                                break
                            }
                        }
                    }
                }
                return if (childView != null) {
                    childView == view
                } else false
            }
        }
    }


    private fun findItemView(itemMatcher: Matcher<View>, rootView: View?): View? {
        for (childView in TreeIterables.breadthFirstViewTraversal(rootView)) {
            if (recyclerViewMatcher.matches(childView)) {
                val recyclerView = childView as RecyclerView
                this.recyclerView = recyclerView    // to describe the error
                val viewHolderMatcher: Matcher<RecyclerView.ViewHolder> =
                    viewHolderMatcher(itemMatcher)
                val matchedItems: List<MatchedItem> =
                    itemsMatching(recyclerView, viewHolderMatcher, 1)
                if (matchedItems.isEmpty()) return null
                return recyclerView.findViewHolderForAdapterPosition(matchedItems[0].position)
                    ?.itemView
            }
        }
        return null
    }

    private fun findItemViewAtPosition(position: Int, rootView: View?): View? {
        for (childView in TreeIterables.breadthFirstViewTraversal(rootView)) {
            if (recyclerViewMatcher.matches(childView)) {
                val recyclerView = childView as RecyclerView
                this.recyclerView = recyclerView    // to describe the error
                return recyclerView.findViewHolderForAdapterPosition(position)?.itemView
            }
        }
        return null
    }

    private fun defineRecyclerView(): BoundedMatcher<View, RecyclerView> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("RecyclerView matches ").appendValue(recyclerViewMatcher)
                return
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                recyclerView = view
                return recyclerViewMatcher.matches(recyclerView)
            }
        }
    }

    open fun getRecyclerViewList(): RecyclerView? {
        recyclerViewMatcher.assertMatches(defineRecyclerView())
        return recyclerView
    }

    open fun getSize(): Int {
        val count = getRecyclerViewList()?.adapter?.itemCount
        return count ?: 0
    }
}

fun withRecyclerView(recyclerViewMatcher: Matcher<View>): RecyclerViewMatcher {
    return RecyclerViewMatcher(recyclerViewMatcher)
}
