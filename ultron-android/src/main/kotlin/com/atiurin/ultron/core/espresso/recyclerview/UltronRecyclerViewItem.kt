package com.atiurin.ultron.core.espresso.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import com.atiurin.ultron.core.common.assertion.DefaultOperationAssertion
import com.atiurin.ultron.core.common.assertion.OperationAssertion
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import com.atiurin.ultron.custom.espresso.matcher.withSuitableRoot
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.listeners.setListenersState
import org.hamcrest.Matcher

/**
 * Represents the item of RecyclerView list.
 * Provides a set of interaction with item.
 */
open class UltronRecyclerViewItem {
    private var executor: RecyclerViewItemExecutor? = null

    /**
     * Use this constructor to inherit from [UltronRecyclerViewItem]
     * Don't create an instance of subclass. Use [UltronRecyclerView.getItem] instead
     */
    protected constructor()

    fun child(block: () -> Matcher<View>): Lazy<Matcher<View>> = lazy {
        getChild(block())
    }

    fun child(interaction: UltronEspressoInteraction<ViewInteraction>): Lazy<UltronEspressoInteraction<ViewInteraction>> = lazy {
        getChild(interaction)
    }

    constructor(
        ultronRecyclerView: UltronRecyclerView,
        itemViewMatcher: Matcher<View>,
        autoScroll: Boolean = true,
        scrollOffset: Int = 0
    ) {
        setExecutor(ultronRecyclerView, UltronEspressoInteraction(onView(itemViewMatcher)))
        if (autoScroll) scrollToItem(scrollOffset)
    }

    constructor(
        ultronRecyclerView: UltronRecyclerView,
        position: Int,
        autoScroll: Boolean = true,
        scrollOffset: Int = 0
    ) {
        setExecutor(ultronRecyclerView, position)
        if (autoScroll) scrollToItem(scrollOffset)
    }

    fun scrollToItem(offset: Int = 0): UltronRecyclerViewItem = apply {
        executor?.scrollToItem(offset)
    }

    fun getViewHolder(): RecyclerView.ViewHolder? {
        return executor?.getItemViewHolder()
    }

    /**
     * @return matcher to a child element
     */
    fun getChild(childMatcher: Matcher<View>): Matcher<View> {
        if (executor == null) throw UltronException(
            """
            |UltronRecyclerViewItem child element should have lazy initialisation in subclasses. 
            |For example, do not use: 
            |val name = getChild(matcher)
            |Refactor it to:
            |val name by lazy { getChild(matcher) }
        """.trimMargin()
        )
        return executor!!.getItemChildMatcher(childMatcher)
    }

    /**
     * @return interaction to a child element
     */
    fun getChild(childInteraction: UltronEspressoInteraction<ViewInteraction>): UltronEspressoInteraction<ViewInteraction> {
        if (executor == null) throw UltronException(
            """
            |UltronRecyclerViewItem child element should have lazy initialisation in subclasses. 
            |For example, do not use: 
            |val name = getChild(matcher)
            |Refactor it to:
            |val name by lazy { getChild(matcher) }
        """.trimMargin()
        )
        return executor!!.getItemChildInteraction(childInteraction)
    }

    fun withTimeout(timeoutMs: Long) = getInteraction().withTimeout(timeoutMs)
    fun withResultHandler(resultHandler: (EspressoOperationResult<UltronEspressoOperation>) -> Unit) =
        getInteraction().withResultHandler(resultHandler)

    fun withAssertion(assertion: OperationAssertion) = getInteraction().withAssertion(assertion)
    fun withAssertion(name: String = "", isListened: Boolean = false, block: () -> Unit) =
        getInteraction().withAssertion(DefaultOperationAssertion(name, block.setListenersState(isListened)))

    //root view searching
    fun withSuitableRoot() = apply { this.getMatcher().withSuitableRoot() }

    //actions
    fun click() = apply { this.getInteraction().click() }
    fun longClick() = apply { this.getInteraction().longClick() }
    fun doubleClick() = apply { this.getInteraction().doubleClick() }

    fun clickTopLeft(offsetX: Int = 0, offsetY: Int = 0) = apply { this.getInteraction().clickTopLeft(offsetX, offsetY) }
    fun clickTopCenter(offsetY: Int) = apply { this.getInteraction().clickTopCenter(offsetY) }
    fun clickTopRight(offsetX: Int = 0, offsetY: Int = 0) = apply { this.getInteraction().clickTopRight(offsetX, offsetY) }
    fun clickCenterRight(offsetX: Int = 0) = apply { this.getInteraction().clickCenterRight(offsetX) }
    fun clickBottomRight(offsetX: Int = 0, offsetY: Int = 0) = apply { this.getInteraction().clickBottomRight(offsetX, offsetY) }
    fun clickBottomCenter(offsetY: Int = 0) = apply { this.getInteraction().clickBottomCenter(offsetY) }
    fun clickBottomLeft(offsetX: Int = 0, offsetY: Int = 0) = apply { this.getInteraction().clickBottomLeft(offsetX, offsetY) }
    fun clickCenterLeft(offsetX: Int = 0) = apply { this.getInteraction().clickCenterLeft(offsetX) }

