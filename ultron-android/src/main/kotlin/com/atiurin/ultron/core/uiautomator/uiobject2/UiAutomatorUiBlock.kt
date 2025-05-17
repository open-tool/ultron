package com.atiurin.ultron.core.uiautomator.uiobject2

import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiObject2
import com.atiurin.ultron.core.config.UltronConfig.UiAutomator.Companion.uiDevice
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.exceptions.UltronUiAutomatorException
import com.atiurin.ultron.extensions.getBySelector
import kotlin.reflect.typeOf

open class UltronUiObject2UiBlock(val blockSelector: () -> BySelector?) {
    /**
     * Provides a search mechanism for descendant elements within this block.
     *
     * @param childSelector A function that returns the selector for the child element.
     * @return A provider function that retrieves the descendant element or throws an exception if not found.
     */
    fun descendantSearchProvider(childSelector: () -> BySelector): () -> UiObject2? = {
        uiDevice.findObject(blockSelector())
            ?.findObject(childSelector())
            ?: throw UltronUiAutomatorException("Block ${blockSelector()} has no descendant matching ${childSelector()}")
    }

    /**
     * Searches for a child element within the block and returns it as an [UltronUiObject2].
     *
     * @param childSelector A function that returns the selector for the child element.
     * @return The [UltronUiObject2] representing the child element.
     */
    fun child(childSelector: () -> BySelector): UltronUiObject2 = UltronUiObject2(
        uiObject2ProviderBlock = descendantSearchProvider(childSelector),
        selectorDesc = "" //"Child of block ${blockSelector()} with selector ${childSelector()}"
    )

    /**
     * Searches for a child block within this block and returns it as the specified type [T].
     *
     * @param uiBlock The block to locate as a child of this block.
     * @return An instance of the specified block type [T].
     * @throws UltronException If the block's constructor is not compatible.
     */
    inline fun <reified T : UltronUiObject2UiBlock> child(uiBlock: T): T {
        val constructor = T::class.constructors.firstOrNull {
            it.parameters.size == 1 && it.parameters.first().type == typeOf<() -> BySelector>()
        } ?: throw UltronException(
            """
            |UiBlock subclass ${T::class.simpleName} doesn't have a constructor with single () -> BySelector argument.
            |Try following code: 
            |class ${T::class.simpleName}(parent: () -> BySelector) : UltronUiObject2UiBlock(parent) {
        """.trimMargin()
        )
        val modifiedSelectorBlock = {
            uiDevice.findObject(blockSelector())
                ?.findObject(uiBlock.blockSelector())
                ?.getBySelector()

        }
        return constructor.call(modifiedSelectorBlock)
    }

    fun <B : UltronUiObject2UiBlock> child(
        childSelector: () -> BySelector,
        uiBlockFactory: (() -> BySelector?) -> B
    ): B {
        val modifiedSelectorBlock = {
            uiDevice.findObject(blockSelector())
                ?.findObject(childSelector())
                ?.getBySelector()
        }
        return uiBlockFactory(modifiedSelectorBlock)
    }
}