package com.atiurin.ultron.recyclerview

import android.view.View
import androidx.test.espresso.ViewAction
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import com.atiurin.ultron.extensions.*
import org.hamcrest.Matcher
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

open class UltronRecyclerViewItem {
    private lateinit var executor: RecyclerViewItemExecutor

    /**
     * Use this constructor to inherit from [UltronRecyclerViewItem]
     * Don't create an instance of subclass. Use [UltronRecyclerView.getItem] instead
     */
    protected constructor()

    constructor(
        recyclerViewMatcher: Matcher<View>,
        itemViewMatcher: Matcher<View>,
        autoScroll: Boolean = true
    ) {
        this.executor = getExecutor(recyclerViewMatcher, itemViewMatcher)
        if (autoScroll) {
            scrollToItem()
        }
    }

    constructor(
        recyclerViewMatcher: Matcher<View>,
        position: Int,
        autoScroll: Boolean = true
    ) {
        executor = getExecutor(recyclerViewMatcher, position)
        if (autoScroll) {
            scrollToItem()
        }
    }

    private fun getExecutor(
        recyclerViewMatcher: Matcher<View>,
        itemViewMatcher: Matcher<View>
    ): RecyclerViewItemExecutor {
        return RecyclerViewItemMatchingExecutor(recyclerViewMatcher, itemViewMatcher)
    }

    private fun getExecutor(
        recyclerViewMatcher: Matcher<View>,
        position: Int
    ): RecyclerViewItemExecutor {
        return RecyclerViewItemPositionalExecutor(recyclerViewMatcher, position)
    }

    fun setExecutor(
        recyclerViewMatcher: Matcher<View>,
        itemViewMatcher: Matcher<View>
    ) {
        this.executor = getExecutor(recyclerViewMatcher, itemViewMatcher)
    }

    fun setExecutor(
        recyclerViewMatcher: Matcher<View>,
        position: Int
    ) {
        this.executor = getExecutor(recyclerViewMatcher, position)
    }

    fun scrollToItem(): UltronRecyclerViewItem = apply {
        executor.scrollToItem()
    }

    private fun get(): Matcher<View> {
        return executor.getItemMatcher()
    }

    /**
     * @return matcher to a child element
     */
    fun getChild(childMatcher: Matcher<View>) : Matcher<View> {
        return executor.getItemChildMatcher(childMatcher)
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

    companion object {
        inline fun <reified T : UltronRecyclerViewItem> getInstance(
            recyclerViewMatcher: Matcher<View>,
            itemViewMatcher: Matcher<View>,
            autoScroll: Boolean = true
        ): T {
            val item = T::class.constructors.find { it.parameters.isEmpty() }?.call()!!
            item.setExecutor(recyclerViewMatcher, itemViewMatcher)
            if (autoScroll) item.scrollToItem()
            return item
        }

        inline fun <reified T : UltronRecyclerViewItem> getInstance(
            recyclerViewMatcher: Matcher<View>,
            position: Int,
            autoScroll: Boolean = true
        ): T {
            val item = T::class.constructors.find { it.parameters.isEmpty() }?.call()!!
            item.setExecutor(recyclerViewMatcher, position)
            if (autoScroll) item.scrollToItem()
            return item
        }
    }
}