package com.atiurin.ultron.core.espresso.page

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withParent
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

open class EspressoUiBlock(open val blockMatcher: Matcher<View>) {
    fun child(deepSearch: Boolean, matcher: () -> Matcher<View>): Matcher<View> {
        return when(deepSearch){
            true -> allOf(isDescendantOfA(blockMatcher), matcher())
            false -> allOf(withParent(blockMatcher), matcher())
        }
    }
}