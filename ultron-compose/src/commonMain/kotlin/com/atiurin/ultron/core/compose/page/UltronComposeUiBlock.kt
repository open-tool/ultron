package com.atiurin.ultron.core.compose.page

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasParent
import com.atiurin.ultron.extensions.withName

/**
 * Base class for creating and manipulating UI blocks in Compose testing.
 *
 * This class provides mechanisms for performing descendant and direct child searches within the Compose UI hierarchy,
 * allowing for efficient UI element matching and composition of UI blocks. It focuses on finding elements
 * within the scope of this block, not across the entire Compose hierarchy.
 *
 * @param blockMatcher The [SemanticsMatcher] used to identify this UI block.
 * @param blockDescription A textual description of the UI block, used for better logging and debugging.
 */
open class UltronComposeUiBlock(val blockMatcher: SemanticsMatcher, val blockDescription: String = "") {

    /**
     * Searches for a descendant element within the Compose UI block.
     *
     * Constructs a matcher that represents a descendant element of this block,
     * based on the provided `childMatcher`.
     *
     * @param childMatcher Matcher to locate the descendant element within this block.
     * @return A matcher for the descendant element of this block.
     */
    fun _descendantSearch(childMatcher: SemanticsMatcher): SemanticsMatcher {
        return hasAnyAncestor(blockMatcher) and childMatcher
    }

    /**
     * Searches for a direct child element of the current Compose UI block.
     *
     * Constructs a matcher that represents a direct child element of this block,
     * based on the provided `childMatcher`.
     *
     * @param childMatcher Matcher to locate the direct child element within this block.
     * @return A matcher for the direct child element of this block.
     */
    fun _childSearch(childMatcher: SemanticsMatcher): SemanticsMatcher {
        return hasParent(blockMatcher) and childMatcher
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
     * Applies a descendant or direct child search transformation to the given `childMatcher`.
     *
     * @param childMatcher Matcher to be modified.
     * @param descendantSearch Specifies the type of search:
     *                         - `true` (default): Performs a descendant search.
     *                         - `false`: Performs a direct child search.
     * @return A new matcher modified according to the search type.
     */
    fun child(childMatcher: SemanticsMatcher, descendantSearch: Boolean = true): SemanticsMatcher = when (descendantSearch) {
        true -> _descendantSearch(childMatcher)
        false -> _childSearch(childMatcher)
    }

    /**
     * Creates a child UI block using a matcher modified based on the specified search type.
     *
     * Applies a descendant or direct child search transformation to the provided `childMatcher`
     * and constructs a new UI block instance using the `uiBlockFactory`.
     *
     * @param childMatcher Matcher to be modified for the child UI block.
     * @param descendantSearch Specifies the type of search:
     *                         - `true` (default): Performs a descendant search.
     *                         - `false`: Performs a direct child search.
     * @param uiBlockFactory A factory function to create a new instance of the child UI block.
     *                       The function takes the modified matcher as input and returns a UI block instance.
     * @return A new instance of the child UI block, constructed with the modified matcher.
     */
    fun <B : UltronComposeUiBlock> child(childMatcher: SemanticsMatcher, descendantSearch: Boolean = true, uiBlockFactory: (SemanticsMatcher) -> B): B {
        val newMatcher = when (descendantSearch) {
            true -> _descendantSearch(childMatcher)
            false -> _childSearch(childMatcher)
        }
        return uiBlockFactory(newMatcher)
    }
}

