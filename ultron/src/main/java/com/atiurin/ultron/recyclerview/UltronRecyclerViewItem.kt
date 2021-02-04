package com.atiurin.ultron.recyclerview

import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import com.atiurin.ultron.core.espresso.UltronInteraction
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.extensions.*
import org.hamcrest.Matcher
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

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
        autoScroll: Boolean = true,
        scrollTimeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT
    ) {
        setExecutor(ultronRecyclerView, itemViewMatcher , scrollTimeoutMs)
        if (autoScroll) scrollToItem()
    }

    constructor(
        ultronRecyclerView: UltronRecyclerView,
        position: Int,
        autoScroll: Boolean = true,
        scrollTimeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT
    ) {
        setExecutor(ultronRecyclerView, position, scrollTimeoutMs)
        if (autoScroll) scrollToItem()
    }

    constructor(
        recyclerViewMatcher: Matcher<View>,
        itemViewMatcher: Matcher<View>,
        autoScroll: Boolean = true,
        scrollTimeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT
    ) {
        setExecutor(UltronRecyclerView(recyclerViewMatcher), itemViewMatcher, scrollTimeoutMs)
        if (autoScroll) scrollToItem()
    }

    constructor(
        recyclerViewMatcher: Matcher<View>,
        position: Int,
        autoScroll: Boolean = true,
        scrollTimeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT
    ) {
        setExecutor(UltronRecyclerView(recyclerViewMatcher), position, scrollTimeoutMs)
        if (autoScroll) scrollToItem()
    }

    fun scrollToItem(): UltronRecyclerViewItem = apply {
        executor?.scrollToItem()
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

    fun withTimeout(timeoutMs: Long) = get().withTimeout(timeoutMs)
    fun withResultHandler(resultHandler: (EspressoOperationResult<UltronEspressoOperation>) -> Unit) =
        get().withResultHandler(resultHandler)

    //actions
    fun click() = apply { this.get().click() }
    fun longClick() = apply { this.get().longClick() }
    fun doubleClick() = apply { this.get().doubleClick() }
    fun swipeDown() = apply { this.get().swipeDown() }
    fun swipeLeft() = apply { this.get().swipeLeft() }
    fun swipeRight() = apply { this.get().swipeRight() }
    fun swipeUp() = apply { this.get().swipeUp() }
    fun perform(action: ViewAction) = apply { this.get().perform(action) }

    //assertions
    fun isDisplayed() = apply { this.get().isDisplayed() }
    fun isNotDisplayed() = apply { this.get().isNotDisplayed() }
    fun isCompletelyDisplayed() = apply { this.get().isCompletelyDisplayed() }
    fun isDisplayingAtLeast(percentage: Int) = apply { this.get().isDisplayingAtLeast(percentage) }
    fun isClickable() = apply { this.get().isClickable() }
    fun isNotClickable() = apply { this.get().isNotClickable() }
    fun isEnabled() = apply { this.get().isEnabled() }
    fun isNotEnabled() = apply { this.get().isNotEnabled() }
    fun assertMatches(condition: Matcher<View>) = apply { this.get().assertMatches(condition) }
    fun hasContentDescription(contentDescription: String) =
        apply { this.get().hasContentDescription(contentDescription) }

    fun hasContentDescription(resourceId: Int) =
        apply { this.get().hasContentDescription(resourceId) }

    fun hasContentDescription(charSequenceMatcher: Matcher<CharSequence>) =
        apply { this.get().hasContentDescription(charSequenceMatcher) }

    fun contentDescriptionContains(text: String) =
        apply { this.get().contentDescriptionContains(text) }

    fun setExecutor(
        ultronRecyclerView: UltronRecyclerView,
        itemViewMatcher: Matcher<View>,
        scrollTimeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT
    ) {
        this.executor = RecyclerViewItemMatchingExecutor(ultronRecyclerView, itemViewMatcher, scrollTimeoutMs)
    }

    fun setExecutor(
        ultronRecyclerView: UltronRecyclerView,
        position: Int,
        scrollTimeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT
    ) {
        this.executor = RecyclerViewItemPositionalExecutor(ultronRecyclerView, position, scrollTimeoutMs)
    }

    private fun get(): Matcher<View> {
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
            item.setExecutor(ultronRecyclerView, itemViewMatcher, scrollTimeoutMs)
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
            item.setExecutor(ultronRecyclerView, position, scrollTimeoutMs)
            if (autoScroll) item.scrollToItem()
            return item
        }
    }
}