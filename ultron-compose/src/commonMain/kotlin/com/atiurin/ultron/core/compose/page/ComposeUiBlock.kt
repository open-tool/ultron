package com.atiurin.ultron.core.compose.page

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasParent
import com.atiurin.ultron.page.UiBlock

/**
 * Base class for creating and manipulating UI blocks in Compose testing.
 *
 * This class provides mechanisms for performing descendant and parent searches within the Compose UI hierarchy,
 * allowing for efficient UI element matching and composition of UI blocks. It focuses on finding elements
 * within the scope of this block, not across the entire Compose hierarchy.
 *
 * @param blockMatcher The [SemanticsMatcher] used to identify this UI block.
 */
open class ComposeUiBlock(val blockMatcher: SemanticsMatcher) : UiBlock<SemanticsMatcher>() {

    /**
     * Performs a descendant search within the Compose UI block to find a child element.
     *
     * This method constructs a new matcher that represents a child element within the scope of the current block,
     * where the child element is a descendant of the current block as defined by the provided `childMatcher`.
     *
     * @param childMatcher The matcher that specifies how to locate the child element within this block.
     * @return A matcher for the child element that is a descendant of this block.
     */
    override fun _descendantSearch(childMatcher: SemanticsMatcher): SemanticsMatcher {
        return hasAnyAncestor(blockMatcher) and childMatcher
    }

    /**
     * Performs a parent search within the Compose UI block to find a child element.
     *
     * This method constructs a new matcher that represents a child element within the scope of the current block,
     * where the child element is a direct parent of the current block as defined by the provided `childMatcher`.
     *
     * @param childMatcher The matcher that specifies how to locate the child element within this block.
     * @return A matcher for the child element that is a direct parent of this block.
     */
    override fun _parentSearch(childMatcher: SemanticsMatcher): SemanticsMatcher {
        return hasParent(blockMatcher) and childMatcher
    }
}

