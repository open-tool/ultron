package com.atiurin.ultron.recyclerview

import android.view.View
import androidx.annotation.IntegerRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.util.TreeIterables
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import com.atiurin.ultron.extensions.*
import com.atiurin.ultron.utils.AssertUtils
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 *  Provides a set of interaction with a RecyclerView list.
 */
open class UltronRecyclerView(val recyclerViewMatcher: Matcher<View>) {
    private var recyclerView: RecyclerView? = null

    inline fun <reified T : UltronRecyclerViewItem> getItem(
        itemMatcher: Matcher<View>,
        autoScroll: Boolean = true,
        scrollTimeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT
    ) = UltronRecyclerViewItem.getInstance<T>(this, itemMatcher, autoScroll, scrollTimeoutMs)

    /**
     * @param scrollTimeoutMs of item waiting
     */
    inline fun <reified T : UltronRecyclerViewItem> getItem(
        itemPosition: Int,
        autoScroll: Boolean = true,
        scrollTimeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT
    ): T {
        return UltronRecyclerViewItem.getInstance(this, itemPosition, autoScroll, scrollTimeoutMs)
    }

    /** @return simple RecyclerView item matching '[matcher]'
     *  @param autoScroll evaluate scrollTo matched item in case of true value
     *  @param scrollTimeoutMs of item waiting
     * */
    fun item(matcher: Matcher<View>, autoScroll: Boolean = true, scrollTimeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT) =
        UltronRecyclerViewItem(recyclerViewMatcher, matcher, autoScroll, scrollTimeoutMs)

    /** @return simple RecyclerView item at [position]
     *  @param autoScroll evaluate scrollTo item at [position] in case of true value
     *  @param scrollTimeoutMs of item waiting
     * */
    fun item(
        position: Int, autoScroll: Boolean = true, scrollTimeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT
    ) = UltronRecyclerViewItem(recyclerViewMatcher, position, autoScroll, scrollTimeoutMs)


    open fun getRecyclerViewList(): RecyclerView? {
        recyclerViewMatcher.assertMatches(identifyRecyclerView())
        return recyclerView
    }

    /**
     * identify recyclerView items count immediately
     * be careful: when list loading takes some time then it could return false zero value
     *
     * use [assertSize] to make sure the list has correct items count
     */
    open fun getSize(): Int {
        return getRecyclerViewList()?.adapter?.itemCount ?: 0
    }

    /**
     * Asserts RecyclerView list has [expected] items count during [timeoutMs]
     */
    open fun assertSize(expected: Int, timeoutMs: Long = UltronConfig.Espresso.ASSERTION_TIMEOUT) {
        AssertUtils.assertTrue(
            { getSize() == expected },
            timeoutMs,
            "Assert RecyclerView($recyclerViewMatcher) size is $expected, but actual ${getSize()}"
        )
    }

    /**
     * Asserts RecyclerView list has item at [position] during [timeoutMs]
     */
    open fun assertHasItemAtPosition(position: Int, timeoutMs: Long = UltronConfig.Espresso.ASSERTION_TIMEOUT) {
        AssertUtils.assertTrue(
            { getSize() >= position },
            timeoutMs,
            "Wait RecyclerView($recyclerViewMatcher) size >= $position (actual size = ${getSize()})"
        )
    }

    open fun isDisplayed() = recyclerViewMatcher.isDisplayed()
    open fun isNotDisplayed() = recyclerViewMatcher.isNotDisplayed()
    open fun doesNotExist() = recyclerViewMatcher.doesNotExist()
    open fun isEnabled() = recyclerViewMatcher.isEnabled()
    open fun isNotEnabled() = recyclerViewMatcher.isNotEnabled()
    open fun hasContentDescription(contentDescription: String) =
        recyclerViewMatcher.hasContentDescription(contentDescription)

    open fun hasContentDescription(resourceId: Int) =
        recyclerViewMatcher.hasContentDescription(resourceId)

    open fun hasContentDescription(charSequenceMatcher: Matcher<CharSequence>) =
        recyclerViewMatcher.hasContentDescription(charSequenceMatcher)

    open fun contentDescriptionContains(text: String) =
        recyclerViewMatcher.contentDescriptionContains(text)

