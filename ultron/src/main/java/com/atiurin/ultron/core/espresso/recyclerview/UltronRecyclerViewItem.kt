package com.atiurin.ultron.core.espresso.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.ViewAction
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.extensions.*
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

    constructor(
        ultronRecyclerView: UltronRecyclerView,
        itemViewMatcher: Matcher<View>,
        autoScroll: Boolean = true
    ) {
        setExecutor(ultronRecyclerView, itemViewMatcher)
        if (autoScroll) scrollToItem()
    }

    constructor(
        ultronRecyclerView: UltronRecyclerView,
        position: Int,
        autoScroll: Boolean = true
    ) {
        setExecutor(ultronRecyclerView, position)
        if (autoScroll) scrollToItem()
    }

    constructor(
        recyclerViewMatcher: Matcher<View>,
        itemViewMatcher: Matcher<View>,
        autoScroll: Boolean = true
    ) {
        setExecutor(UltronRecyclerView(recyclerViewMatcher), itemViewMatcher)
        if (autoScroll) scrollToItem()
    }

    constructor(
        recyclerViewMatcher: Matcher<View>,
        position: Int,
        autoScroll: Boolean = true
    ) {
        setExecutor(UltronRecyclerView(recyclerViewMatcher), position)
        if (autoScroll) scrollToItem()
    }

    fun scrollToItem(): UltronRecyclerViewItem = apply {
        executor?.scrollToItem()
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

    fun withTimeout(timeoutMs: Long) = getMatcher().withTimeout(timeoutMs)
    fun withResultHandler(resultHandler: (EspressoOperationResult<UltronEspressoOperation>) -> Unit) =
        getMatcher().withResultHandler(resultHandler)

    //actions
    fun click() = apply { this.getMatcher().click() }
    fun longClick() = apply { this.getMatcher().longClick() }
    fun doubleClick() = apply { this.getMatcher().doubleClick() }
    fun swipeDown() = apply { this.getMatcher().swipeDown() }
    fun swipeLeft() = apply { this.getMatcher().swipeLeft() }
    fun swipeRight() = apply { this.getMatcher().swipeRight() }
    fun swipeUp() = apply { this.getMatcher().swipeUp() }
    fun perform(action: ViewAction) = apply { this.getMatcher().perform(action) }

    //assertions
    fun isDisplayed() = apply { this.getMatcher().isDisplayed() }
    fun isNotDisplayed() = apply { this.getMatcher().isNotDisplayed() }
    fun isCompletelyDisplayed() = apply { this.getMatcher().isCompletelyDisplayed() }
    fun isDisplayingAtLeast(percentage: Int) = apply { this.getMatcher().isDisplayingAtLeast(percentage) }
    fun isClickable() = apply { this.getMatcher().isClickable() }
    fun isNotClickable() = apply { this.getMatcher().isNotClickable() }
    fun isEnabled() = apply { this.getMatcher().isEnabled() }
    fun isNotEnabled() = apply { this.getMatcher().isNotEnabled() }
    fun assertMatches(condition: Matcher<View>) = apply { this.getMatcher().assertMatches(condition) }
    fun hasContentDescription(contentDescription: String) =
        apply { this.getMatcher().hasContentDescription(contentDescription) }

    fun hasContentDescription(resourceId: Int) =
        apply { this.getMatcher().hasContentDescription(resourceId) }

    fun hasContentDescription(charSequenceMatcher: Matcher<CharSequence>) =
        apply { this.getMatcher().hasContentDescription(charSequenceMatcher) }

    fun contentDescriptionContains(text: String) =
        apply { this.getMatcher().contentDescriptionContains(text) }

    fun setExecutor(
        ultronRecyclerView: UltronRecyclerView,
        itemViewMatcher: Matcher<View>
    ) {
        this.executor = RecyclerViewItemMatchingExecutor(ultronRecyclerView, itemViewMatcher)
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

    companion object {
        inline fun <reified T : UltronRecyclerViewItem> getInstance(
            ultronRecyclerView: UltronRecyclerView,
            itemViewMatcher: Matcher<View>,
            autoScroll: Boolean = true,
            scrollTimeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT
        ): T {
            val item = T::class.java.newInstance()
            item.setExecutor(ultronRecyclerView, itemViewMatcher)
            if (autoScroll) item.scrollToItem()
            return item
        }

        inline fun <reified T : UltronRecyclerViewItem> getInstance(
            ultronRecyclerView: UltronRecyclerView,
            position: Int,
            autoScroll: Boolean = true,
            scrollTimeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT
        ): T {
            val item = T::class.java.newInstance()
            item.setExecutor(ultronRecyclerView, position)
            if (autoScroll) item.scrollToItem()
            return item
        }
    }
}