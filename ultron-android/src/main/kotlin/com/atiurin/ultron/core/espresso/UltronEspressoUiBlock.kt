package com.atiurin.ultron.core.espresso

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withParent
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.extensions.withName
import com.atiurin.ultron.log.UltronLog
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import kotlin.reflect.typeOf

/**
 * Base class for creating and manipulating UI blocks in Espresso testing.
 *
 * This class provides mechanisms for performing searches within the UI hierarchy,
 * allowing for efficient UI element matching and composition of UI blocks.
 * It supports both descendant and parent searches, focusing on elements within
 * the scope of this block.
 *
 * @param blockMatcher The matcher used to identify this UI block.
 * @param blockDescription A textual description of the UI block, used for better logging and debugging.
 */
open class UltronEspressoUiBlock(val blockMatcher: Matcher<View>, val blockDescription: String = "") {
    /**
     * Searches for a descendant element within the Espresso UI block.
     *
     * Constructs a matcher that represents a descendant element of this block,
     * based on the provided `childMatcher`.
     *
     * @param childMatcher Matcher to locate the descendant element within this block.
     * @return A matcher for the descendant element of this block.
     */
    fun _descendantSearch(childMatcher: Matcher<View>): Matcher<View> {
        return allOf(isDescendantOfA(blockMatcher), childMatcher)
    }

    /**
     * Searches for a direct child element of the current Espresso UI block.
     *
     * Constructs a matcher that represents a direct child element of this block,
     * based on the provided `childMatcher`.
     *
     * @param childMatcher Matcher to locate the direct child element within this block.
     * @return A matcher for the direct child element of this block.
     */
    fun _childSearch(childMatcher: Matcher<View>): Matcher<View> {
        return allOf(withParent(blockMatcher), childMatcher)
    }

    /**
     * A utility property for interaction with this UI block.
     *
     * Appends the block description to the matcher for better logging and debugging.
     */
    val uiBlock
        get() = blockMatcher.withName(blockDescription)

    /**
     * Modifies the provided matcher based on the specified search type.
     *
     * This method applies a descendant or child search transformation to the given `childMatcher`.
     * It is useful for updating matchers to narrow down or expand the scope of UI element matching.
     *
     * @param childMatcher The original matcher to be modified.
     * @param descendantSearch Determines the type of search transformation:
     *                         - `true` (default): Performs a descendant search.
     *                         - `false`: Performs a direct child search.
     * @return A new matcher that has been modified according to the search type.
     */
    fun child(childMatcher: Matcher<View>, descendantSearch: Boolean = true): Matcher<View> = when (descendantSearch) {
        true -> _descendantSearch(childMatcher)
        false -> _childSearch(childMatcher)
    }

    /**
     * Creates a child UI block using a matcher modified based on the specified search type.
     *
     * This method applies a descendant or parent search transformation to the provided `childMatcher`
     * and constructs a new UI block instance using the `uiBlockFactory`.
     *
     * @param childMatcher The matcher to be modified for the child UI block.
     * @param descendantSearch Determines the type of search transformation:
     *                         - `true` (default): Performs a descendant search.
     *                         - `false`: Performs a direct child search.
     * @param uiBlockFactory A factory function to create a new instance of the child UI block. The function
     *                       takes the modified matcher as input and returns an instance of the UI block.
     * @return A new instance of the child UI block, constructed with the modified matcher.
     */
    fun <B : UltronEspressoUiBlock> child(childMatcher: Matcher<View>, descendantSearch: Boolean = true, uiBlockFactory: (Matcher<View>) -> B): B {
        val newMatcher = when (descendantSearch) {
            true -> _descendantSearch(childMatcher)
            false -> _childSearch(childMatcher)
        }
        return uiBlockFactory(newMatcher)
    }

