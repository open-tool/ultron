package com.atiurin.ultron.core.compose.view

import android.view.View
import androidx.compose.ui.platform.AbstractComposeView
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

object ComposeViewMatcher : TypeSafeMatcher<View>() {
    override fun describeTo(description: Description) {
        description.appendText("View is subtype of androidx.compose.ui.platform.AbstractComposeView")
    }

    override fun matchesSafely(view: View): Boolean = view is AbstractComposeView
}