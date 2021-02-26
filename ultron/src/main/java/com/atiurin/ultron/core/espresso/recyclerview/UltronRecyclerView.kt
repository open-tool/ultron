package com.atiurin.ultron.core.espresso.recyclerview

import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.annotation.IntegerRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.util.TreeIterables
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.config.UltronConfig.Espresso.Companion.DEFAULT_RECYCLER_VIEW_LOAD_TIMEOUT
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.UltronEspresso.executeAssertion
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import com.atiurin.ultron.core.espresso.assertion.EspressoAssertionType
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.extensions.*
import com.atiurin.ultron.utils.AssertUtils
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 *  Provides a set of interactions with RecyclerView list.
 *  @param recyclerViewMatcher help ot identify RecyclerView inside view hierarchy
 *  @param loadTimeoutMs specifies a time of waiting while RecyclerView items will be loaded
 */
open class UltronRecyclerView(val recyclerViewMatcher: Matcher<View>, val loadTimeoutMs: Long = DEFAULT_RECYCLER_VIEW_LOAD_TIMEOUT) {
    private var recyclerView: RecyclerView? = null
    private var recyclerViewOperationsTimeoutMs = 5_000L

    /**
     * @return current [UltronRecyclerView] operations timeout
     */
    fun getTimeout() = recyclerViewOperationsTimeoutMs

    /** @return [UltronRecyclerViewItem] subclass instance matches [itemMatcher]
     *
     * Note: never add inner modifier to [T] class
     *
     * Note: [T] class should have a constructor without parameters, eg
     *
     *    class SomeRecyclerViewItem : UltronRecyclerViewItem(){...}
     * */
    inline fun <reified T : UltronRecyclerViewItem> getItem(itemMatcher: Matcher<View>, autoScroll: Boolean = true): T {
        waitItemsLoaded()
        return UltronRecyclerViewItem.getInstance(this, itemMatcher, autoScroll)
    }

    /** @return [UltronRecyclerViewItem] subclass instance at position [itemPosition]
     *
     * Note: never add inner modifier to [T] class
     *
     * Note: [T] class should have a constructor without parameters, eg
     *
     *    class SomeRecyclerViewItem : UltronRecyclerViewItem(){...}
     * */
    inline fun <reified T : UltronRecyclerViewItem> getItem(itemPosition: Int, autoScroll: Boolean = true): T {
        waitItemsLoaded()
        return UltronRecyclerViewItem.getInstance(this, itemPosition, autoScroll)
    }

    /** @return [UltronRecyclerViewItem] subclass instance at first position
     *
     * Note: never add inner modifier to [T] class
     *
     * Note: [T] class should have a constructor without parameters, eg
     *
     *    class SomeRecyclerViewItem : UltronRecyclerViewItem(){...}
     * */
    inline fun <reified T : UltronRecyclerViewItem> getFirstItem(autoScroll: Boolean = true) = getItem<T>(0, autoScroll)

    /** @return [UltronRecyclerViewItem] subclass instance at last position
     *
     * Note: never add inner modifier to [T] class
     *
     * Note: [T] class should have a constructor without parameters, eg
     *
     *    class SomeRecyclerViewItem : UltronRecyclerViewItem(){...}
     * */
    inline fun <reified T : UltronRecyclerViewItem> getLastItem(autoScroll: Boolean = true) : T {
        waitItemsLoaded()
        return UltronRecyclerViewItem.getInstance(this, getSize() - 1, autoScroll)
    }

    /**
     * @return first [T] matched [matcher]
     *
     * it could be used if you can't determine unique matcher for item
     *
     * @param matcher determines how to find item
     * @param autoScroll evaluate scrollTo this item in case of true value
     */
    inline fun <reified T : UltronRecyclerViewItem> getFirstItemMatched(matcher: Matcher<View>, autoScroll: Boolean = true) = getItemMatched<T>(matcher, 0, autoScroll)

