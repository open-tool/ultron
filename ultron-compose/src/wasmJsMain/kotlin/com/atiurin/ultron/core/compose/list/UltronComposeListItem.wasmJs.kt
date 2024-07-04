package com.atiurin.ultron.core.compose.list

import androidx.compose.ui.test.SemanticsMatcher
import com.atiurin.ultron.exceptions.UltronException

actual inline fun <reified T : UltronComposeListItem> getComposeListItemInstance(
    ultronComposeList: UltronComposeList,
    itemMatcher: SemanticsMatcher
): T {
    val item = ultronComposeList.itemInstancesMap[T::class]?.invoke()
        ?: throw UltronException(
            """
            |Item with ${T::class} not registered in compose list ${ultronComposeList.listMatcher.description}
            |Configure it by using [initBlock] parameter
            |```
            |composeList(.., initBlock = {
            |    registerItem { ListItem() }
            |})
            |```
            """.trimMargin()
        )
    item.setExecutor(ultronComposeList, itemMatcher)
    return item as T
}

actual inline fun <reified T : UltronComposeListItem> getComposeListItemInstance(
    ultronComposeList: UltronComposeList,
    position: Int,
    isPositionPropertyConfigured: Boolean
): T {
    val item = ultronComposeList.itemInstancesMap[T::class]?.invoke()
        ?: throw UltronException(
            """
            |Item with ${T::class} not registered in compose list ${ultronComposeList.listMatcher.description}
            |Configure it by using [initBlock] parameter
            |```
            |composeList(.., initBlock = {
            |    registerItem { ListItem() }
            |})
            |```
            """.trimMargin()
        )
    item.setExecutor(ultronComposeList, position, isPositionPropertyConfigured)
    return item as T
}