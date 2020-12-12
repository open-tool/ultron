package com.atiurin.ultron.recyclerview

import android.view.View
import androidx.test.espresso.ViewAction
import com.atiurin.ultron.extensions.*
import org.hamcrest.Matcher

open class RecyclerViewItem {
    var executor: RecyclerViewItemExecutor

    constructor(
        recyclerViewMatcher: Matcher<View>,
        itemViewMatcher: Matcher<View>,
        autoScroll: Boolean = true
    ) {
        executor = RecyclerViewItemMatchingExecutor(recyclerViewMatcher, itemViewMatcher)
        if (autoScroll) {
            scrollToItem()
        }
    }

    constructor(recyclerViewMatcher: Matcher<View>, position: Int, autoScroll: Boolean = true) {
        executor = RecyclerViewItemPositionalExecutor(recyclerViewMatcher, position)
        if (autoScroll) {
            scrollToItem()
        }
    }

    fun scrollToItem(): RecyclerViewItem = apply {
        executor.scrollToItem()
    }

    private fun get(): Matcher<View> {
        return executor.getItemMatcher()
    }

    fun getChildMatcher(childMatcher: Matcher<View>): Matcher<View> {
        return executor.getItemChildMatcher(childMatcher)
    }

    //actions
    fun click() = apply { this.get().click() }
    fun longClick() = apply { this.get().longClick() }
    fun doubleClick() = apply { this.get().doubleClick() }
    fun swipeDown() = apply { this.get().swipeDown() }
    fun swipeLeft() = apply { this.get().swipeLeft() }
    fun swipeRight() = apply { this.get().swipeRight() }
    fun swipeUp() = apply { this.get().swipeUp() }
    fun execute(action: ViewAction) = apply { this.get().execute(action) }

    //assertions
    fun isDisplayed() = apply { this.get().isDisplayed() }
    fun isCompletelyDisplayed() = apply { this.get().isCompletelyDisplayed() }
    fun isDisplayingAtLeast(percentage: Int) = apply { this.get().isDisplayingAtLeast(percentage) }
    fun isClickable() = apply { this.get().isClickable() }
    fun assertMatches(condition: Matcher<View>) = apply { this.get().assertMatches(condition) }

}