package com.atiurin.ultron.page


/**
 * Abstract base class for defining UI blocks with generic matchers.
 *
 * This class provides mechanisms for descendant and parent searches, allowing subclasses to define how
 * child matchers should be derived from the current matcher.
 *
 * @param M The type of matcher used to identify elements within the UI block.
 */
abstract class UiBlock<M> {

    /**
     * Performs a descendant search within the UI hierarchy to find a child element.
     *
     * @param childMatcher The matcher to identify the child element within UI block.
     * @return A modified matcher for the child element based on a descendant search.
     */
    abstract fun _descendantSearch(childMatcher: M): M

    /**
     * Performs a parent search within the UI hierarchy to find a child element.
     *
     * @param childMatcher The matcher to identify the child element within UI block.
     * @return A modified matcher for the child element based on a parent search.
     */
    abstract fun _parentSearch(childMatcher: M): M

    /**
     * Creates a child matcher based on the current matcher.
     *
     * @param childMatcher The matcher to identify the child element within UI block.
     * @param descendantSearch Whether to perform a descendant search for the child matcher. Defaults to `true`.
     * @return A modified matcher for the child element.
     */
    fun child(childMatcher: M, descendantSearch: Boolean = true): M = when (descendantSearch) {
        true -> _descendantSearch(childMatcher)
        false -> _parentSearch(childMatcher)
    }

    /**
     * Creates a child UI block using a custom UI block factory.
     *
     * The factory function receives a modified matcher that corresponds to the correct parent or ancestor,
     * ensuring that the created UI block is scoped to the appropriate part of the UI hierarchy.
     *
     * @param childMatcher The matcher to identify the child element within UI block.
     * @param uiBlockFactory A factory function to create the child UI block. The function receives the modified matcher.
     * @param descendantSearch Whether to perform a descendant search for the child matcher. Defaults to `true`.
     * @return A new instance of the child UI block.
     */
    fun <B : UiBlock<M>> child(
        childMatcher: M,
        uiBlockFactory: (M) -> B,
        descendantSearch: Boolean = true
    ): B {
        val newMatcher = when (descendantSearch) {
            true -> _descendantSearch(childMatcher)
            false -> _parentSearch(childMatcher)
        }
        return uiBlockFactory(newMatcher)
    }
}