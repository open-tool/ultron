package com.atiurin.ultron.core.compose

import androidx.compose.ui.test.SemanticsMatcher
import com.atiurin.ultron.core.compose.page.UltronComposeUiBlock
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.log.UltronLog
import kotlin.reflect.typeOf

/**
 * Creates a child UI block of a specified type using a matcher modified based on the given search type.
 *
 * This extension function simplifies the creation of a new instance of a child UI block by automatically invoking
 * the appropriate constructor of the specified type [T]. The matcher of the current block is transformed
 * to locate the desired child block based on the specified `descendantSearch` parameter.
 *
 * @param T The type of the child UI block to be created. It must extend [UltronComposeUiBlock].
 * @param uiBlock The existing instance of the child UI block to use as a template for the new block.
 *                The `blockMatcher` and `blockDescription` properties of this block are used to create the new instance.
 * @param descendantSearch Specifies the type of search:
 *                         - `true` (default): Performs a descendant search.
 *                         - `false`: Performs a direct child search.
 * @return A new instance of the specified type [T], initialized with the updated matcher.
 *
 * @throws UltronException If the specified class [T] does not have an appropriate constructor or cannot be instantiated.
 *
 * ### Requirements:
 * The class [T] must meet the following conditions to be instantiated:
 * 1. It must not be a nested or inner class. It should be defined at the top level or as a file-level class.
 * 2. It must have one of the following constructors:
 *    - A constructor with one parameter of type [SemanticsMatcher]:
 *      `class CustomBlock(blockMatcher: SemanticsMatcher)`
 *    - A constructor with two parameters: `blockMatcher` of type [SemanticsMatcher] and `blockDescription` of type [String]:
 *      `class CustomBlock(blockMatcher: SemanticsMatcher, blockDescription: String)`
 *
 * If neither constructor is available, consider using an alternative method for child creation,
 * such as `child(childMatcher, descendantSearch, uiBlockFactory)`.
 *
 * ### Usage Example:
 * ```kotlin
 * // ui elements declaration
 * class CustomElementBlock(blockMatcher: SemanticsMatcher, blockDescription: String = "") : UltronComposeUiBlock(blockMatcher, blockDescription)
 *
 * class CustomComplexBlock(blockMatcher: SemanticsMatcher, blockDescription: String = "") : UltronComposeUiBlock(blockMatcher, blockDescription){
 *      val custom = child(CustomElementBlock(hasTestTag(elementTag), "custom element child of '$blockDescription'"))
 * }
 * // screen object
 * object MyScreen : Screen<MyScreen>(){
 *      val complexBlock = CustomComplexBlock(hasTestTag("bigUiBlockTag"), "Presentable description of UI block")
 * }
 *
 * // somewhere in test
 * MyScreen {
 *     complexBlock.custom.assertIsDisplayed()
 * }
 * ```
 */
inline fun <reified T : UltronComposeUiBlock> UltronComposeUiBlock.child(
    uiBlock: T,
    descendantSearch: Boolean = true
): T {
    val newMatcher = when (descendantSearch) {
        true -> _descendantSearch(uiBlock.blockMatcher)
        false -> _childSearch(uiBlock.blockMatcher)
    }
    val updateBlock = runCatching {
        T::class.constructors.forEach { constructor ->
            UltronLog.info("Constructor: $constructor, Parameters: ${constructor.parameters.map { it.type }}")
        }
        T::class.constructors.firstOrNull {
            it.parameters.size == 2 && it.parameters.first().type == typeOf<SemanticsMatcher>()
                    && it.parameters[1].type == typeOf<String>()
        }?.let { constructor ->
            return@runCatching constructor.call(newMatcher, uiBlock.blockDescription)
        }
        T::class.constructors.firstOrNull {
            it.parameters.size == 1 && it.parameters.first().type == typeOf<SemanticsMatcher>()
        }?.let {
            return@runCatching it.call(newMatcher)
        }
        null
    }.onFailure {
        if (it is IllegalArgumentException) {
            throw UltronException(
                "${T::class.simpleName} has hidden java constructor parameters. ${T::class.simpleName} must be defined as a top-level class (not nested inside any other class)."
            )
        } else throw UltronException("Unable to create updated ${T::class.simpleName}. Message: ${it.message}")
    }.getOrNull()
    updateBlock?.let {
        return it
    } ?: throw UltronException(
        """ |${T::class.simpleName} doesn't have an appropriate constructor with arguments: (SemanticsMatcher, String) or (SemanticsMatcher)  
            |Ensure that the class meets the following conditions:
            |1. ${T::class.simpleName} must not be defined inside another class. It should be a top-level or file-level class.
            |2. ${T::class.simpleName} must have one of the following constructors:
            |- A constructor with one parameter of type SemanticsMatcher:
            |class ${T::class.simpleName}(blockMatcher: SemanticsMatcher) : UltronComposeUiBlock(blockMatcher)
            |- A constructor with two parameters: blockMatcher of type SemanticsMatcher and blockDescription of type String:
            |class ${T::class.simpleName}(blockMatcher: SemanticsMatcher, blockDescription: String) : UltronComposeUiBlock(blockMatcher, blockDescription)
            |If neither constructor is available, consider using another method for child declaration:
            |fun <B : UltronComposeUiBlock> child(childMatcher: SemanticsMatcher, descendantSearch: Boolean = true, uiBlockFactory: (SemanticsMatcher) -> B): B
        """.trimIndent()
    )
}