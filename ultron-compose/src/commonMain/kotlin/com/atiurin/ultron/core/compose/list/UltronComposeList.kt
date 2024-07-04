package com.atiurin.ultron.core.compose.list

import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.performScrollToNode
import com.atiurin.ultron.core.common.options.ContentDescriptionContainsOption
import com.atiurin.ultron.core.compose.config.UltronComposeConfig
import com.atiurin.ultron.core.compose.nodeinteraction.UltronComposeSemanticsNodeInteraction
import com.atiurin.ultron.core.compose.operation.ComposeOperationType
import com.atiurin.ultron.core.compose.operation.UltronComposeOperationParams
import com.atiurin.ultron.exceptions.UltronAssertionException
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.utils.AssertUtils
import kotlin.reflect.KClass

class UltronComposeList(
    val listMatcher: SemanticsMatcher,
    var useUnmergedTree: Boolean = true,
    var positionPropertyKey: SemanticsPropertyKey<Int>? = null,
    val initBlock: UltronComposeList.() -> Unit = {},
    private val itemSearchLimit: Int = UltronComposeConfig.params.lazyColumnItemSearchLimit,
    private var operationTimeoutMs: Long = UltronComposeConfig.params.lazyColumnOperationTimeoutMs
) {
    private val itemChildInteractionProvider = getItemChildInteractionProvider()
    val itemInstancesMap = mutableMapOf<KClass<*>, () -> UltronComposeListItem>()
    inline fun <reified T : UltronComposeListItem> registerItem(noinline creator: () -> T){
        itemInstancesMap[T::class] = creator
    }
    init {
        initBlock()
    }

    open fun withTimeout(timeoutMs: Long) =
        UltronComposeList(
            listMatcher = listMatcher,
            useUnmergedTree = useUnmergedTree,
            positionPropertyKey = positionPropertyKey,
            itemSearchLimit = itemSearchLimit,
            operationTimeoutMs = timeoutMs
        )

    /**
     * @return current [UltronComposeList] operations timeout
     */
    fun getOperationTimeout() = operationTimeoutMs

    fun item(matcher: SemanticsMatcher) = UltronComposeListItem(this, matcher)
    fun item(position: Int): UltronComposeListItem {
        if (positionPropertyKey == null) {
            throw UltronException(
                """
                    |[positionPropertyKey] parameter is not specified for UltronComposeList
                    |Configure it like
                    |```
                    |composeList(.., positionPropertyKey = ListItemPositionPropertyKey)
                    |```
                """.trimMargin()
            )
        }
        return UltronComposeListItem(this, position, true)
    }

    fun firstItem(): UltronComposeListItem = item(0)

    /**
     * This method works properly before any scroll of target list
     * After scroll the positions of children inside the list are changed.
     */
    fun visibleItem(index: Int) = UltronComposeListItem(this, index)

    /**
     * This method works properly before any scroll of target list
     * After scroll the positions of children inside the list are changed.
     */
    fun firstVisibleItem() = visibleItem(0)

    /**
     * This method works properly before any scroll of target list
     * After scroll the positions of children inside the list are changed.
     */
    fun lastVisibleItem() = visibleItem(getInteraction().execute { it.fetchSemanticsNode().children.lastIndex })

    fun onItemChild(itemMatcher: SemanticsMatcher, childMatcher: SemanticsMatcher): UltronComposeSemanticsNodeInteraction =
        UltronComposeSemanticsNodeInteraction(
            getInteraction()
                .execute(
                    UltronComposeOperationParams(
                        operationName = "Get item '${itemMatcher.description}' child '${childMatcher.description}' in list '${getInteraction().elementInfo.name}'",
                        operationDescription = "Get Compose list item '${itemMatcher.description}' child '${childMatcher.description}' in list '${getInteraction().elementInfo.name}'",
                        operationType = ComposeOperationType.GET_LIST_ITEM_CHILD
                    )
                ) { listInteraction ->
                    itemChildInteractionProvider.onItemChild(listMatcher, itemMatcher, childMatcher, useUnmergedTree).invoke()
                }
        )


    fun onVisibleItemChild(index: Int, childMatcher: SemanticsMatcher) = UltronComposeSemanticsNodeInteraction(
        getInteraction().execute(
            UltronComposeOperationParams(
                operationName = "Get child '${childMatcher.description}' of visible item at index $index in list '${getInteraction().elementInfo.name}'",
                operationDescription = "Get Compose list child '${childMatcher.description}' of visible item at index $index in list '${getInteraction().elementInfo.name}'",
                operationType = ComposeOperationType.GET_LIST_ITEM_CHILD
            )
        ) { listInteraction ->
            itemChildInteractionProvider.onVisibleItemChild(listMatcher, index, childMatcher, useUnmergedTree).invoke()
        }
    )

    inline fun <reified T : UltronComposeListItem> getItem(matcher: SemanticsMatcher): T {
        return getComposeListItemInstance(this, matcher)
    }

    inline fun <reified T : UltronComposeListItem> getItem(position: Int): T {
        if (positionPropertyKey == null) {
            throw UltronException(
                """
                    |[positionPropertyKey] parameter is not specified for UltronComposeList
                    |Configure it like 
                    |```
                    |composeList(.., positionPropertyKey = ListItemPositionPropertyKey)
                    |```
                """.trimMargin()
            )
        }
        return getComposeListItemInstance(this, position, true)
    }

    inline fun <reified T : UltronComposeListItem> getFirstItem(): T = getItem(0)

    /**
     * This method works properly before any scroll of target list
     * After scroll the positions of children inside the list are changed.
     */
    inline fun <reified T : UltronComposeListItem> getVisibleItem(index: Int): T {
        return getComposeListItemInstance(this, index)
    }

    /**
     * This method works properly before any scroll of target list
     * After scroll the positions of children inside the list are changed.
     * */
    inline fun <reified T : UltronComposeListItem> getFirstVisibleItem(): T = getVisibleItem(0)

    /**
     * This method works properly before any scroll of target list
     * After scroll the positions of children inside the list are changed.
     * */
    inline fun <reified T : UltronComposeListItem> getLastVisibleItem(): T = getVisibleItem(getInteraction().execute { it.fetchSemanticsNode().children.lastIndex })

    /**
     * Provide a scope with references to list SemanticsNode and SemanticsNodeInteraction.
     * It is possible to evaluate any action or assertion on this node.
     */
    fun <T> performOnList(block: (SemanticsNode, SemanticsNodeInteraction) -> T): T =
        UltronComposeSemanticsNodeInteraction(listMatcher, useUnmergedTree).execute { listSemanticsNodeInteraction ->
            val listSemanticsNode = listSemanticsNodeInteraction.fetchSemanticsNode()
            block(listSemanticsNode, listSemanticsNodeInteraction)
        }

    /**
     * @return SemanticsNodeInteraction for list item
     */
    fun onItem(matcher: SemanticsMatcher) = UltronComposeSemanticsNodeInteraction(
        getInteraction().execute(
            UltronComposeOperationParams(
                operationName = "Get item '${matcher.description}' in list '${getInteraction().elementInfo.name}'",
                operationDescription = "Get Compose list item with matcher '${matcher.description}' in list '${getInteraction().elementInfo.name}'",
                operationType = ComposeOperationType.GET_LIST_ITEM
            )
        ) { listInteraction ->
            listInteraction.performScrollToNode(matcher).onChildren().filterToOne(matcher)
        }
    )

    fun visibleChild(childMatcher: SemanticsMatcher) = UltronComposeSemanticsNodeInteraction(
        getInteraction().execute(
            UltronComposeOperationParams(
                operationName = "Get child '${childMatcher.description}' of list '${getInteraction().elementInfo.name}'",
                operationDescription = "Get Compose list child '${childMatcher.description}' of list '${getInteraction().elementInfo.name}'",
                operationType = ComposeOperationType.GET_LIST_ITEM
            )
        ) { listInteraction ->
            listInteraction.onChildren().filterToOne(childMatcher)
        }
    )

    fun onVisibleItem(index: Int) = UltronComposeSemanticsNodeInteraction(
        getInteraction().execute(
            UltronComposeOperationParams(
                operationName = "Get visible item at index $index in list '${getInteraction().elementInfo.name}'",
                operationDescription = "Get Compose list visible item at index $index in list '${getInteraction().elementInfo.name}'",
                operationType = ComposeOperationType.GET_LIST_ITEM
            )
        ) { listInteraction ->
            val visibleItemsList = listInteraction.fetchSemanticsNode().children
            if (index > visibleItemsList.size) {
                throw UltronException(
                    """
                |Item index ($index) is out of visible items (${visibleItemsList.size}). 
                |It's impossible to get the reference to item by index after scroll. You have 3 variants:
                |1. [Preferred one] Use another method to receive list item with matcher UltronComposeList.item(matcher: SemanticsMatcher) 
                |   In case you still wanna scroll to item by position in list:
                |   - Add testTag for items in LazyColumn definition like 'itemsIndexed(items){ index, index -> .. Modifier.testTag("position=`$`index") }'
                |   - Use matcher in test to get item 'list.item(hasTestTag("position=`$`index"))'
                |2. [A good way also] Use another method to receive list item with position UltronComposeList.item(position: Int) 
                |   To use this method you have to:
                |   - Configure custom Position SemanticsProperty for compose list items in application code
                |   - Setup [positionPropertyKey] parameter for composeList(..) in test code
                |   - Read documentation for details.  
                |3. Scroll to index by using UltronComposeList.scrollToIndex(index: Int) and use SemanticsMatcher to find item. 
            """.trimMargin()
                )
            }
            listInteraction.onChildAt(index)
        }
    )

    fun scrollToNode(itemMatcher: SemanticsMatcher) = apply { getInteraction().scrollToNode(itemMatcher) }
    fun scrollToIndex(index: Int) = apply { getInteraction().scrollToIndex(index) }
    fun scrollToKey(key: Any) = apply { getInteraction().scrollToKey(key) }
    fun assertIsDisplayed() = apply { getInteraction().assertIsDisplayed() }
    fun assertIsNotDisplayed() = apply { getInteraction().assertIsNotDisplayed() }
    fun assertExists() = apply { getInteraction().assertExists() }
    fun assertDoesNotExist() = apply { getInteraction().assertDoesNotExist() }
    fun assertContentDescriptionEquals(vararg expected: String) = apply { getInteraction().assertContentDescriptionEquals(*expected) }
    fun assertContentDescriptionContains(expected: String, option: ContentDescriptionContainsOption? = null) =
        apply { getInteraction().assertContentDescriptionContains(expected, option) }

    fun assertMatches(matcher: SemanticsMatcher) = apply { getInteraction().assertMatches(matcher) }
    fun assertNotEmpty() = apply {
        AssertUtils.assertTrue(
            { getVisibleItemsCount() > 0 }, getOperationTimeout(),
            { "Compose list (${listMatcher.description}) is NOT empty" }
        )
    }

    fun assertEmpty() = apply {
        AssertUtils.assertTrue(
            { getVisibleItemsCount() == 0 }, getOperationTimeout(),
            { "Compose list (${listMatcher.description}) has no items (visible items count = ${getVisibleItemsCount()})" }
        )
    }

    fun assertVisibleItemsCount(expected: Int) = apply {
        AssertUtils.assertTrue(
            { getVisibleItemsCount() == expected }, getOperationTimeout(),
            { "Compose list (${listMatcher.description}) has visible items count = $expected (actual visible items count = ${getVisibleItemsCount()})" }
        )
    }

    /**
     * Asserts whether an item exists in the list or not.
     * If the item doesn't exist, the operation is considered successful immediately.
     * If the item exists, the operation waits for a specified timeout ([operationTimeoutMs]) for the item to disappear.
     * Otherwise, an exception will be thrown.
     */
    fun assertItemDoesNotExist(itemMatcher: SemanticsMatcher) {
        getInteraction().perform(
            params = UltronComposeOperationParams(
                operationName = "Assert item ${itemMatcher.description} doesn't exist in list ${getInteraction().elementInfo.name}",
                operationDescription = "Assert item ${itemMatcher.description} doesn't exist in list ${getInteraction().elementInfo.name} during ${getOperationTimeout()}",
                operationType = ComposeOperationType.ASSERT_LIST_ITEM_DOES_NOT_EXIST
            )
        ) {
            runCatching { it.performScrollToNode(itemMatcher) }
                .onSuccess { throw UltronAssertionException("Item '${itemMatcher.description}' exists in list '${listMatcher.description}'") }
                .onFailure { e ->
                    e.message?.let { message ->
                        if (!message.contains("No node found that matches")) {
                            throw AssertionError(message)
                        }
                    }
                }
        }
    }

    fun getVisibleItemsCount(): Int = getInteraction().execute { it.fetchSemanticsNode().children.size }
    fun getInteraction() = UltronComposeSemanticsNodeInteraction(listMatcher, useUnmergedTree, operationTimeoutMs)

    @Deprecated("Use getInteraction() instead", ReplaceWith("getInteraction()"))
    fun getMatcher() = getInteraction()
}



fun composeList(
    listMatcher: SemanticsMatcher,
    useUnmergedTree: Boolean = true,
    positionPropertyKey: SemanticsPropertyKey<Int>? = null,
    initBlock: UltronComposeList.() -> Unit = {}
) = UltronComposeList(listMatcher, useUnmergedTree, positionPropertyKey, initBlock)
