package com.atiurin.ultron.core.espresso.recyclerview

import android.os.SystemClock
import android.view.View
import androidx.annotation.IntegerRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.atiurin.ultron.core.config.UltronConfig.Espresso.Companion.RECYCLER_VIEW_IMPLEMENTATION
import com.atiurin.ultron.core.config.UltronConfig.Espresso.Companion.RECYCLER_VIEW_ITEM_SEARCH_LIMIT
import com.atiurin.ultron.core.config.UltronConfig.Espresso.Companion.RECYCLER_VIEW_LOAD_TIMEOUT
import com.atiurin.ultron.core.config.UltronConfig.Espresso.Companion.RECYCLER_VIEW_OPERATIONS_TIMEOUT
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import com.atiurin.ultron.core.espresso.assertion.EspressoAssertionType
import com.atiurin.ultron.exceptions.UltronOperationException
import com.atiurin.ultron.extensions.*
import com.atiurin.ultron.utils.AssertUtils
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 *  Provides a set of interactions with RecyclerView list.
 *  @param recyclerViewInteraction help ot identify RecyclerView inside view hierarchy
 *  @param loadTimeoutMs specifies a time of waiting while RecyclerView items will be loaded
 *  @param itemSearchLimit set an amount of RecyclerView items to be researched for target item. There is no limit by default
 *  [itemSearchLimit] is applied for matcher search. If you're looking for RecyclerView item by position it isn't used.
 *  @param operationTimeoutMs specifies a timeout for actions and assertions on [UltronRecyclerView]. [UltronRecyclerViewItem] has it own timeout.
 *  @param recyclerImpl specifies a recycler item child matcher implementation
 */