    /**
     * @return [T] matched [matcher]
     *
     * it could be used if you can't determine unique matcher for item, eg
     *
     * RecyclerView has several matched items. You can get any of them by specifying desired [index] value
     *
     * @param matcher determines how to find items
     * @param index value from 0 to lastIndex of matched items
     * @param autoScroll evaluate scrollTo this item in case of true value
     */
    inline fun <reified T : UltronRecyclerViewItem> getItemMatched(matcher: Matcher<View>, index: Int, autoScroll: Boolean = true): T {
        waitItemsLoaded()
        AssertUtils.assertTrue(
            { getItemsAdapterPositionList(matcher).getOrNull(index) != null }, getTimeout(), "RecyclerView($recyclerViewMatcher) has item matched '$matcher' with index $index"
        )
        val position = getItemsAdapterPositionList(matcher).getOrNull(index) ?: throw UltronException("RecyclerViewItem matched $matcher with index $index disappeared")
        return UltronRecyclerViewItem.getInstance(this, position, autoScroll)
    }

    /**
     * @return last [T] matched [matcher]
     *
     * it could be used if you can't determine unique matcher for item
     *
     * @param matcher determines how to find item
     * @param autoScroll evaluate scrollTo this item in case of true value
     */
    inline fun <reified T : UltronRecyclerViewItem> getLastItemMatched(matcher: Matcher<View>, autoScroll: Boolean = true): T {
        waitItemsLoaded()
        AssertUtils.assertTrue(
            { getItemsAdapterPositionList(matcher).lastOrNull() != null }, getTimeout(), "RecyclerView($recyclerViewMatcher) has last item matched '$matcher'"
        )
        val position = getItemsAdapterPositionList(matcher).lastOrNull() ?: throw UltronException("Last RecyclerViewItem matched $matcher disappeared")
        return UltronRecyclerViewItem.getInstance(this, position, autoScroll)
    }

    /** @return simple [UltronRecyclerViewItem] matches '[matcher]'
     * @param matcher helps to identify unique item in RecyclerView list
     * @param autoScroll evaluate scrollTo matched item in case of true value
     * */
    fun item(matcher: Matcher<View>, autoScroll: Boolean = true): UltronRecyclerViewItem {
        waitItemsLoaded()
        return UltronRecyclerViewItem(recyclerViewMatcher, matcher, autoScroll)
    }

    /** @return simple [UltronRecyclerViewItem] at [position]
     * @param position of item in RecyclerView list
     * @param autoScroll evaluate scrollTo item at [position] in case of true value
     * */
    fun item(position: Int, autoScroll: Boolean = true): UltronRecyclerViewItem {
        waitItemsLoaded()
        return UltronRecyclerViewItem(recyclerViewMatcher, position, autoScroll)
    }

    /** @return [UltronRecyclerViewItem] at first position */
    fun firstItem(autoScroll: Boolean = true) = item(0, autoScroll)

    /** @return [UltronRecyclerViewItem] at last position */
    fun lastItem(autoScroll: Boolean = true) : UltronRecyclerViewItem {
        waitItemsLoaded()
        return UltronRecyclerViewItem(recyclerViewMatcher, getLastPosition(), autoScroll)
    }

    /**
     * @return first [UltronRecyclerViewItem] matched [matcher]
     *
     * it could be used if you can't determine unique matcher for item
     *
     * @param matcher determines how to find item
     * @param autoScroll evaluate scrollTo this item in case of true value
     */
    fun firstItemMatched(matcher: Matcher<View>, autoScroll: Boolean = true) = itemMatched(matcher, 0, autoScroll)

    /**
     * @return [UltronRecyclerViewItem] matched [matcher]
     *
     * it could be used if you can't determine unique matcher for item, eg
     *
     * RecyclerView has several matched items. You can get any of them by specifying desired [index] value
     *
     * @param matcher determines how to find items
     * @param index value from 0 to lastIndex of matched items
     * @param autoScroll evaluate scrollTo this item in case of true value
     */
    fun itemMatched(matcher: Matcher<View>, index: Int, autoScroll: Boolean = true): UltronRecyclerViewItem {
        waitItemsLoaded()
        AssertUtils.assertTrue(
            { getItemsAdapterPositionList(matcher).getOrNull(index) != null }, getTimeout(), "RecyclerView($recyclerViewMatcher) has item matched '$matcher' with index $index"
        )
        val position = getItemsAdapterPositionList(matcher).getOrNull(index) ?: throw UltronException("RecyclerViewItem matched $matcher with index $index disappeared")
        return UltronRecyclerViewItem(recyclerViewMatcher, position, autoScroll)
    }

