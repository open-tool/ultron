package com.atiurin.ultron.extensions

import android.view.View
import androidx.test.espresso.util.TreeIterables
import org.hamcrest.Matcher

internal fun View.findChildView(matcher: Matcher<View>): View? {
    var childView: View? = null
    for (child in TreeIterables.breadthFirstViewTraversal(this)) {
        if (matcher.matches(child)) {
            childView = child
            break
        }
    }
    return childView
}