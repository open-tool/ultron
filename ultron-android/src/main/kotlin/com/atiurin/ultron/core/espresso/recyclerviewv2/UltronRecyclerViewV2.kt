package com.atiurin.ultron.core.espresso.recyclerviewv2

import android.view.View
import androidx.annotation.IntegerRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.util.TreeIterables
import com.atiurin.ultron.core.config.UltronConfig.Espresso.Companion.RECYCLER_IMPL
import com.atiurin.ultron.core.config.UltronConfig.Espresso.Companion.RECYCLER_VIEW_ITEM_SEARCH_LIMIT
import com.atiurin.ultron.core.config.UltronConfig.Espresso.Companion.RECYCLER_VIEW_LOAD_TIMEOUT
import com.atiurin.ultron.core.config.UltronConfig.Espresso.Companion.RECYCLER_VIEW_OPERATIONS_TIMEOUT
import com.atiurin.ultron.core.config.UltronRecyclerImpl
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import com.atiurin.ultron.core.espresso.assertion.EspressoAssertionType
import com.atiurin.ultron.exceptions.UltronOperationException
import com.atiurin.ultron.extensions.withResultHandler
import com.atiurin.ultron.extensions.withTimeout
import com.atiurin.ultron.utils.AssertUtils
import com.atiurin.ultron.utils.runOnUiThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher

