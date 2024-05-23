package com.atiurin.ultron.custom.espresso.matcher

import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.test.espresso.matcher.BoundedMatcher
import com.atiurin.ultron.utils.getTargetString
import org.hamcrest.Description
import org.hamcrest.Matcher

/**
 * Matcher for [AppCompatTextView]
 * @param textMatcher to match with [AppCompatTextView.getText]
 */
fun withAppCompatText(textMatcher: Matcher<String>): Matcher<View> {
    return object : BoundedMatcher<View, AppCompatTextView>(AppCompatTextView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("withAppCompactText: ")
            textMatcher.describeTo(description)
        }

        override fun matchesSafely(item: AppCompatTextView): Boolean {
            val value = item.text.toString().replace("\n".toRegex(), " ")
            return textMatcher.matches(value)
        }
    }
}

/**
 * Matcher for [AppCompatTextView]
 * @param text to match with [AppCompatTextView.getText]
 */
fun withAppCompatText(text: String): Matcher<View> {
    return object : BoundedMatcher<View, AppCompatTextView>(AppCompatTextView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("withAppCompactText: $text")
        }

        override fun matchesSafely(item: AppCompatTextView): Boolean {
            val value = item.text.toString().replace("\n".toRegex(), " ")
            return text == value
        }
    }
}

/**
 * Matcher for [AppCompatTextView]
 * @param stringId resource id of string to match with [AppCompatTextView.getText]
 */
fun withAppCompatText(@StringRes stringId: Int): Matcher<View> {
    val text = getTargetString(stringId)
    return object : BoundedMatcher<View, AppCompatTextView>(AppCompatTextView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("withAppCompactText: $text")
        }

        override fun matchesSafely(item: AppCompatTextView): Boolean {
            val value = item.text.toString().replace("\n".toRegex(), " ")
            return text == value
        }
    }
}