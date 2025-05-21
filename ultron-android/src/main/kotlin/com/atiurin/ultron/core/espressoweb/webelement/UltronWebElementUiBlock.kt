package com.atiurin.ultron.core.espressoweb.webelement

import com.atiurin.ultron.exceptions.UltronException
import kotlin.reflect.typeOf

open class UltronWebElementUiBlock(val blockElement: UltronWebElement) {

    fun child(element: UltronWebElement): UltronWebElement = blockElement.withContextual(element)

    inline fun <reified T : UltronWebElementUiBlock> child(
        uiBlock: T,
    ): T {
        val constructor = T::class.constructors.firstOrNull {
            it.parameters.size == 1
                    && it.parameters.first().type == typeOf<UltronWebElement>()
        } ?: throw UltronException(
            """
            |${T::class.simpleName} doesn't have a constructor with argument UltronWebElement
            |Try following code: 
            |class ${T::class.simpleName}(val blockElement: UltronWebElement) : UltronWebElementUiBlock(blockElement) {
        """.trimMargin()
        )
        return constructor.call(blockElement.withContextual(uiBlock.blockElement))
    }

    fun <B : UltronWebElementUiBlock> child(
        element: UltronWebElement,
        uiBlockFactory: (UltronWebElement) -> B
    ): B {
        return uiBlockFactory(blockElement.withContextual(element))
    }
}