    /**
     * @return last [UltronRecyclerViewItem] matched [matcher]
     *
     * it could be used if you can't determine unique matcher for item
     *
     * @param matcher determines how to find item
     * @param autoScroll evaluate scrollTo this item in case of true value
     */
    fun lastItemMatched(matcher: Matcher<View>, autoScroll: Boolean = true): UltronRecyclerViewItem {
        waitItemsLoaded()
        AssertUtils.assertTrue(
            { getItemsAdapterPositionList(matcher).lastOrNull() != null }, getTimeout(), "RecyclerView($recyclerViewMatcher) has last item matched '$matcher'"
        )
        val position = getItemsAdapterPositionList(matcher).lastOrNull() ?: throw UltronException("Last RecyclerViewItem matched $matcher disappeared")
        return UltronRecyclerViewItem(recyclerViewMatcher, position, autoScroll)
    }

    /**
     * @return RecyclerView representation at the moment of identifying
     *
     * in case of original RecyclerView in app under test is changed you have to call this method again to get an actual info
     * */
    open fun getRecyclerViewList(): RecyclerView {
        recyclerViewMatcher.identifyRecyclerView(recyclerViewIdentifierMatcher())
        return recyclerView ?: throw UltronException("Couldn't find recyclerView with matcher $recyclerViewMatcher")
    }

    /**
     * identify recyclerView items count immediately at the moment of method calling
     *
     * use [assertSize] to make sure the list has correct items count
     *
     * in case the list could be not fully loaded use [waitItemsLoaded] method before calling [getSize]
     */
    open fun getSize() = getRecyclerViewList().adapter?.itemCount ?: 0

    /**
     * specifies a list is empty at the moment of method calling
     *
     * use [assertEmpty] to assert that list has no items
     */
    fun isEmpty() = getSize() == 0

    fun getLastPosition(): Int {
        val position = getSize() - 1
        return if (position >= 0) position else 0
    }
    /**
     * Asserts RecyclerView has no item
     */
    fun assertEmpty(){
        AssertUtils.assertTrue(
            { getSize() == 0 }, recyclerViewOperationsTimeoutMs, "RecyclerView($recyclerViewMatcher) has no items (actual size = ${getSize()})"
        )
    }
    /**
     * Asserts RecyclerView list has [expected] items count during [timeoutMs]
     */
    open fun assertSize(expected: Int) {
        AssertUtils.assertTrue(
            { getSize() == expected }, recyclerViewOperationsTimeoutMs, "RecyclerView($recyclerViewMatcher) size is $expected (actual size = ${getSize()})"
        )
    }

    /**
     * Asserts RecyclerView list has item at [position] during [timeoutMs]
     */
    open fun assertHasItemAtPosition(position: Int) {
        AssertUtils.assertTrue(
            { getSize() >= position }, recyclerViewOperationsTimeoutMs, "Wait RecyclerView($recyclerViewMatcher) size >= $position (actual size = ${getSize()})"
        )
    }

    open fun isDisplayed() = apply { recyclerViewMatcher.withTimeout(getTimeout()).isDisplayed() }
    open fun isNotDisplayed() = apply { recyclerViewMatcher.withTimeout(getTimeout()).isNotDisplayed() }
    open fun doesNotExist() = apply { recyclerViewMatcher.withTimeout(getTimeout()).doesNotExist() }
    open fun isEnabled() = apply { recyclerViewMatcher.withTimeout(getTimeout()).isEnabled() }
    open fun isNotEnabled() = apply { recyclerViewMatcher.withTimeout(getTimeout()).isNotEnabled() }
    open fun hasContentDescription(contentDescription: String) =
        apply { recyclerViewMatcher.withTimeout(getTimeout()).hasContentDescription(contentDescription) }

    open fun hasContentDescription(resourceId: Int) =
        apply { recyclerViewMatcher.withTimeout(getTimeout()).hasContentDescription(resourceId) }