open class UltronRecyclerView(
    val recyclerViewInteraction: UltronEspressoInteraction<ViewInteraction>,
    val loadTimeoutMs: Long = RECYCLER_VIEW_LOAD_TIMEOUT,
    private val itemSearchLimit: Int = RECYCLER_VIEW_ITEM_SEARCH_LIMIT,
    private var operationTimeoutMs: Long = RECYCLER_VIEW_OPERATIONS_TIMEOUT,
    private val recyclerImpl: UltronRecyclerViewImpl = RECYCLER_VIEW_IMPLEMENTATION
) {
    constructor(
        recyclerViewMatcher: Matcher<View>,
        loadTimeoutMs: Long = RECYCLER_VIEW_LOAD_TIMEOUT,
        itemSearchLimit: Int = RECYCLER_VIEW_ITEM_SEARCH_LIMIT,
        operationTimeoutMs: Long = RECYCLER_VIEW_OPERATIONS_TIMEOUT,
        recyclerImpl: UltronRecyclerViewImpl = RECYCLER_VIEW_IMPLEMENTATION
    ): this(
        recyclerViewInteraction = UltronEspressoInteraction(onView(recyclerViewMatcher)),
        loadTimeoutMs = loadTimeoutMs,
        itemSearchLimit = itemSearchLimit,
        operationTimeoutMs = operationTimeoutMs,
        recyclerImpl = recyclerImpl
    )

    val recyclerViewMatcher: Matcher<View>
        get() = recyclerViewInteraction.getInteractionMatcher() ?: throw UltronOperationException("RecyclerView matcher is not specified")

    private var recyclerView: RecyclerView? = null

    /**
     * @return current [UltronRecyclerView] operations timeout
     */
    fun getTimeout() = operationTimeoutMs

    /** @return [UltronRecyclerViewItem] subclass instance matches [matcher]
     *
     * Note: never add inner modifier to [T] class
     *
     * Note: [T] class should have a constructor without parameters, eg
     *
     *    class SomeRecyclerViewItem : UltronRecyclerViewItem(){...}
     * */
    inline fun <reified T : UltronRecyclerViewItem> getItem(
        matcher: Matcher<View>,
        autoScroll: Boolean = true,
        scrollOffset: Int = 0
    ): T {
        waitItemsLoaded()
        return UltronRecyclerViewItem.getInstance(this, UltronEspressoInteraction(onView(matcher)), autoScroll, scrollOffset)
    }

    inline fun <reified T : UltronRecyclerViewItem> getItem(
        interaction: UltronEspressoInteraction<ViewInteraction>,
        autoScroll: Boolean = true,
        scrollOffset: Int = 0
    ): T {
        waitItemsLoaded()
        return UltronRecyclerViewItem.getInstance(this, interaction, autoScroll, scrollOffset)
    }

    /** @return [UltronRecyclerViewItem] subclass instance at position [position]
     *
     * Note: never add inner modifier to [T] class
     *
     * Note: [T] class should have a constructor without parameters, eg
     *
     *    class SomeRecyclerViewItem : UltronRecyclerViewItem(){...}
     * */
    inline fun <reified T : UltronRecyclerViewItem> getItem(
        position: Int,
        autoScroll: Boolean = true,
        scrollOffset: Int = 0
    ): T {
        waitItemsLoaded()
        return UltronRecyclerViewItem.getInstance(this, position, autoScroll, scrollOffset)
    }

    /** @return [UltronRecyclerViewItem] subclass instance at first position
     *
     * Note: never add inner modifier to [T] class
     *
     * Note: [T] class should have a constructor without parameters, eg
     *
     *    class SomeRecyclerViewItem : UltronRecyclerViewItem(){...}
     * */
    inline fun <reified T : UltronRecyclerViewItem> getFirstItem(autoScroll: Boolean = true) =
        getItem<T>(0, autoScroll)

    /** @return [UltronRecyclerViewItem] subclass instance at last position
     *
     * Note: never add inner modifier to [T] class
     *
     * Note: [T] class should have a constructor without parameters, eg
     *
     *    class SomeRecyclerViewItem : UltronRecyclerViewItem(){...}
     * */
    inline fun <reified T : UltronRecyclerViewItem> getLastItem(autoScroll: Boolean = true): T {
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
    inline fun <reified T : UltronRecyclerViewItem> getFirstItemMatched(
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
    inline fun <reified T : UltronRecyclerViewItem> getItemMatched(
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
            desc = "RecyclerView(${recyclerViewInteraction.elementInfo.name}) has item matched '$matcher' with index $index"
        )
        return UltronRecyclerViewItem.getInstance(this, position, autoScroll, scrollOffset)
    }

    /**
     * @return last [T] matched [matcher]
     *
     * it could be used if you can't determine unique matcher for item
     *
     * @param matcher determines how to find item
     * @param autoScroll evaluate scrollTo this item in case of true value
     */
    inline fun <reified T : UltronRecyclerViewItem> getLastItemMatched(
        matcher: Matcher<View>,
        autoScroll: Boolean = true,
        scrollOffset: Int = 0
    ): T {
        waitItemsLoaded()
        val position = AssertUtils.assertTrueAndReturnValue(
            valueBlock = { getItemsAdapterPositionList(matcher).lastOrNull() ?: -1 },
            assertionBlock = { value -> value >= 0 },
            timeoutMs = getTimeout(),
            desc = "RecyclerView(${recyclerViewInteraction.elementInfo.name}) has last item matched '$matcher'"
        )
        return UltronRecyclerViewItem.getInstance(this, position, autoScroll, scrollOffset)
    }

    /** @return simple [UltronRecyclerViewItem] matches '[matcher]'
     * @param matcher helps to identify unique item in RecyclerView list
     * @param autoScroll evaluate scrollTo matched item in case of true value
     * */
    fun item(matcher: Matcher<View>, autoScroll: Boolean = true, scrollOffset: Int = 0): UltronRecyclerViewItem {
        waitItemsLoaded()
        return UltronRecyclerViewItem(this, matcher, autoScroll, scrollOffset)
    }

    /** @return simple [UltronRecyclerViewItem] at [position]
     * @param position of item in RecyclerView list
     * @param autoScroll evaluate scrollTo item at [position] in case of true value
     * */
    fun item(position: Int, autoScroll: Boolean = true, scrollOffset: Int = 0): UltronRecyclerViewItem {
        waitItemsLoaded()
        return UltronRecyclerViewItem(this, position, autoScroll, scrollOffset)
    }

    /** @return [UltronRecyclerViewItem] at first position */
    fun firstItem(autoScroll: Boolean = true) = item(0, autoScroll)

    /** @return [UltronRecyclerViewItem] at last position */
    fun lastItem(autoScroll: Boolean = true, scrollOffset: Int = 0): UltronRecyclerViewItem {
        waitItemsLoaded()
        return UltronRecyclerViewItem(this, getLastPosition(), autoScroll, scrollOffset)
    }

    /**
     * @return first [UltronRecyclerViewItem] matched [matcher]
     *
     * it could be used if you can't determine unique matcher for item
     *
     * @param matcher determines how to find item
     * @param autoScroll evaluate scrollTo this item in case of true value
     */
    fun firstItemMatched(matcher: Matcher<View>, autoScroll: Boolean = true, scrollOffset: Int = 0) =
        itemMatched(matcher, 0, autoScroll, scrollOffset)

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
    fun itemMatched(
        matcher: Matcher<View>,
        index: Int,
        autoScroll: Boolean = true,
        scrollOffset: Int = 0
    ): UltronRecyclerViewItem {
        waitItemsLoaded()
        val position = AssertUtils.assertTrueAndReturnValue(
            valueBlock = { getItemAdapterPositionAtIndex(matcher, index) },
            assertionBlock = { value -> value >= 0 },
            timeoutMs = getTimeout(),
            desc = "RecyclerView(${recyclerViewInteraction.elementInfo.name}) has item matched '$matcher' with index $index"
        )
        return UltronRecyclerViewItem(this, position, autoScroll, scrollOffset)
    }

    /**
     * @return last [UltronRecyclerViewItem] matched [matcher]
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
    ): UltronRecyclerViewItem {
        waitItemsLoaded()
        val position = AssertUtils.assertTrueAndReturnValue(
            valueBlock = { getItemsAdapterPositionList(matcher).lastOrNull() ?: -1 },
            assertionBlock = { value -> value >= 0 },
            timeoutMs = getTimeout(),
            desc = "RecyclerView(${recyclerViewInteraction.elementInfo.name}) has last item matched '$matcher'"
        )
        return UltronRecyclerViewItem(this, position, autoScroll, scrollOffset)
    }

    /**
     * @return RecyclerView representation at the moment of identifying
     *
     * in case of original RecyclerView in app under test is changed you have to call this method again to get an actual info
     * */
    open fun getRecyclerViewList(): RecyclerView {
        recyclerViewInteraction.identifyRecyclerView(recyclerViewIdentifierMatcher())
        return recyclerView
            ?: throw UltronOperationException("Couldn't find recyclerView with '${recyclerViewInteraction.elementInfo.name}'")
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
            { "RecyclerView(${recyclerViewInteraction.elementInfo.name}) has no items (actual size = ${getSize()})" }
        )
    }

    /**
     * Asserts RecyclerView has any item
     */
    fun assertNotEmpty() {
        AssertUtils.assertTrue(
            { getSize() > 0 }, operationTimeoutMs,
            { "RecyclerView(${recyclerViewInteraction.elementInfo.name}) is NOT empty" }
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
            { "RecyclerView(${recyclerViewInteraction.elementInfo.name}) size expected to be $operatorSymbol $expected (actual size = ${getSize()})" }
        )
    }

    /**
     * Asserts RecyclerView list has item at [position] during [timeoutMs]
     */
    open fun assertHasItemAtPosition(position: Int) {
        AssertUtils.assertTrue(
            { getSize() >= position }, operationTimeoutMs,
            { "Wait RecyclerView(${recyclerViewInteraction.elementInfo.name}) size >= $position (actual size = ${getSize()})" }
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
            { "RecyclerView(${recyclerViewInteraction.elementInfo.name}) has no item matched '$matcher'" }
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
            "RecyclerView(${recyclerViewInteraction.elementInfo.name}) has no item matched '$matcher'"
        )
    }

    open fun isDisplayed() = apply { recyclerViewInteraction.withTimeout(getTimeout()).isDisplayed() }
    open fun isNotDisplayed() =
        apply { recyclerViewInteraction.withTimeout(getTimeout()).isNotDisplayed() }

    open fun doesNotExist() = apply { recyclerViewInteraction.withTimeout(getTimeout()).doesNotExist() }
    open fun isEnabled() = apply { recyclerViewInteraction.withTimeout(getTimeout()).isEnabled() }
    open fun isNotEnabled() = apply { recyclerViewInteraction.withTimeout(getTimeout()).isNotEnabled() }
    open fun hasContentDescription(contentDescription: String) =
        apply {
            recyclerViewInteraction.withTimeout(getTimeout()).hasContentDescription(contentDescription)
        }

    open fun hasContentDescription(resourceId: Int) =
        apply { recyclerViewInteraction.withTimeout(getTimeout()).hasContentDescription(resourceId) }

    open fun hasContentDescription(charSequenceMatcher: Matcher<CharSequence>) =
        apply {
            recyclerViewInteraction.withTimeout(getTimeout()).hasContentDescription(charSequenceMatcher)
        }

    open fun contentDescriptionContains(text: String) =
        apply { recyclerViewInteraction.withTimeout(getTimeout()).contentDescriptionContains(text) }

    open fun assertMatches(matcher: Matcher<View>) =
        apply { recyclerViewInteraction.withTimeout(getTimeout()).assertMatches(matcher) }


    fun scrollToItem(itemMatcher: Matcher<View>, searchLimit: Int = this.itemSearchLimit, offset: Int = 0) = apply {
        recyclerViewInteraction.withTimeout(getTimeout()).perform(
            viewAction = RecyclerViewScrollAction(itemMatcher, searchLimit, offset),
            description = "Scroll RecyclerView '${recyclerViewInteraction.elementInfo.name}' to item = '$itemMatcher' with searchLimit = $searchLimit and offset = $offset"
        )
    }

    fun scrollToItem(itemInteraction: UltronEspressoInteraction<ViewInteraction>, searchLimit: Int = this.itemSearchLimit, offset: Int = 0) = apply {
        recyclerViewInteraction.withTimeout(getTimeout()).perform(
            viewAction = RecyclerViewScrollAction(itemInteraction.getInteractionMatcher()!!, searchLimit, offset),
            description = "Scroll RecyclerView '${recyclerViewInteraction.elementInfo.name}' to item with: '${itemInteraction.elementInfo.name}', searchLimit = $searchLimit and offset = $offset"
        )
    }

    @Deprecated("Use scrollToItem(itemMatcher, searchLimit, offset)")
    fun scrollToIem(itemMatcher: Matcher<View>, searchLimit: Int = this.itemSearchLimit, offset: Int = 0) = scrollToItem(UltronEspressoInteraction(onView(itemMatcher)), searchLimit, offset)

    fun scrollToItem(position: Int, offset: Int = 0) {
        assertHasItemAtPosition(position)
        val positionToScroll = position + offset
        recyclerViewInteraction.withTimeout(getTimeout()).perform(
            viewAction = RecyclerViewScrollToPositionViewAction(positionToScroll),
            description = "Scroll RecyclerView '${recyclerViewInteraction.elementInfo.name}' to position $position with offset = $offset"
        )
    }

    /** set timeout for operations with RecyclerView.
     * Note: it doesn't modify [loadTimeoutMs] (waiting a RecyclerView to be loaded)
     * */
    open fun withTimeout(timeoutMs: Long) =
        UltronRecyclerView(
            this.recyclerViewInteraction,
            this.loadTimeoutMs,
            this.itemSearchLimit,
            operationTimeoutMs = timeoutMs,
            this.recyclerImpl
        )

    open fun withResultHandler(resultHandler: (EspressoOperationResult<UltronEspressoOperation>) -> Unit) =
        recyclerViewInteraction.withResultHandler(resultHandler)

    fun isItemExist(matcher: Matcher<View>): Boolean {
        return getItemAdapterPositionAtIndex(matcher, 0) >= 0
    }

    /**
     * It's waiting while RecyclerView items to be loaded
     * @throws [UltronOperationException] if no item is loaded during [loadTimeoutMs]
     */
    fun waitItemsLoaded(
        recyclerView: RecyclerView = getRecyclerViewList(),
        minItemsCount: Int = 1
    ) = apply {
        if ((recyclerView.adapter?.itemCount ?: 0) >= minItemsCount &&
            recyclerView.childCount >= minItemsCount
        ) {
            return@apply
        }
        var isLoaded = false
        val finishTime = SystemClock.elapsedRealtime() + loadTimeoutMs
        while (!isLoaded && finishTime > SystemClock.elapsedRealtime()) {
            isLoaded = (recyclerView.adapter?.itemCount ?: 0) >= minItemsCount &&
                    recyclerView.childCount >= minItemsCount
        }
        if (!isLoaded) throw UltronOperationException(
            "RecyclerView failed to load $minItemsCount items in $loadTimeoutMs ms. " +
                    "Current state: adapter.items = ${recyclerView.adapter?.itemCount}, " +
                    "visible.items = ${recyclerView.childCount}"
        )
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
        return if (items.size - 1 < index) -1
        else items[index]
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
                if (itemView == null) itemView =
                    findItemViewAtPosition(position, view.rootView)?.itemView
                return if (itemView != null) itemView == view else false
            }
        }
    }

    /**
     * @param itemMatcher describes how to identify item in the list. It shouldn't be unique in  view hierarchy
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
                return when (recyclerImpl) {
                    UltronRecyclerViewImpl.STANDARD -> {
                        findItemView(itemMatcher, view.rootView)?.itemView?.let {
                            childView = it.findChildView(childMatcher)
                        }
                        if (childView != null) childView == view else false
                    }

                    UltronRecyclerViewImpl.PERFORMANCE -> {
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
                return when (recyclerImpl) {
                    UltronRecyclerViewImpl.STANDARD -> {
                        findItemViewAtPosition(position, view.rootView)?.itemView.let {
                            childView = it?.findChildView(childMatcher)
                        }
                        return if (childView != null) childView == view else false
                    }

                    UltronRecyclerViewImpl.PERFORMANCE -> {
                        if (childMatcher.matches(view)) {
                            isDescendantOfA(atPosition(position)).matches(view)
                        } else false
                    }
                }
            }
        }
    }

    private fun findItemView(itemMatcher: Matcher<View>, rootView: View): RecyclerView.ViewHolder? {
        val recyclerView = rootView.findChildView(recyclerViewMatcher) as RecyclerView
        this.recyclerView = recyclerView
        val viewHolderMatcher: Matcher<RecyclerView.ViewHolder> = viewHolderMatcher(itemMatcher)
        val matchedItem =
            itemsMatching(recyclerView, viewHolderMatcher, 1, itemSearchLimit).firstOrNull()
        return matchedItem?.let { recyclerView.findViewHolderForAdapterPosition(it.position) }
    }

    private fun findItemViewAtPosition(position: Int, rootView: View): RecyclerView.ViewHolder? {
        val recyclerView = rootView.findChildView(recyclerViewMatcher) as? RecyclerView
            ?: return null
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

    fun withName(name: String): UltronRecyclerView = apply {
        recyclerViewInteraction.elementInfo.name = name
    }

    enum class SizeComparison {
        EQUAL,
        GREATER_THAN,
        LESS_THAN,
        GREATER_THAN_OR_EQUAL,
        LESS_THAN_OR_EQUAL
    }
}

fun withRecyclerView(
    recyclerViewMatcher: Matcher<View>,
    loadTimeout: Long = RECYCLER_VIEW_LOAD_TIMEOUT,
    itemSearchLimit: Int = RECYCLER_VIEW_ITEM_SEARCH_LIMIT,
    operationsTimeoutMs: Long = RECYCLER_VIEW_OPERATIONS_TIMEOUT,
    implementation: UltronRecyclerViewImpl = RECYCLER_VIEW_IMPLEMENTATION
): UltronRecyclerView {
    return UltronRecyclerView(recyclerViewMatcher, loadTimeout, itemSearchLimit, operationsTimeoutMs, implementation)
}

fun withRecyclerView(
    @IntegerRes resourceId: Int,
    loadTimeout: Long = RECYCLER_VIEW_LOAD_TIMEOUT,
    itemSearchLimit: Int = RECYCLER_VIEW_ITEM_SEARCH_LIMIT,
    operationsTimeoutMs: Long = RECYCLER_VIEW_OPERATIONS_TIMEOUT,
    implementation: UltronRecyclerViewImpl = RECYCLER_VIEW_IMPLEMENTATION
): UltronRecyclerView {
    return UltronRecyclerView(withId(resourceId), loadTimeout, itemSearchLimit, operationsTimeoutMs, implementation)
}

internal fun <T> UltronEspressoInteraction<T>.identifyRecyclerView(matcher: Matcher<View>) {
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

internal fun Matcher<View>.identifyRecyclerView(matcher: Matcher<View>): Unit =
    UltronEspressoInteraction(onView(this)).identifyRecyclerView(matcher)
