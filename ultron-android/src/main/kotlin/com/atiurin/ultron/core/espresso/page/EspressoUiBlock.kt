package com.atiurin.ultron.core.espresso.page

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withParent
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.page.UiBlock
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import kotlin.reflect.typeOf

/**
 * Base class for creating and manipulating UI blocks in Espresso testing.
 *
 * This class provides mechanisms for performing descendant and parent searches within the UI hierarchy,
 * allowing for efficient UI element matching and composition of UI blocks. It focuses on finding elements
 * within the scope of this block, not across the entire UI hierarchy.
 *
 * @param blockMatcher The matcher used to identify this UI block.
 */
open class EspressoUiBlock(val blockMatcher: Matcher<View>) : UiBlock<Matcher<View>>() {
    /**
     * Performs a descendant search within the UI hierarchy to find a child element.
     *
     * @param childMatcher The matcher to identify the child element.
     * @return A matcher that matches the child element that is a descendant of this block.
     */
    override fun _descendantSearch(childMatcher: Matcher<View>): Matcher<View> {
        return allOf(isDescendantOfA(blockMatcher), childMatcher)
    }

    /**
     * Performs a parent search within the UI hierarchy to find a child element.
     *
     * @param childMatcher The matcher to identify the child element.
     * @return A matcher that matches the child element that is a direct parent of this block.
     */
    override fun _parentSearch(childMatcher: Matcher<View>): Matcher<View> {
        return allOf(withParent(blockMatcher), childMatcher)
    }

    /**
     * Creates a child UI block by reconstructing the block with a matcher modified for this block.
     *
     * This method requires the UI block to have a constructor that accepts a single `Matcher<View>` parameter.
     * If no such constructor exists, an exception is thrown.
     *
     * @param uiBlock The UI block to be created as a child.
     * @param descendantSearch Whether to perform a descendant search for the child matcher. Defaults to `true`.
     * @return A new instance of the child UI block.
     * @throws UltronException If the specified class does not have a compatible constructor.
     */
    inline fun <reified T : EspressoUiBlock> child(
        uiBlock: T,
        descendantSearch: Boolean = true
    ): T {
        val newMatcher = when (descendantSearch) {
            true -> _descendantSearch(uiBlock.blockMatcher)
            false -> _parentSearch(uiBlock.blockMatcher)
        }

        val constructor = T::class.constructors.firstOrNull {
            it.parameters.size == 1 && it.parameters.first().type == typeOf<Matcher<View>>()
        } ?: throw UltronException("""
            |UiBlock subclass ${T::class.simpleName} doesn't has a constructor with single Matcher<View> argument. 
            |Try following code: 
            |class ${T::class.simpleName}(parent: Matcher<View>) : EspressoUiBlock(parent) {
        """.trimMargin())

        return constructor.call(newMatcher)
    }
}