    open fun assertMatches(matcher: Matcher<View>) = recyclerViewMatcher.assertMatches(matcher)
    open fun withTimeout(timeoutMs: Long) = recyclerViewMatcher.withTimeout(timeoutMs)
    open fun withResultHandler(resultHandler: (EspressoOperationResult<UltronEspressoOperation>) -> Unit) =
        recyclerViewMatcher.withResultHandler(resultHandler)

    /**
     * @param itemMatcher describes how to identify item in the list. It shouldn't be unique in  view hierarchy
     * @return the matcher to the exact recyclerView item which it's possible to interact
     */
    open fun atItem(itemMatcher: Matcher<View>): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var itemView: View? = null

            override fun describeTo(description: Description) {
                description.appendText("RecyclerViewItem recyclerViewMatcher: '$recyclerViewMatcher', itemMatcher: '$itemMatcher'")
            }

            override fun matchesSafely(view: View?): Boolean {
                if (itemView == null) itemView = findItemView(itemMatcher, view?.rootView)
                return if (itemView != null) itemView == view else false
            }
        }
    }

    /**
     * @return matcher to RecyclerView item at position [position]
     */
    open fun atPosition(position: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var itemView: View? = null
            override fun describeTo(description: Description) {
                description.appendText("RecyclerViewItem recyclerViewMatcher: '$recyclerViewMatcher', itemPosition: '$position'")
            }

            override fun matchesSafely(view: View?): Boolean {
                if (itemView == null) itemView = findItemViewAtPosition(position, view?.rootView)
                return if (itemView != null) itemView == view else false
            }
        }
    }

    /**
     * @param itemMatcher describes how to identify item in the list. It shouldn't be unique in  view hierarchy
     * @param childMatcher describes how to identify child inside item. It shouldn't be unique in scope of RecyclerView list
     * @return matcher to a RecyclerView item child which it's possible to interact
     */
    open fun atItemChild(itemMatcher: Matcher<View>, childMatcher: Matcher<View>): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var childView: View? = null

            override fun describeTo(description: Description) {
                description.appendText("RecyclerViewItem recyclerViewMatcher: '$recyclerViewMatcher', itemMatcher: '$itemMatcher', childMatcher: '$childMatcher'")
            }

            override fun matchesSafely(view: View?): Boolean {
                findItemView(itemMatcher, view?.rootView)?.let {
                    childView = it.findChildView(childMatcher)
                }
                return if (childView != null) childView == view else false
            }
        }
    }

    /**
     * @param position of item in a RecyclerView list
     * @param childMatcher describes how to identify child inside item. It shouldn't be unique in scope of RecyclerView list
     * @return matcher to a RecyclerView item child which it's possible to interact
     */
    open fun atPositionItemChild(position: Int, childMatcher: Matcher<View>): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var childView: View? = null
            override fun describeTo(description: Description) {
                description.appendText("RecyclerViewItem recyclerViewMatcher: '$recyclerViewMatcher', itemPosition: '$position', childMatcher: '$childMatcher'")
            }

            override fun matchesSafely(view: View?): Boolean {
                findItemViewAtPosition(position, view?.rootView)?.let {
                    childView = it.findChildView(childMatcher)
                }
                return if (childView != null) childView == view else false
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

    private fun identifyRecyclerView(): BoundedMatcher<View, RecyclerView> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("RecyclerView matches ").appendValue(recyclerViewMatcher)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                if (recyclerViewMatcher.matches(view)) {
                    recyclerView = view
                }
                return recyclerView == view
            }
        }
    }
}

fun withRecyclerView(recyclerViewMatcher: Matcher<View>): UltronRecyclerView {
    return UltronRecyclerView(recyclerViewMatcher)
}

fun withRecyclerView(@IntegerRes resourceId: Int): UltronRecyclerView {
    return UltronRecyclerView(withId(resourceId))
}

private fun View.findChildView(matcher: Matcher<View>): View? {
    var childView: View? = null
    for (child in TreeIterables.breadthFirstViewTraversal(this)) {
        if (matcher.matches(child)) {
            childView = child
            break
        }
    }
    return childView
}