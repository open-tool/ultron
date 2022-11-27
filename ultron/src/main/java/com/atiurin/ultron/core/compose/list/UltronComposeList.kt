package com.atiurin.ultron.core.compose.list

import androidx.compose.ui.semantics.*
import androidx.compose.ui.test.*
import com.atiurin.ultron.core.common.options.ContentDescriptionContainsOption
import com.atiurin.ultron.core.compose.nodeinteraction.UltronComposeSemanticsNodeInteraction
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.recyclerview.UltronRecyclerViewItem
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.utils.AssertUtils

class UltronComposeList(
    val listMatcher: SemanticsMatcher,
    var useUnmergedTree: Boolean = true,
    private val itemSearchLimit: Int = UltronConfig.Compose.LAZY_COLUMN_ITEM_SEARCH_LIMIT,
    private var operationTimeoutMs: Long = UltronConfig.Compose.LAZY_COLUMN_OPERATIONS_TIMEOUT
) {

    open fun withTimeout(timeoutMs: Long) =
        UltronComposeList(listMatcher, useUnmergedTree, this.itemSearchLimit, operationTimeoutMs = timeoutMs)

    /**
     * @return current [UltronComposeList] operations timeout
     */
    fun getOperationTimeout() = operationTimeoutMs

    fun item(matcher: SemanticsMatcher) = UltronComposeListItem(this, matcher)
    fun visibleItem(index: Int) = UltronComposeListItem(this, index)
    fun firstVisibleItem() = visibleItem(0)
    fun lastVisibleItem() = visibleItem(getMatcher().perform { it.fetchSemanticsNode().children.lastIndex })

    /** @return [UltronRecyclerViewItem] subclass instance matches [matcher]
     *
     * Note: never add inner modifier to [T] class
     *
     * Note: [T] class should have a constructor without parameters, eg
     *
     *    class SomeRecyclerViewItem : UltronRecyclerViewItem(){...}
     * */
    inline fun <reified T : UltronComposeListItem> getItem(
        matcher: SemanticsMatcher
    ): T {
        return UltronComposeListItem.getInstance(this, matcher)
    }

    /** @return [UltronRecyclerViewItem] subclass instance at position [index]
     *
     * Note: never add inner modifier to [T] class
     *
     * Note: [T] class should have a constructor without parameters, eg
     *
     *    class SomeRecyclerViewItem : UltronRecyclerViewItem(){...}
     * */
    inline fun <reified T : UltronComposeListItem> getVisibleItem(
        index: Int
    ): T {
        return UltronComposeListItem.getInstance(this, index)
    }

    inline fun <reified T : UltronComposeListItem> getFirstVisibleItem(): T = getVisibleItem(0)
    inline fun <reified T : UltronComposeListItem> getLastVisibleItem(): T = getVisibleItem(getMatcher().perform { it.fetchSemanticsNode().children.lastIndex })
    /**
     * Provide a scope with references to list SemanticsNode and SemanticsNodeInteraction.
     * It is possible to evaluate any action or assertion on this node.
     */
    fun <T> performOnList(block: (SemanticsNode, SemanticsNodeInteraction) -> T): T =
        UltronComposeSemanticsNodeInteraction(listMatcher, useUnmergedTree).perform { listSemanticsNodeInteraction ->
            val listSemanticsNode = listSemanticsNodeInteraction.fetchSemanticsNode()
            block(listSemanticsNode, listSemanticsNodeInteraction)
        }

    /**
     * @return SemanticsNodeInteraction for list item
     */
    fun onItem(matcher: SemanticsMatcher) = UltronComposeSemanticsNodeInteraction(
        getMatcher().perform { listInteraction ->
            listInteraction.performScrollToNode(matcher).onChildren().filterToOne(matcher)
        }
    )

    fun onItemChild(itemMatcher: SemanticsMatcher, childMatcher: SemanticsMatcher): UltronComposeSemanticsNodeInteraction =
        UltronComposeSemanticsNodeInteraction(UltronComposeSemanticsNodeInteraction(listMatcher, true)
            .perform { listInteraction ->
                listInteraction.performScrollToNode(itemMatcher)
                    .onChildren().filterToOne(itemMatcher)
                    .onChildren().filterToOne(childMatcher)
            }
        )

    fun visibleChild(childMatcher: SemanticsMatcher) = UltronComposeSemanticsNodeInteraction(
        getMatcher().perform { listInteraction ->
            listInteraction.onChildren().filterToOne(childMatcher)
        }
    )

    fun onVisibleItemChild(index: Int, childMatcher: SemanticsMatcher) = UltronComposeSemanticsNodeInteraction(
        getMatcher().perform { listInteraction ->
            listInteraction.onChildAt(index).onChildren().filterToOne(childMatcher)
        }
    )

    fun onVisibleItem(index: Int) = UltronComposeSemanticsNodeInteraction(
        getMatcher().perform { listInteraction ->
            val visibleItemsList = listInteraction.fetchSemanticsNode().children
            if (index > visibleItemsList.size) {
                throw UltronException(
                    """
                |Item index ($index) is out of visible items (${visibleItemsList.size}). 
                |It's impossible to get the reference to item by index after scroll. You have 2 variants:
                |1. [Preferred one] Use another method to receive list item with matcher UltronComposeList.item(matcher: SemanticsMatcher) 
                |   In case you still wanna scroll to item by position in list:
                |   - Add testTag for items in LazyColumn definition like 'itemsIndexed(items){ index, index -> .. Modifier.testTag("position=`$`index") }'
                |   - Use matcher in test to get item 'list.item(hasTestTag("position=`$`index"))'
                |2. Scroll to index by using UltronComposeList.scrollToIndex(index: Int) and use SemanticsMatcher to find item. 
            """.trimMargin()
                )
            }
            listInteraction.onChildAt(index)
        }
    )

    fun scrollToNode(itemMatcher: SemanticsMatcher) = apply {
        getMatcher().perform { listInteraction ->
            listInteraction.performScrollToNode(itemMatcher)
        }
    }
    fun scrollToIndex(index: Int) = apply { getMatcher().perform { it.performScrollToIndex(index) } }
    fun scrollToKey(key: Any) = apply { getMatcher().perform { it.performScrollToKey(key) } }
    fun assertIsDisplayed() = apply { getMatcher().withTimeout(getOperationTimeout()).assertIsDisplayed() }
    fun assertIsNotDisplayed() = apply { getMatcher().withTimeout(getOperationTimeout()).assertIsNotDisplayed() }
    fun assertExists() = apply { getMatcher().withTimeout(getOperationTimeout()).assertExists() }
    fun assertDoesNotExist() = apply { getMatcher().withTimeout(getOperationTimeout()).assertDoesNotExist() }
    fun assertContentDescriptionEquals(vararg expected: String) = apply { getMatcher().withTimeout(getOperationTimeout()).assertContentDescriptionEquals(*expected) }
    fun assertContentDescriptionContains(expected: String, option: ContentDescriptionContainsOption? = null) =
        apply { getMatcher().withTimeout(getOperationTimeout()).assertContentDescriptionContains(expected, option) }

    fun assertNotEmpty() = apply {
        AssertUtils.assertTrue(
            { getVisibleItemsCount() > 0 }, getOperationTimeout(),
            "Compose list (${listMatcher.description}) is NOT empty"
        )
    }

    fun assertEmpty() = apply {
        AssertUtils.assertTrue(
            { getVisibleItemsCount() == 0 }, getOperationTimeout(),
            "Compose list (${listMatcher.description}) has no items (visible items count = ${getVisibleItemsCount()})"
        )
    }

    fun assertVisibleItemsCount(expected: Int) = apply {
        AssertUtils.assertTrue(
            { getVisibleItemsCount() == expected }, getOperationTimeout(),
            "Compose list (${listMatcher.description}) has visible items count = $expected (actual visible items count = ${getVisibleItemsCount()})"
        )
    }

    fun getVisibleItemsCount(): Int = getMatcher().perform { it.fetchSemanticsNode().children.size }
    fun getMatcher() = UltronComposeSemanticsNodeInteraction(listMatcher, useUnmergedTree)
}

fun composeList(listMatcher: SemanticsMatcher, useUnmergedTree: Boolean = true) = UltronComposeList(listMatcher, useUnmergedTree)