open class UltronRecyclerViewV2(
    val recyclerViewMatcher: Matcher<View>,
    val loadTimeoutMs: Long = RECYCLER_VIEW_LOAD_TIMEOUT,
    private val itemSearchLimit: Int = RECYCLER_VIEW_ITEM_SEARCH_LIMIT,
    private var operationTimeoutMs: Long = RECYCLER_VIEW_OPERATIONS_TIMEOUT,
    private val recyclerImpl: UltronRecyclerImpl = RECYCLER_IMPL
) {
    private var recyclerView: RecyclerView? = null

    /**
     * @return current [UltronRecyclerViewV2] operations timeout
     */
    fun getTimeout() = operationTimeoutMs

    /** @return [UltronRecyclerViewItemV2] subclass instance matches [matcher]
     *
     * Note: never add inner modifier to [T] class
     *
     * Note: [T] class should have a constructor without parameters, eg
     *
     *    class SomeRecyclerViewItem : UltronRecyclerViewItemV2(){...}
     * */
    inline fun <reified T : UltronRecyclerViewItemV2> getItem(
        matcher: Matcher<View>,
        autoScroll: Boolean = true,
        scrollOffset: Int = 0
    ): T {
        waitItemsLoaded()
        return UltronRecyclerViewItemV2.getInstance(this, matcher, autoScroll, scrollOffset)
    }

    /** @return [UltronRecyclerViewItemV2] subclass instance at position [position]
     *
     * Note: never add inner modifier to [T] class
     *
     * Note: [T] class should have a constructor without parameters, eg
     *
     *    class SomeRecyclerViewItem : UltronRecyclerViewItemV2(){...}
     * */
    inline fun <reified T : UltronRecyclerViewItemV2> getItem(
        position: Int,
        autoScroll: Boolean = true,
        scrollOffset: Int = 0
    ): T {
        waitItemsLoaded()
        return UltronRecyclerViewItemV2.getInstance(this, position, autoScroll, scrollOffset)
    }

    /** @return [UltronRecyclerViewItemV2] subclass instance at first position
     *
     * Note: never add inner modifier to [T] class
     *
     * Note: [T] class should have a constructor without parameters, eg
     *
     *    class SomeRecyclerViewItem : UltronRecyclerViewItemV2(){...}
     * */
    inline fun <reified T : UltronRecyclerViewItemV2> getFirstItem(autoScroll: Boolean = true) =
        getItem<T>(0, autoScroll)

    /** @return [UltronRecyclerViewItemV2] subclass instance at last position
     *
     * Note: never add inner modifier to [T] class
     *
     * Note: [T] class should have a constructor without parameters, eg
     *
     *    class SomeRecyclerViewItem : UltronRecyclerViewItemV2(){...}
     * */
    inline fun <reified T : UltronRecyclerViewItemV2> getLastItem(autoScroll: Boolean = true): T {
        waitItemsLoaded()
        return UltronRecyclerViewItemV2.getInstance(this, getSize() - 1, autoScroll)
    }

    /**
     * @return first [T] matched [matcher]
     *
     * it could be used if you can't determine unique matcher for item
     *
     * @param matcher determines how to find item
     * @param autoScroll evaluate scrollTo this item in case of true value
     */
    inline fun <reified T : UltronRecyclerViewItemV2> getFirstItemMatched(
        matcher: Matcher<View>,
        autoScroll: Boolean = true,
        scrollOffset: Int = 0
    ) = getItemMatched<T>(matcher, 0, autoScroll, scrollOffset)

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
    inline fun <reified T : UltronRecyclerViewItemV2> getItemMatched(
        matcher: Matcher<View>,
        index: Int,
        autoScroll: Boolean = true,
        scrollOffset: Int = 0
    ): T {
        waitItemsLoaded()
        val position = AssertUtils.assertTrueAndReturnValue(
            valueBlock = { getItemAdapterPositionAtIndex(matcher, index) },
            assertionBlock = { value -> value >= 0 },
            timeoutMs = getTimeout(),
            desc = "RecyclerView($recyclerViewMatcher) has item matched '$matcher' with index $index"
        )
        return UltronRecyclerViewItemV2.getInstance(this, position, autoScroll, scrollOffset)
    }

    /**
     * @return last [T] matched [matcher]
     *
     * it could be used if you can't determine unique matcher for item
     *
     * @param matcher determines how to find item
     * @param autoScroll evaluate scrollTo this item in case of true value
     */
    inline fun <reified T : UltronRecyclerViewItemV2> getLastItemMatched(
        matcher: Matcher<View>,
        autoScroll: Boolean = true,
        scrollOffset: Int = 0
    ): T {
        waitItemsLoaded()
        val position = AssertUtils.assertTrueAndReturnValue(
            valueBlock = { getItemsAdapterPositionList(matcher).lastOrNull() ?: -1 },
            assertionBlock = { value -> value >= 0 },
            timeoutMs = getTimeout(),
            desc = "RecyclerView($recyclerViewMatcher) has last item matched '$matcher'"
        )
        return UltronRecyclerViewItemV2.getInstance(this, position, autoScroll, scrollOffset)
    }

    /** @return simple [UltronRecyclerViewItemV2] matches '[matcher]'
     * @param matcher helps to identify unique item in RecyclerView list
     * @param autoScroll evaluate scrollTo matched item in case of true value
     * */
    fun item(
        matcher: Matcher<View>,
        autoScroll: Boolean = true,
        scrollOffset: Int = 0
    ): UltronRecyclerViewItemV2 {
        waitItemsLoaded()
        return UltronRecyclerViewItemV2(this, matcher, autoScroll, scrollOffset)
    }

    /** @return simple [UltronRecyclerViewItemV2] at [position]
     * @param position of item in RecyclerView list
     * @param autoScroll evaluate scrollTo item at [position] in case of true value
     * */
    fun item(
        position: Int,
        autoScroll: Boolean = true,
        scrollOffset: Int = 0
    ): UltronRecyclerViewItemV2 {
        waitItemsLoaded()
        return UltronRecyclerViewItemV2(this, position, autoScroll, scrollOffset)
    }

    /** @return [UltronRecyclerViewItemV2] at first position */
    fun firstItem(autoScroll: Boolean = true) = item(0, autoScroll)

    /** @return [UltronRecyclerViewItemV2] at last position */
    fun lastItem(autoScroll: Boolean = true, scrollOffset: Int = 0): UltronRecyclerViewItemV2 {
        waitItemsLoaded()
        return UltronRecyclerViewItemV2(this, getLastPosition(), autoScroll, scrollOffset)
    }

    /**
     * @return first [UltronRecyclerViewItemV2] matched [matcher]
     *
     * it could be used if you can't determine unique matcher for item
     *
     * @param matcher determines how to find item
     * @param autoScroll evaluate scrollTo this item in case of true value
     */
    fun firstItemMatched(
        matcher: Matcher<View>,
        autoScroll: Boolean = true,
        scrollOffset: Int = 0
    ) =
        itemMatched(matcher, 0, autoScroll, scrollOffset)

    /**
     * @return [UltronRecyclerViewItemV2] matched [matcher]
     *
     * it could be used if you can't determine unique matcher for item, eg
     *
     * RecyclerView has several matched items. You can get any of them by specifying desired [index] value
     *
     * @param matcher determines how to find items
     * @param index value from 0 to lastIndex of matched items
     * @param autoScroll evaluate scrollTo this item in case of true value
     */
    fun itemMatched(
        matcher: Matcher<View>,
        index: Int,
        autoScroll: Boolean = true,
        scrollOffset: Int = 0
    ): UltronRecyclerViewItemV2 {
        waitItemsLoaded()
        val position = AssertUtils.assertTrueAndReturnValue(
            valueBlock = { getItemAdapterPositionAtIndex(matcher, index) },
            assertionBlock = { value -> value >= 0 },
            timeoutMs = getTimeout(),
            desc = "RecyclerView($recyclerViewMatcher) has item matched '$matcher' with index $index"
        )
        return UltronRecyclerViewItemV2(this, position, autoScroll, scrollOffset)
    }

    /**
     * @return last [UltronRecyclerViewItemV2] matched [matcher]
     *
     * it could be used if you can't determine unique matcher for item
     *
     * @param matcher determines how to find item
     * @param autoScroll evaluate scrollTo this item in case of true value
     */
    fun lastItemMatched(
        matcher: Matcher<View>,
        autoScroll: Boolean = true,
        scrollOffset: Int = 0
    ): UltronRecyclerViewItemV2 {
        waitItemsLoaded()
        val position = AssertUtils.assertTrueAndReturnValue(
            valueBlock = { getItemsAdapterPositionList(matcher).lastOrNull() ?: -1 },
            assertionBlock = { value -> value >= 0 },
            timeoutMs = getTimeout(),
            desc = "RecyclerView($recyclerViewMatcher) has last item matched '$matcher'"
        )
        return UltronRecyclerViewItemV2(this, position, autoScroll, scrollOffset)
    }

    /**
     * @return RecyclerView representation at the moment of identifying
     *
     * in case of original RecyclerView in app under test is changed you have to call this method again to get an actual info
     */
    open fun getRecyclerViewList(): RecyclerView {
        recyclerViewMatcher.identifyRecyclerView(recyclerViewIdentifierMatcher())
        return recyclerView
            ?: throw UltronOperationException("Couldn't find recyclerView with matcher $recyclerViewMatcher")
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
    fun assertEmpty() {
        AssertUtils.assertTrue(
            { getSize() == 0 }, operationTimeoutMs,
            { "RecyclerView($recyclerViewMatcher) has no items (actual size = ${getSize()})" }
        )
    }

    /**
     * Asserts RecyclerView has any item
     */
    fun assertNotEmpty() {
        AssertUtils.assertTrue(
            { getSize() > 0 }, operationTimeoutMs,
            { "RecyclerView($recyclerViewMatcher) is NOT empty" }
        )
    }

    /**
     * Asserts RecyclerView list size matches the expected condition during [operationTimeoutMs]
     * @param expected The expected size value to compare against
     * @param comparison The type of size comparison to perform
     */
    open fun assertSize(expected: Int, comparison: SizeComparison = SizeComparison.EQUAL) {
        val condition: (Int) -> Boolean = when (comparison) {
            SizeComparison.EQUAL -> { size -> size == expected }
            SizeComparison.GREATER_THAN -> { size -> size > expected }
            SizeComparison.LESS_THAN -> { size -> size < expected }
            SizeComparison.GREATER_THAN_OR_EQUAL -> { size -> size >= expected }
            SizeComparison.LESS_THAN_OR_EQUAL -> { size -> size <= expected }
        }

        val operatorSymbol = when (comparison) {
            SizeComparison.EQUAL -> "="
            SizeComparison.GREATER_THAN -> ">"
            SizeComparison.LESS_THAN -> "<"
            SizeComparison.GREATER_THAN_OR_EQUAL -> ">="
            SizeComparison.LESS_THAN_OR_EQUAL -> "<="
        }

        AssertUtils.assertTrue(
            { condition(getSize()) },
            operationTimeoutMs,
            { "RecyclerView($recyclerViewMatcher) size expected to be $operatorSymbol $expected (actual size = ${getSize()})" }
        )
    }

    /**
     * Asserts RecyclerView list has item at [position] during [operationTimeoutMs]
     */
    open fun assertHasItemAtPosition(position: Int) {
        AssertUtils.assertTrue(
            { getSize() >= position }, operationTimeoutMs,
            { "Wait RecyclerView($recyclerViewMatcher) size >= $position (actual size = ${getSize()})" }
        )
    }

    /**
     * Asserts RecyclerView list hasn't item matched with [matcher].
     * In case of item not exist it returns immediately.
     * If, for some reason, item appears later. These assertion can give you false positive result
     */
    open fun assertItemNotExistImmediately(matcher: Matcher<View>, timeoutMs: Long) {
        waitItemsLoaded()
        AssertUtils.assertTrue(
            { !isItemExist(matcher) },
            timeoutMs,
            { "RecyclerView($recyclerViewMatcher) has no item matched '$matcher'" }
        )
    }

    /**
     * Asserts RecyclerView list hasn't item matched with [matcher] during specified [timeoutMs].
     * In case item appears in list during [timeoutMs] an exception is going to be thrown.
     * Note: In positive scenario this assert takes [timeoutMs]
     */
    open fun assertItemNotExist(matcher: Matcher<View>, timeoutMs: Long) {
        waitItemsLoaded()
        AssertUtils.assertTrueWhileTime(
            { !isItemExist(matcher) },
            timeoutMs,
            "RecyclerView($recyclerViewMatcher) has no item matched '$matcher'"
        )
    }

    open fun isDisplayed() = apply { recyclerViewMatcher.withTimeout(getTimeout()).isDisplayed() }
    open fun isNotDisplayed() =
        apply { recyclerViewMatcher.withTimeout(getTimeout()).isNotDisplayed() }

    open fun doesNotExist() = apply { recyclerViewMatcher.withTimeout(getTimeout()).doesNotExist() }
    open fun isEnabled() = apply { recyclerViewMatcher.withTimeout(getTimeout()).isEnabled() }
    open fun isNotEnabled() = apply { recyclerViewMatcher.withTimeout(getTimeout()).isNotEnabled() }
    open fun hasContentDescription(contentDescription: String) =
        apply {
            recyclerViewMatcher.withTimeout(getTimeout()).hasContentDescription(contentDescription)
        }

    open fun hasContentDescription(resourceId: Int) =
        apply { recyclerViewMatcher.withTimeout(getTimeout()).hasContentDescription(resourceId) }

    open fun hasContentDescription(charSequenceMatcher: Matcher<CharSequence>) =
        apply {
            recyclerViewMatcher.withTimeout(getTimeout()).hasContentDescription(charSequenceMatcher)
        }

    open fun contentDescriptionContains(text: String) =
        apply { recyclerViewMatcher.withTimeout(getTimeout()).contentDescriptionContains(text) }

    open fun assertMatches(matcher: Matcher<View>) =
        apply { recyclerViewMatcher.withTimeout(getTimeout()).assertMatches(matcher) }

    fun scrollToItem(
        itemMatcher: Matcher<View>,
        searchLimit: Int = this.itemSearchLimit,
        offset: Int = 0
    ) = apply {
        recyclerViewMatcher.withTimeout(getTimeout()).perform(
            viewAction = RecyclerViewScrollActionV2(itemMatcher, searchLimit, offset),
            description = "Scroll RecyclerView '$recyclerViewMatcher' to item = '$itemMatcher' with searchLimit = $searchLimit and offset = $offset"
        )
    }

    @Deprecated("Use scrollToItem(itemMatcher, searchLimit, offset)")
    fun scrollToIem(
        itemMatcher: Matcher<View>,
        searchLimit: Int = this.itemSearchLimit,
        offset: Int = 0
    ) =
        scrollToItem(itemMatcher, searchLimit, offset)

    fun scrollToItem(position: Int, offset: Int = 0) {
        assertHasItemAtPosition(position)
        val positionToScroll = position + offset
        recyclerViewMatcher.withTimeout(getTimeout()).perform(
            viewAction = ScrollToPositionViewAction(positionToScroll),
            description = "RecyclerViewActions scrollToPosition $position with offset = $offset"
        )
    }

    /** set timeout for operations with RecyclerView.
     * Note: it doesn't modify [loadTimeoutMs] (waiting a RecyclerView to be loaded)
     */
    open fun withTimeout(timeoutMs: Long) =
        UltronRecyclerViewV2(
            this.recyclerViewMatcher,
            this.loadTimeoutMs,
            this.itemSearchLimit,
            operationTimeoutMs = timeoutMs
        )

    open fun withResultHandler(resultHandler: (EspressoOperationResult<UltronEspressoOperation>) -> Unit) =
        recyclerViewMatcher.withResultHandler(resultHandler)

    fun isItemExist(matcher: Matcher<View>): Boolean {
        return getItemAdapterPositionAtIndex(matcher, 0) >= 0
    }

    /**
     * It's waiting while RecyclerView items to be loaded
     * @param recyclerView RecyclerView to monitor
     * @param minItemsCount Minimum number of items to successfully load
     * @throws @throws [UltronOperationException] if no item is loaded during [loadTimeoutMs]
     */
    fun waitItemsLoaded(
        recyclerView: RecyclerView = getRecyclerViewList(),
        minItemsCount: Int = 1
    ) = apply {
        if ((recyclerView.adapter?.itemCount ?: 0) >= minItemsCount &&
            recyclerView.childCount >= minItemsCount
        ) { return@apply }

        runBlocking {
            try {
                kotlinx.coroutines.withTimeout(loadTimeoutMs) {
                    while (true) {
                        if ((recyclerView.adapter?.itemCount ?: 0) >= minItemsCount &&
                            recyclerView.childCount >= minItemsCount
                        ) {
                            return@withTimeout
                        }
                        delay(50)
                    }
                }
            } catch (_: TimeoutCancellationException) {
                throw UltronOperationException(
                    "RecyclerView failed to load $minItemsCount items in $loadTimeoutMs ms. " +
                            "Current state: adapter.items = ${recyclerView.adapter?.itemCount}, " +
                            "visible.items = ${recyclerView.childCount}"
                )
            }
        }
    }

    /**
     * @return a list of [RecyclerView.ViewHolder] objects those matched with [itemMatcher]
     *
     * Note: only items are displayed on the screen have ViewHolder
     */
    fun getViewHolderList(itemMatcher: Matcher<View>): List<RecyclerView.ViewHolder> {
        val recyclerView = getRecyclerViewList()
        val viewHolderMatcher: Matcher<RecyclerView.ViewHolder> = viewHolderMatcher(itemMatcher)
        val matchedItems: List<MatchedItem> =
            itemsMatching(recyclerView, viewHolderMatcher, itemSearchLimit = itemSearchLimit)
        val viewHolders = mutableListOf<RecyclerView.ViewHolder>()
        matchedItems.forEach {
            recyclerView.findViewHolderForAdapterPosition(it.position)
                ?.let { vh -> viewHolders.add(vh) }
        }
        return viewHolders
    }

    /**
     * @return positions of all matched items in the list
     * @param limitAmountOfMatchedItems restricts the size of list to be returned
     *
     * eg RecyclerView contains 100 matched items, [limitAmountOfMatchedItems] = 10 => this method will return positions of first 10 matched RecyclerView items
     */
    fun getItemsAdapterPositionList(
        itemMatcher: Matcher<View>,
        limitAmountOfMatchedItems: Int = -1
    ): List<Int> {
        return itemsMatching(
            getRecyclerViewList(),
            viewHolderMatcher(itemMatcher),
            limitAmountOfMatchedItems,
            itemSearchLimit
        ).map { it.position }
    }

    /**
     * @return position of matched item with [index]
     * @return -1 if there is no matched item with [index]
     */
    fun getItemAdapterPositionAtIndex(itemMatcher: Matcher<View>, index: Int): Int {
        val items = getItemsAdapterPositionList(itemMatcher, index + 1)
        return if (items.size - 1 < index) -1 else items[index]
    }

    /**
     * @return [RecyclerView.ViewHolder] object at position [position]
     */
    fun getViewHolderAtPosition(position: Int): RecyclerView.ViewHolder? {
        return getRecyclerViewList().findViewHolderForAdapterPosition(position)
    }

    /**
     * @param itemMatcher describes how to identify item in the list.
     * It shouldn't be unique in view hierarchy
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
                if (itemView == null) itemView =
                    findItemViewAtPosition(position, view.rootView)?.itemView
                return if (itemView != null) itemView == view else false
            }
        }
    }

    /**
     * @param itemMatcher describes how to identify item in the list. It shouldn't be unique in view hierarchy
     * @param childMatcher describes how to identify child inside item. It shouldn't be unique in scope of RecyclerView list
     * @return matcher to a RecyclerView item child which it's possible to interact
     */
    internal fun atItemChild(
        itemMatcher: Matcher<View>,
        childMatcher: Matcher<View>
    ): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var childView: View? = null

            override fun describeTo(description: Description) {
                description.appendText("RecyclerViewItem of '$recyclerViewMatcher', itemMatcher: '$itemMatcher', childMatcher: '$childMatcher'")
            }

            override fun matchesSafely(view: View): Boolean {
                return when(recyclerImpl) {
                    UltronRecyclerImpl.STANDART -> {
                        findItemView(itemMatcher, view.rootView)?.itemView?.let {
                            childView = it.findChildView(childMatcher)
                        }
                        return if (childView != null) childView == view else false
                    }

                    UltronRecyclerImpl.PERFORMANCE -> {
                        if (childMatcher.matches(view)) {
                            isDescendantOfA(atItem(itemMatcher)).matches(view)
                        } else false
                    }
                }
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
                description.appendText("RecyclerViewItem of '$recyclerViewMatcher', itemPosition: '$position', childMatcher: '$childMatcher'")
            }

            override fun matchesSafely(view: View): Boolean {
                return when(recyclerImpl) {
                    UltronRecyclerImpl.STANDART -> {
                        findItemViewAtPosition(position, view.rootView)?.itemView.let {
                            childView = it?.findChildView(childMatcher)
                        }
                        return if (childView != null) childView == view else false
                    }

                    UltronRecyclerImpl.PERFORMANCE -> {
                        if (childMatcher.matches(view)) {
                            isDescendantOfA(atPosition(position)).matches(view)
                        } else false
                    }
                }
            }
        }
    }

    private fun findItemView(itemMatcher: Matcher<View>, rootView: View): RecyclerView.ViewHolder? {
        val recyclerView =
            rootView.findChildView(recyclerViewMatcher) as? RecyclerView ?: return null
        this.recyclerView = recyclerView
        val viewHolderMatcher: Matcher<RecyclerView.ViewHolder> = viewHolderMatcher(itemMatcher)
        val matchedItem =
            itemsMatching(recyclerView, viewHolderMatcher, 1, itemSearchLimit).firstOrNull()
        return matchedItem?.let { recyclerView.findViewHolderForAdapterPosition(it.position) }
    }

    private fun findItemViewAtPosition(position: Int, rootView: View): RecyclerView.ViewHolder? {
        val recyclerView =
            rootView.findChildView(recyclerViewMatcher) as? RecyclerView ?: return null
        this.recyclerView = recyclerView
        return recyclerView.findViewHolderForAdapterPosition(position)
    }

    private fun recyclerViewIdentifierMatcher(): BoundedMatcher<View, RecyclerView> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("identify RecyclerView matches ")
                    .appendValue(recyclerViewMatcher)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                if (recyclerViewMatcher.matches(view)) {
                    recyclerView = view
                }
                return recyclerView == view
            }
        }
    }

    enum class SizeComparison {
        EQUAL,
        GREATER_THAN,
        LESS_THAN,
        GREATER_THAN_OR_EQUAL,
        LESS_THAN_OR_EQUAL
    }
}

