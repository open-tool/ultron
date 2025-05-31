package com.atiurin.ultron.core.uiautomator.uiobject2

import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiObject2
import com.atiurin.ultron.core.config.UltronConfig.UiAutomator.Companion.uiDevice
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.exceptions.UltronUiAutomatorException
import com.atiurin.ultron.extensions.getBySelector
import kotlin.reflect.typeOf

/**
 * Represents a UI block based on the UiAutomator framework using [UiObject2] elements.
 *
 * This class provides a foundation for defining and working with blocks in UI tests,
 * offering methods to locate and interact with descendant elements or child blocks.
 *
 * ### Constructor Requirements:
 * To define a new block type inheriting from this class, ensure the following:
 * 1. The block class must be a **top-level or file-level class** (not nested inside another class).
 *    Nested class definitions may result in hidden Java constructor parameters.
 * 2. The block class must have the following constructor:
 *    `class CustomBlock(blockDesc: String, blockSelector: () -> BySelector) : UltronUiObject2UiBlock(blockDesc, blockSelector)`
 *
 * ### Methods:
 * - [child]: Creates and retrieves child UI elements or blocks of the specified type [T].
 */
open class UltronUiObject2UiBlock(val blockDesc: String = "Empty block description", val blockSelector: () -> BySelector) {
    /**
     * Provides a search mechanism for descendant elements within this block.
     *
     * @param childSelector A [BySelector] to locate the child element.
     * @return A function that retrieves the descendant element or throws an exception if not found.
     */
    private fun descendantSearchProvider(childSelector: BySelector): () -> UiObject2 = {
        uiDevice.findObject(blockSelector())
            ?.findObject(childSelector)
            ?: throw UltronUiAutomatorException("'$childSelector' in block ${this::class.simpleName} '$blockDesc' not found!")
    }

    /**
     * A utility property for interaction with this UI block.
     *
     * Appends the block description to the matcher for better logging and debugging.
     */
    val uiBlock
        get() = UltronUiObject2({ uiDevice.findObject(blockSelector()) }, blockDesc)

    /**
     * Searches for a child element within the block and returns it as an [UltronUiObject2].
     *
     * @param selector The selector for the child element.
     * @param description A textual description of the child element.
     * @return The [UltronUiObject2] representing the child element.
     */
    fun child(selector: BySelector, description: String = selector.toString()): UltronUiObject2 = UltronUiObject2(
        uiObject2ProviderBlock = descendantSearchProvider(selector),
        selectorDesc = "Child of block '$blockDesc' with selector '$description'"
    )

    /**
     * Searches for a child block within this block and returns it as the specified type [T].
     *
     * @param uiBlock An instance of the desired block type, acting as a template.
     * @param description A textual description of the block.
     * @return A new instance of the specified block type [T].
     * @throws UltronException If the block's constructor is incompatible or the block cannot be created.
     */
    inline fun <reified T : UltronUiObject2UiBlock> child(
        uiBlock: T,
        description: String = uiBlock.blockDesc
    ): T {
        val modifiedSelectorBlock = {
            uiDevice.findObject(blockSelector())
                ?.findObject(uiBlock.blockSelector())
                ?.getBySelector()
        }
        val updatedBLock = runCatching {
            T::class.constructors.firstOrNull {
                it.parameters.size == 2
                        && it.parameters.first().type == typeOf<String>()
                        && it.parameters[1].type == typeOf<() -> BySelector>()
            }?.call(description, modifiedSelectorBlock)
        }.onFailure {
            if (it is IllegalArgumentException) {
                throw UltronException(
                    "${T::class.simpleName} has hidden java constructor parameters. ${T::class.simpleName} must be defined as a top-level class (not nested inside any other class)."
                )
            } else throw UltronException("Unable to create updated ${T::class.simpleName}. Message: ${it.message}")
        }.getOrNull()
        updatedBLock?.let { return it } ?: throw UltronException(
            """
            |${T::class.simpleName} doesn't have an appropriate constructor with arguments: String, () -> BySelector.
            |Ensure that the class meets the following conditions:
            |1. ${T::class.simpleName} must not be defined inside another class. It should be a top-level or file-level class.
            |2. ${T::class.simpleName} must have the following constructor:
            |class ${T::class.simpleName}(blockDesc: String, blockSelector: () -> BySelector) : UltronUiObject2UiBlock(blockDesc, blockSelector)
            |If neither constructor is available, consider using another method for child declaration:
            |```
            |fun <B : UltronUiObject2UiBlock> child(
            |    selector: BySelector,
            |    description: String = selector.toString(),
            |    uiBlockFactory: (String, () -> BySelector) -> B
            |): B
            | ```
        """.trimMargin()
        )
    }

    /**
     * Searches for a child block using a factory function.
     *
     * @param selector The selector for the child block.
     * @param description A textual description of the block.
     * @param uiBlockFactory A factory function to create the block.
     * @return The created block instance.
     */
    fun <B : UltronUiObject2UiBlock> child(
        selector: BySelector,
        description: String = selector.toString(),
        uiBlockFactory: (String, () -> BySelector) -> B
    ): B {
        val modifiedSelectorBlock = {
            val block = uiDevice.findObject(blockSelector())
                ?: throw UltronUiAutomatorException("'$blockDesc' with type ${this::class.simpleName} not found!")
            block.findObject(selector)?.getBySelector()
                ?: throw UltronUiAutomatorException(
                    "'$description' child with selector '$selector' not found in block '$blockDesc' with type ${this::class.simpleName}!"
                )

        }
        return uiBlockFactory(description, modifiedSelectorBlock)
    }
}