    fun swipeDown() = apply { this.getInteraction().swipeDown() }
    fun swipeLeft() = apply { this.getInteraction().swipeLeft() }
    fun swipeRight() = apply { this.getInteraction().swipeRight() }
    fun swipeUp() = apply { this.getInteraction().swipeUp() }
    fun perform(action: ViewAction) = apply { this.getInteraction().perform(action) }

    //assertions
    fun isDisplayed() = apply { this.getInteraction().isDisplayed() }
    fun isNotDisplayed() = apply { this.getInteraction().isNotDisplayed() }
    fun isCompletelyDisplayed() = apply { this.getInteraction().isCompletelyDisplayed() }
    fun isDisplayingAtLeast(percentage: Int) =
        apply { this.getInteraction().isDisplayingAtLeast(percentage) }

    fun isClickable() = apply { this.getInteraction().isClickable() }
    fun isNotClickable() = apply { this.getInteraction().isNotClickable() }
    fun isEnabled() = apply { this.getInteraction().isEnabled() }
    fun isNotEnabled() = apply { this.getInteraction().isNotEnabled() }
    fun assertMatches(condition: Matcher<View>) =
        apply { this.getInteraction().assertMatches(condition) }

    fun hasContentDescription(contentDescription: String) =
        apply { this.getInteraction().hasContentDescription(contentDescription) }

    fun hasContentDescription(resourceId: Int) =
        apply { this.getInteraction().hasContentDescription(resourceId) }

    fun hasContentDescription(charSequenceMatcher: Matcher<CharSequence>) =
        apply { this.getInteraction().hasContentDescription(charSequenceMatcher) }

    fun contentDescriptionContains(text: String) =
        apply { this.getInteraction().contentDescriptionContains(text) }

    fun setExecutor(
        ultronRecyclerView: UltronRecyclerView,
        itemViewInteraction: UltronEspressoInteraction<ViewInteraction>,
    ) {
        this.executor = RecyclerViewItemMatchingExecutor(ultronRecyclerView, itemViewInteraction)
    }

    fun setExecutor(
        ultronRecyclerView: UltronRecyclerView,
        position: Int
    ) {
        this.executor = RecyclerViewItemPositionalExecutor(ultronRecyclerView, position)
    }

    fun getMatcher(): Matcher<View> {
        return executor!!.getItemMatcher()
    }

    fun getInteraction(): UltronEspressoInteraction<ViewInteraction> {
        return executor!!.getItemInteraction()
    }

    companion object {
        inline fun <reified T : UltronRecyclerViewItem> getInstance(
            ultronRecyclerView: UltronRecyclerView,
            itemViewInteraction: UltronEspressoInteraction<ViewInteraction>,
            autoScroll: Boolean = true,
            scrollOffset: Int = 0
        ): T {
            val item = this.createUltronRecyclerViewItemInstance<T>()
            item.setExecutor(ultronRecyclerView, itemViewInteraction)
            if (autoScroll) item.scrollToItem(scrollOffset)
            return item
        }

        inline fun <reified T : UltronRecyclerViewItem> getInstance(
            ultronRecyclerView: UltronRecyclerView,
            position: Int,
            autoScroll: Boolean = true,
            scrollOffset: Int = 0
        ): T {
            val item = this.createUltronRecyclerViewItemInstance<T>()
            item.setExecutor(ultronRecyclerView, position)
            if (autoScroll) item.scrollToItem(scrollOffset)
            return item
        }

        inline fun <reified T : UltronRecyclerViewItem> createUltronRecyclerViewItemInstance(): T {
            return try {
                T::class.java.newInstance()
            } catch (ex: Exception) {
                val desc = when {
                    T::class.isInner -> {
                        "${T::class.simpleName} is an inner class so you have to delete inner modifier (It is often when kotlin throws 'has no zero argument constructor' but real reason is an inner modifier)"
                    }
                    T::class.constructors.find { it.parameters.isEmpty() } == null -> {
                        "${T::class.simpleName} doesn't have a constructor without params (create an empty constructor)"
                    }
                    else -> ex.message
                }
                throw UltronException(
                    """
                    |Couldn't create an instance of ${T::class.simpleName}. 
                    |Possible reason: $desc 
                    |Original exception: ${ex.message}, cause ${ex.cause}
                """.trimMargin()
                )
            }
        }
    }
}