    /**
     * Creates a child UI block of a specified type using a matcher modified based on the given search type.
     *
     * This extension function simplifies the creation of a new instance of a child UI block by automatically invoking
     * the appropriate constructor of the specified type [T]. The matcher of the current block is transformed
     * to locate the desired child block based on the specified `descendantSearch` parameter.
     *
     * @param T The type of the child UI block to be created. It must extend [UltronEspressoUiBlock].
     * @param uiBlock The existing instance of the child UI block to use as a template for the new block.
     *                The `blockMatcher` and `blockDescription` properties of this block are used to create the new instance.
     * @param descendantSearch Specifies the type of search:
     *                         - `true` (default): Performs a descendant search.
     *                         - `false`: Performs a direct parent search.
     * @return A new instance of the specified type [T], initialized with the updated matcher.
     *
     * @throws UltronException If the specified class [T] does not have an appropriate constructor or cannot be instantiated.
     *
     * ### Constructor Requirements:
     * The class [T] must meet the following conditions to be instantiated:
     * 1. It must not be a nested or inner class. It should be defined at the top level or as a file-level class.
     * 2. It must have one of the following constructors:
     *    - A constructor with one parameter of type [Matcher<View>]:
     *      `class CustomBlock(blockMatcher: Matcher<View>)`
     *    - A constructor with two parameters: `blockMatcher` of type [Matcher<View>] and `blockDescription` of type [String]:
     *      `class CustomBlock(blockMatcher: Matcher<View>, blockDescription: String)`
     *
     * If neither constructor is available, consider using an alternative method for child creation,
     * such as `child(childMatcher, descendantSearch, uiBlockFactory)`.
     *
     * ### Alternative Method:
     * If this method does not meet your requirements, consider using the universal method:
     * ```kotlin
     * fun <B : UltronEspressoUiBlock> child(
     *     childMatcher: Matcher<View>,
     *     descendantSearch: Boolean = true,
     *     uiBlockFactory: (Matcher<View>) -> B
     * ): B
     * ```
     *
     * ### Usage Example:
     * ```kotlin
     * // UI block declaration
     * class CustomElementBlock(blockMatcher: Matcher<View>, blockDescription: String = "") : UltronEspressoUiBlock(blockMatcher, blockDescription)
     *
     * class CustomComplexBlock(blockMatcher: Matcher<View>, blockDescription: String = "") : UltronEspressoUiBlock(blockMatcher, blockDescription) {
     *     val custom = child(CustomElementBlock(withId(R.id.custom_id), "Custom child block"))
     * }
     *
     * // Screen object
     * object MyScreen : Screen<MyScreen>() {
     *     val complexBlock = CustomComplexBlock(withContentDescription("Complex Block"), "Descriptive UI Block")
     * }
     *
     * // In a test
     * MyScreen {
     *     complexBlock.custom.performClick()
     * }
     * ```
     */
    inline fun <reified T : UltronEspressoUiBlock> child(
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
                it.parameters.size == 2 && it.parameters.first().type == typeOf<Matcher<View>>()
                        && it.parameters[1].type == typeOf<String>()
            }?.let { constructor ->
                return@runCatching constructor.call(newMatcher, uiBlock.blockDescription)
            }
            T::class.constructors.firstOrNull {
                it.parameters.size == 1 && it.parameters.first().type == typeOf<Matcher<View>>()
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
            """ |${T::class.simpleName} doesn't have an appropriate constructor with arguments: (Matcher<View>, String) or (Matcher<View>)  
                |Ensure that the class meets the following conditions:
                |1. ${T::class.simpleName} must not be defined inside another class. It should be a top-level or file-level class.
                |2. ${T::class.simpleName} must have one of the following constructors:
                |- A constructor with one parameter of type Matcher<View>:
                |class ${T::class.simpleName}(blockMatcher: Matcher<View>) : UltronEspressoUiBlock(blockMatcher)
                |- A constructor with two parameters: blockMatcher of type Matcher<View> and blockDescription of type String:
                |class ${T::class.simpleName}(blockMatcher: Matcher<View>, blockDescription: String) : UltronEspressoUiBlock(blockMatcher, blockDescription)
                |If neither constructor is available, consider using another method for child declaration:
                |fun <B : UltronEspressoUiBlock> child(childMatcher: Matcher<View>, descendantSearch: Boolean = true, uiBlockFactory: (Matcher<View>) -> B): B
            """.trimIndent()
        )
    }
}