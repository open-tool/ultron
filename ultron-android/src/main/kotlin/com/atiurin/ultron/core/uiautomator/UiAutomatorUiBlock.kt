package com.atiurin.ultron.core.uiautomator

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withParent
import com.atiurin.ultron.page.UiBlock
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

open class UiAutomatorUiBlock(val blockMatcher: Matcher<View>) : UiBlock<Matcher<View>>() {
    override fun _descendantSearch(childMatcher: Matcher<View>): Matcher<View> {
        return allOf(isDescendantOfA(blockMatcher), childMatcher)
    }

    override fun _parentSearch(childMatcher: Matcher<View>): Matcher<View> {
        return allOf(withParent(blockMatcher), childMatcher)
    }
}