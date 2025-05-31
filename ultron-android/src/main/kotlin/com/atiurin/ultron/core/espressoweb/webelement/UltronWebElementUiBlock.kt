package com.atiurin.ultron.core.espressoweb.webelement

import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.log.UltronLog
import kotlin.reflect.typeOf

open class UltronWebElementUiBlock(val blockElement: UltronWebElement, val blockDescription: String = "") {
    /**
     * A utility property for interaction with this UI block.
     *
     * Appends the block description to the matcher for better logging and debugging.
     */
    val uiBlock
        get() = blockElement.let {
            if (blockDescription.isNotBlank()) {
                it.withName(blockDescription)
            } else it
        }

    /**
     * Modifies the provided element by contextualizing it with the current block's element.
     *
     * This method applies a contextual transformation to the given `element`, associating it with the context
     * of the current block's `blockElement`.
     *
     * @param childElement The element to be contextualized.
     * @return A new element modified by adding the context of the current block's element.
     */
    fun child(childElement: UltronWebElement): UltronWebElement = blockElement.withContextual(childElement)

    /**
     * Creates a child UI block using an element contextualized with the current block's element.
     *
     * This method applies a contextual transformation to the provided `element` and constructs a new UI block instance
     * using the `uiBlockFactory`.
     *
     * @param childElement The element to be contextualized for the child UI block.
     * @param uiBlockFactory A factory function to create a new instance of the child UI block.
     *                       The function takes the contextualized element as input and returns a UI block instance.
     * @return A new instance of the child UI block, constructed with the contextualized element.
     */
    fun <B : UltronWebElementUiBlock> child(
        childElement: UltronWebElement,
        uiBlockFactory: (UltronWebElement) -> B
    ): B {
        return uiBlockFactory(blockElement.withContextual(childElement))
    }

    /**
     * Creates a child UI block of a specified type using the provided block element of the parent UI block.
     *
     * This extension function simplifies the creation of a new instance of a child UI block by automatically invoking
     * the appropriate constructor of the specified type [T]. The `blockElement` of the current block is contextualized
     * with the `blockElement` of the provided `uiBlock` to locate and define the new block.
     *
     * @param T The type of the child UI block to be created. It must extend [UltronWebElementUiBlock].
     * @param uiBlock The existing instance of the child UI block to use as a template for the new block.
     *                The `blockElement` and `blockDescription` properties of this block are used to create the new instance.
     * @return A new instance of the specified type [T], initialized with the contextualized element and description.
     *
     * @throws UltronException If the specified class [T] does not have an appropriate constructor or cannot be instantiated.
     *
     * ### Constructor Requirements:
     * The class [T] must meet the following conditions to be instantiated:
     * 1. It must not be a nested or inner class. It should be defined at the top level or as a file-level class.
     * 2. It must have one of the following constructors:
     *    - A constructor with two parameters: `blockElement` of type [UltronWebElement] and `blockDescription` of type [String]:
     *      `class CustomWebElementBlock(blockElement: UltronWebElement, blockDescription: String)`
     *    - A constructor with one parameter of type [UltronWebElement]:
     *      `class CustomWebElementBlock(blockElement: UltronWebElement)`
     *
     * If neither constructor is available, consider using an alternative method for child creation,
     * such as `child(element, uiBlockFactory)`.
     *
     * ### Usage Example:
     * ```kotlin
     * // UI elements declaration
     * class CustomElementBlock(blockElement: UltronWebElement, blockDescription: String = "") : UltronWebElementUiBlock(blockElement, blockDescription)
     *
     * class CustomComplexBlock(blockElement: UltronWebElement, blockDescription: String = "") : UltronWebElementUiBlock(blockElement, blockDescription) {
     *     val custom = child(CustomElementBlock(blockElement.withContextual(hasTestTag("elementTag")), "custom element child of '$blockDescription'"))
     * }
     *
     * // Screen object
     * object MyWebPage : Screen<MyWebPage>() {
     *     val complexBlock = CustomComplexBlock(findElement("bigUiBlockLocator"), "Presentable description of UI block")
     * }
     *
     * // Somewhere in the test
     * MyWebPage {
     *     complexBlock.custom.isDisplayed()
     * }
     * ```
     */
    inline fun <reified T : UltronWebElementUiBlock> child(
        uiBlock: T,
    ): T {
        val updatedElement = blockElement.withContextual(uiBlock.blockElement)
        val updatedBlock = runCatching {
            T::class.constructors.forEach { constructor ->
                UltronLog.info("Constructor: $constructor, Parameters: ${constructor.parameters.map { it.type }}")
            }
            T::class.constructors.firstOrNull {
                it.parameters.size == 2
                        && it.parameters.first().type == typeOf<UltronWebElement>()
                        && it.parameters[1].type == typeOf<String>()
            }?.let { constructor ->
                return@runCatching constructor.call(updatedElement, uiBlock.blockDescription)
            }
            T::class.constructors.firstOrNull {
                it.parameters.size == 1 && it.parameters.first().type == typeOf<UltronWebElement>()
            }?.let {
                return@runCatching it.call(updatedElement)
            }
            null
        }.onFailure {
            if (it is IllegalArgumentException) {
                throw UltronException(
                    "${T::class.simpleName} has hidden java constructor parameters. ${T::class.simpleName} must be defined as a top-level class (not nested inside any other class)."
                )
            } else throw UltronException("Unable to create updated ${T::class.simpleName}. Message: ${it.message}")
        }.getOrNull()
        updatedBlock?.let { return it } ?: throw UltronException(
            """ |${T::class.simpleName} doesn't have an appropriate constructor with arguments: (UltronWebElement, String) or (UltronWebElement)
                |Ensure that the class meets the following conditions:
                |1. ${T::class.simpleName} must not be defined inside another class. It should be a top-level or file-level class.
                |2. ${T::class.simpleName} must have one of the following constructors:
                |- A constructor with two parameters: blockElement of type UltronWebElement and blockDescription of type String:
                |class ${T::class.simpleName}(blockElement: UltronWebElement, blockDescription: String) : UltronWebElementUiBlock(blockElement, blockDescription)
                |- A constructor with one parameter of type UltronWebElement:
                |class ${T::class.simpleName}(blockElement: UltronWebElement) : UltronWebElementUiBlock(blockElement)
                |If neither constructor is available, consider using another method for child declaration:
                |```
                |fun <B : UltronWebElementUiBlock> child(
                |    element: UltronWebElement,
                |    uiBlockFactory: (UltronWebElement) -> B
                |): B
                |```
            """.trimMargin()
        )
    }
}