fun withRecyclerViewV2(
    recyclerViewMatcher: Matcher<View>,
    loadTimeout: Long = RECYCLER_VIEW_LOAD_TIMEOUT,
    itemSearchLimit: Int = RECYCLER_VIEW_ITEM_SEARCH_LIMIT,
    operationsTimeoutMs: Long = RECYCLER_VIEW_OPERATIONS_TIMEOUT,
    recyclerImpl: UltronRecyclerImpl = RECYCLER_IMPL
): UltronRecyclerViewV2 {
    return UltronRecyclerViewV2(
        recyclerViewMatcher,
        loadTimeout,
        itemSearchLimit,
        operationsTimeoutMs,
        recyclerImpl
    )
}

fun withRecyclerViewV2(
    @IntegerRes resourceId: Int,
    loadTimeout: Long = RECYCLER_VIEW_LOAD_TIMEOUT,
    itemSearchLimit: Int = RECYCLER_VIEW_ITEM_SEARCH_LIMIT,
    operationsTimeoutMs: Long = RECYCLER_VIEW_OPERATIONS_TIMEOUT,
    recyclerImpl: UltronRecyclerImpl = RECYCLER_IMPL
): UltronRecyclerViewV2 {
    return UltronRecyclerViewV2(
        withId(resourceId),
        loadTimeout,
        itemSearchLimit,
        operationsTimeoutMs,
        recyclerImpl
    )
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
    this.executeAssertion(
        UltronEspressoOperation(
            operationBlock = getInteractionAssertionBlock(matches(matcher)),
            name = "Identify RecyclerView matches '${getInteractionMatcher()}'",
            type = EspressoAssertionType.IDENTIFY_RECYCLER_VIEW,
            description = "${EspressoAssertionType.IDENTIFY_RECYCLER_VIEW} during $timeoutMs ms",
            timeoutMs = getAssertionTimeout()
        )
    )
}