    open fun hasContentDescription(charSequenceMatcher: Matcher<CharSequence>) =
        apply { recyclerViewMatcher.withTimeout(getTimeout()).hasContentDescription(charSequenceMatcher) }

    open fun contentDescriptionContains(text: String) =
        apply { recyclerViewMatcher.withTimeout(getTimeout()).contentDescriptionContains(text) }

    open fun assertMatches(matcher: Matcher<View>) = apply { recyclerViewMatcher.withTimeout(getTimeout()).assertMatches(matcher) }

    /** set timeout for operations with RecyclerView.
     * Note: it doesn't modify [loadTimeoutMs] (waiting a RecyclerView to be loaded)
     * */
    open fun withTimeout(timeoutMs: Long) = apply { recyclerViewOperationsTimeoutMs = timeoutMs }
    open fun withResultHandler(resultHandler: (EspressoOperationResult<UltronEspressoOperation>) -> Unit) =
        recyclerViewMatcher.withResultHandler(resultHandler)

    /**
     * It's waiting while RecyclerView items to be loaded
     * @throws [UltronException] if no item is loaded during [loadTimeoutMs]
     */
    fun waitItemsLoaded(recyclerView: RecyclerView = getRecyclerViewList()) = apply {
        var isLoaded = false
        if (recyclerView.adapter?.itemCount ?: 0 > 0) {
            Log.d("Ultron", "recyclerView.adapter?.itemCount > 0  = " + recyclerView.adapter?.itemCount)
            return@apply
        }
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                Log.d("Ultron", "recyclerView is loaded")
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                isLoaded = true
            }
        })
        val startTime = SystemClock.elapsedRealtime()
        while (!isLoaded && (startTime + loadTimeoutMs > SystemClock.elapsedRealtime())) {}
        if (!isLoaded) throw UltronException("RecyclerView matches '$recyclerViewMatcher' doesn't load any item during $loadTimeoutMs ms")
    }

    /**
     * @return a list of [RecyclerView.ViewHolder] objects those matched with [itemMatcher]
     *
     * Note: only items are displayed on the screen have ViewHolder
     */
    fun getViewHolderList(itemMatcher: Matcher<View>): List<RecyclerView.ViewHolder> {
        val recyclerView = getRecyclerViewList()
        val viewHolderMatcher: Matcher<RecyclerView.ViewHolder> = viewHolderMatcher(itemMatcher)
        val matchedItems: List<MatchedItem> = itemsMatching(recyclerView, viewHolderMatcher)
        val viewHolders = mutableListOf<RecyclerView.ViewHolder>()
        matchedItems.forEach {
            recyclerView.findViewHolderForAdapterPosition(it.position)?.let { vh -> viewHolders.add(vh) }
        }
        return viewHolders
    }

    /**
     * @return positions of all matched items in the list
     */
    fun getItemsAdapterPositionList(itemMatcher: Matcher<View>): List<Int> {
        return itemsMatching(getRecyclerViewList(), viewHolderMatcher(itemMatcher)).map { it.position }
    }

    /**
     * @return [RecyclerView.ViewHolder] object at position [position]
     */
    fun getViewHolderAtPosition(position: Int): RecyclerView.ViewHolder? {
        return getRecyclerViewList().findViewHolderForAdapterPosition(position)
    }

    /**
     * @param itemMatcher describes how to identify item in the list.
     * It shouldn't be unique in  view hierarchy
     * @return the matcher to the exact recyclerView item which it's possible to interact
     */
    internal fun atItem(itemMatcher: Matcher<View>): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var itemView: View? = null

            override fun describeTo(description: Description) {
                description.appendText("RecyclerViewItem recyclerViewMatcher: '$recyclerViewMatcher', itemMatcher: '$itemMatcher'")
            }

            override fun matchesSafely(view: View): Boolean {
                if (itemView == null) itemView = findItemView(itemMatcher, view.rootView)?.itemView
                return if (itemView != null) itemView == view else false
            }
        }
    }

    /**
     * @return matcher to RecyclerView item at position [position]
     */
    internal fun atPosition(position: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var itemView: View? = null
            override fun describeTo(description: Description) {
                description.appendText("RecyclerViewItem recyclerViewMatcher: '$recyclerViewMatcher', itemPosition: '$position'")
            }

            override fun matchesSafely(view: View): Boolean {
                if (itemView == null) itemView = findItemViewAtPosition(position, view.rootView)?.itemView
                return if (itemView != null) itemView == view else false
            }
        }
    }

    /**
     * @param itemMatcher describes how to identify item in the list. It shouldn't be unique in  view hierarchy
     * @param childMatcher describes how to identify child inside item. It shouldn't be unique in scope of RecyclerView list
     * @return matcher to a RecyclerView item child which it's possible to interact
     */
    internal fun atItemChild(itemMatcher: Matcher<View>, childMatcher: Matcher<View>): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var childView: View? = null

            override fun describeTo(description: Description) {
                description.appendText("RecyclerViewItem recyclerViewMatcher: '$recyclerViewMatcher', itemMatcher: '$itemMatcher', childMatcher: '$childMatcher'")
            }

            override fun matchesSafely(view: View): Boolean {
                findItemView(itemMatcher, view.rootView)?.itemView?.let {
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
    internal fun atPositionItemChild(position: Int, childMatcher: Matcher<View>): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var childView: View? = null
            override fun describeTo(description: Description) {
                description.appendText("RecyclerViewItem recyclerViewMatcher: '$recyclerViewMatcher', itemPosition: '$position', childMatcher: '$childMatcher'")
            }

            override fun matchesSafely(view: View): Boolean {
                findItemViewAtPosition(position, view.rootView)?.itemView.let {
                    childView = it?.findChildView(childMatcher)
                }
                return if (childView != null) childView == view else false
            }
        }
    }

    private fun findItemView(itemMatcher: Matcher<View>, rootView: View): RecyclerView.ViewHolder? {
        val recyclerView = rootView.findChildView(recyclerViewMatcher) as RecyclerView
        this.recyclerView = recyclerView
        val viewHolderMatcher: Matcher<RecyclerView.ViewHolder> = viewHolderMatcher(itemMatcher)
        val matchedItem = itemsMatching(recyclerView, viewHolderMatcher, 1).firstOrNull()
        return matchedItem?.let { recyclerView.findViewHolderForAdapterPosition(it.position) }
    }

    private fun findItemViewAtPosition(position: Int, rootView: View): RecyclerView.ViewHolder? {
        val recyclerView = rootView.findChildView(recyclerViewMatcher) as RecyclerView
        this.recyclerView = recyclerView
        return recyclerView.findViewHolderForAdapterPosition(position)
    }

    private fun recyclerViewIdentifierMatcher(): BoundedMatcher<View, RecyclerView> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("identify RecyclerView matches ").appendValue(recyclerViewMatcher)
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

fun withRecyclerView(recyclerViewMatcher: Matcher<View>, loadTimeout: Long = DEFAULT_RECYCLER_VIEW_LOAD_TIMEOUT): UltronRecyclerView {
    return UltronRecyclerView(recyclerViewMatcher, loadTimeout)
}

fun withRecyclerView(@IntegerRes resourceId: Int, loadTimeout: Long = DEFAULT_RECYCLER_VIEW_LOAD_TIMEOUT): UltronRecyclerView {
    return UltronRecyclerView(withId(resourceId), loadTimeout)
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

private fun <T> UltronEspressoInteraction<T>.identifyRecyclerView(matcher: Matcher<View>) {
    executeAssertion(
        UltronEspressoOperation(
            operationBlock = getInteractionAssertionBlock(matches(matcher)),
            name = "Identify RecyclerView matches '${getInteractionMatcher()}'",
            type = EspressoAssertionType.IDENTIFY_RECYCLER_VIEW,
            description = "${EspressoAssertionType.IDENTIFY_RECYCLER_VIEW} during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
        ), resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
    )
}

private fun Matcher<View>.identifyRecyclerView(matcher: Matcher<View>) = UltronEspressoInteraction(onView(this)).identifyRecyclerView(matcher)