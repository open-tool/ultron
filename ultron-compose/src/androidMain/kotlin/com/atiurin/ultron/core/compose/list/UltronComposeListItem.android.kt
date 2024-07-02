package com.atiurin.ultron.core.compose.list

import androidx.compose.ui.test.SemanticsMatcher
import com.atiurin.ultron.exceptions.UltronException

actual inline fun <reified T : UltronComposeListItem> getComposeListItemInstance(
    ultronComposeList: UltronComposeList,
    itemMatcher: SemanticsMatcher
): T {
    val item = createUltronComposeListItemInstance<T>()
    item.setExecutor(ultronComposeList, itemMatcher)
    return item
}

actual inline fun <reified T : UltronComposeListItem> getComposeListItemInstance(
    ultronComposeList: UltronComposeList,
    position: Int,
    isPositionPropertyConfigured: Boolean
): T {
    val item = createUltronComposeListItemInstance<T>()
    item.setExecutor(ultronComposeList, position, isPositionPropertyConfigured)
    return item
}

inline fun <reified T : UltronComposeListItem> createUltronComposeListItemInstance(): T {
    return try {
        T::class.java.newInstance()
    } catch (ex: Exception) {
        val desc = when {
            T::class.isInner -> {
                "${T::class.simpleName} is an inner class so you have to delete inner modifier (It is often when kotlin throws 'has no zero argument constructor' but real reason is an inner modifier)"
            }

            T::class.constructors.find { it.parameters.isEmpty() } == null -> {
                "${T::class.simpleName} doesn't have a constructor without params (create an empty constructor)"
            }

            else -> ex.message
        }
        throw UltronException(
            """
                    |Couldn't create an instance of ${T::class.simpleName}. 
                    |Possible reason: $desc 
                    |Original exception: ${ex.message}, cause ${ex.cause}
                """.trimMargin()
        )
    }
}