private fun Matcher<View>.identifyRecyclerView(matcher: Matcher<View>): Unit =
    UltronEspressoInteraction(onView(this)).identifyRecyclerView(matcher)

private class ScrollToPositionViewAction(
    private val position: Int
) : ViewAction {
    override fun getConstraints(): Matcher<View> {
        return allOf(isAssignableFrom(RecyclerView::class.java), isDisplayed())
    }

    override fun getDescription(): String {
        return "scroll RecyclerView to position: $position"
    }

    override fun perform(uiController: UiController, view: View) {
        val recyclerView = view as RecyclerView
        recyclerView.instantScrollToPosition(position, 0.5f)
        CoroutineScope(Dispatchers.Main).launch { delay(400) }
    }
}

class RecyclerViewScrollActionV2(
    private val itemMatcher: Matcher<View>,
    private val itemSearchLimit: Int = -1,
    private val offset: Int = 0
) : ViewAction {

    override fun getConstraints(): Matcher<View> {
        return allOf(
            isAssignableFrom(RecyclerView::class.java),
            isDisplayed()
        )
    }

    override fun getDescription(): String {
        return "Scroll RecyclerView to item $itemMatcher${if (itemSearchLimit >= 0) " with offset = $offset and search limit = $itemSearchLimit" else ""}"
    }

    override fun perform(uiController: UiController, view: View) {

        val recyclerView = view as RecyclerView
        val viewHolderMatcher: Matcher<RecyclerView.ViewHolder> = viewHolderMatcher(itemMatcher)

        val matchedItem =
            itemsMatching(recyclerView, viewHolderMatcher, 1, itemSearchLimit).firstOrNull()
                ?: throw UltronOperationException("The scroll action could not be performed because no matching element was found using the matcher: $itemMatcher")

        val finalPositionToScroll = matchedItem.position + offset

        recyclerView.instantScrollToPosition(finalPositionToScroll, 0.5f)
        runBlocking { delay(200) }
    }
}

internal class MatchedItem(val position: Int, val description: String) {

    override fun toString(): String {
        return description
    }
}

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

private fun RecyclerView.instantScrollToPosition(
    position: Int,
    snapPreferredSide: Float = 0f
) {
    val adjustedPos = position.coerceIn(0, (adapter?.itemCount ?: 1) - 1)

    runOnUiThread {
        layoutManager?.scrollToPosition(adjustedPos)

        post {
            layoutManager?.apply {
                findViewByPosition(adjustedPos)?.let { targetView ->
                    val parentExtent = if (canScrollVertically()) height else width
                    val childExtent =
                        if (canScrollVertically()) targetView.height else targetView.width

                    when {
                        childExtent > parentExtent -> {
                            if (canScrollVertically()) scrollBy(0, targetView.top)
                            else scrollBy(targetView.left, 0)
                        }

                        else -> {
                            val snapOffset = (parentExtent - childExtent) * snapPreferredSide
                            if (canScrollVertically()) {
                                scrollBy(0, targetView.top - snapOffset.toInt())
                            } else {
                                scrollBy(targetView.left - snapOffset.toInt(), 0)
                            }
                        }
                    }
                }
            }
        }
    }
}