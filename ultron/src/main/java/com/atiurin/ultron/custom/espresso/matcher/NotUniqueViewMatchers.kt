package com.atiurin.ultron.custom.espresso.matcher

import android.view.View
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Should be the last description of element
 */
fun Matcher<View>.first(): Matcher<View> {
    val initialMatcher = this
    return object : TypeSafeMatcher<View>(View::class.java) {
        var isFirstMatchedItemFound = false
        override fun describeTo(description: Description) {
            description.appendText("first matched view: ")
            initialMatcher.describeTo(description)
        }

        override fun matchesSafely(item: View): Boolean {
            return if (!isFirstMatchedItemFound) {
                isFirstMatchedItemFound = initialMatcher.matches(item)
                isFirstMatchedItemFound
            }
            else false
        }
    }
}

/**
 * Should be the last description of element
 * @param number of matched view in hierarchy. Starts from 0
 */
fun Matcher<View>.hierarchyNumber(number: Int): Matcher<View> {
    val initialMatcher = this
    return object : TypeSafeMatcher<View>(View::class.java) {
        var lastMatchedItemNumber = -1
        override fun describeTo(description: Description) {
            description.appendText("first matched view: ")
            initialMatcher.describeTo(description)
        }

        override fun matchesSafely(item: View): Boolean {
            val isMatched = initialMatcher.matches(item)
            if (isMatched) lastMatchedItemNumber++
            return (lastMatchedItemNumber == number && isMatched)
        }
    }
}