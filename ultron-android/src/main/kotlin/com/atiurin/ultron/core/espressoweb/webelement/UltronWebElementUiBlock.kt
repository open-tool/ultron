package com.atiurin.ultron.core.espressoweb.webelement

import com.atiurin.ultron.exceptions.UltronException
import kotlin.reflect.typeOf

open class UltronWebElementUiBlock(val blockElement: UltronWebElement, val blockDescription: String) {
    /**
     * A utility property for interaction with this UI block.
     *
     * Appends the block description to the matcher for better logging and debugging.
     */
    val uiBlock
        get() = blockElement.withName(blockDescription)

    fun child(element: UltronWebElement): UltronWebElement = blockElement.withContextual(element)

    inline fun <reified T : UltronWebElementUiBlock> child(
        uiBlock: T,
    ): T {
        val updatedElement = blockElement.withContextual(uiBlock.blockElement)
        val updatedBLock = runCatching {
            T::class.constructors.firstOrNull {
                it.parameters.size == 2
                        && it.parameters.first().type == typeOf<UltronWebElement>()
                        && it.parameters[1].type == typeOf<String>()
            }?.call(updatedElement, blockDescription)
        }.onFailure {
            if (it is IllegalArgumentException) {
                throw UltronException(
                    "${T::class.simpleName} has hidden java constructor parameters. ${T::class.simpleName} must be defined as a top-level class (not nested inside any other class)."
                )
            } else throw UltronException("Unable to create updated ${T::class.simpleName}. Message: ${it.message}")
        }.getOrNull()
        updatedBLock?.let { return it } ?: throw UltronException(
            """
            |${T::class.simpleName} doesn't have an appropriate constructor with arguments: (UltronWebElement, String)
            |Ensure that the class meets the following conditions:
            |1. ${T::class.simpleName} must not be defined inside another class. It should be a top-level or file-level class.
            |2. ${T::class.simpleName} must have the following constructor:
            |class ${T::class.simpleName}(val blockElement: UltronWebElement, val blockDescription: String) : UltronWebElementUiBlock(blockElement, blockDescription)
            |If neither constructor is available, consider using another method for child declaration:
            |```
            |fun <B : UltronWebElementUiBlock> child(
            |    element: UltronWebElement,
            |    uiBlockFactory: (UltronWebElement) -> B
            |): B
            | ```
        """.trimMargin()
        )
    }

    fun <B : UltronWebElementUiBlock> child(
        element: UltronWebElement,
        uiBlockFactory: (UltronWebElement) -> B
    ): B {
        return uiBlockFactory(blockElement.withContextual(element))
    }
}