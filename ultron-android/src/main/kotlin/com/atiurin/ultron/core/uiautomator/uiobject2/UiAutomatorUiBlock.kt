package com.atiurin.ultron.core.uiautomator.uiobject2

import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiObject2
import com.atiurin.ultron.core.config.UltronConfig.UiAutomator.Companion.uiDevice
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.exceptions.UltronUiAutomatorException
import com.atiurin.ultron.extensions.getBySelector
import kotlin.reflect.typeOf

open class UltronUiObject2UiBlock(val blockDesc: String = "Empty block description", val blockSelector: () -> BySelector) {
    /**
     * Provides a search mechanism for descendant elements within this block.
     *
     * @param childSelector A function that returns the selector for the child element.
     * @return A provider function that retrieves the descendant element or throws an exception if not found.
     */
    fun descendantSearchProvider(childSelector: BySelector): () -> UiObject2 = {
        uiDevice.findObject(blockSelector())
            ?.findObject(childSelector)
            ?: throw UltronUiAutomatorException("'$childSelector' in block ${this::class.simpleName} '$blockDesc' not found!")
    }

    /**
     * Searches for a child element within the block and returns it as an [UltronUiObject2].
     *
     * @param childSelector A function that returns the selector for the child element.
     * @return The [UltronUiObject2] representing the child element.
     */
    fun child(selector: BySelector, description: String = selector.toString()): UltronUiObject2 = UltronUiObject2(
        uiObject2ProviderBlock = descendantSearchProvider(selector),
        selectorDesc = "Child of block '$blockDesc' with selector '$description'"
    )

    /**
     * Searches for a child block within this block and returns it as the specified type [T].
     *
     * @param uiBlock The block to locate as a child of this block.
     * @return An instance of the specified block type [T].
     * @throws UltronException If the block's constructor is not compatible.
     */
    inline fun <reified T : UltronUiObject2UiBlock> child(
        uiBlock: T,
        description: String = uiBlock.blockDesc
    ): T {
        val constructor = T::class.constructors.firstOrNull {
            it.parameters.size == 2
                    && it.parameters.first().type == typeOf<String>()
                    && it.parameters[1].type == typeOf<() -> BySelector>()
        } ?: throw UltronException(
            """
            |${T::class.simpleName} doesn't have a constructor with arguments: String, () -> BySelector.
            |Try following code: 
            |class ${T::class.simpleName}(blockDesc: String, blockSelector: () -> BySelector) : UltronUiObject2UiBlock(blockDesc, blockSelector) {
        """.trimMargin()
        )
        val modifiedSelectorBlock = {
            uiDevice.findObject(blockSelector())
                ?.findObject(uiBlock.blockSelector())
                ?.getBySelector()
        }
        return constructor.call(description, modifiedSelectorBlock)
    }

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