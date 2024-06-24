package com.atiurin.ultron.custom.espresso.matcher

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Root
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.core.espresso.recyclerview.UltronRecyclerView
import com.atiurin.ultron.extensions.withSuitableRoot
import com.atiurin.ultron.utils.allViews
import com.atiurin.ultron.utils.isVisible
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

fun withSuitableRoot(viewMatcher: Matcher<View>): Matcher<Root> {
    return SuitableRootMatcher(viewMatcher)
}

internal class SuitableRootMatcher(private val viewMatcher: Matcher<View>) :
    TypeSafeMatcher<Root>() {

    override fun describeTo(description: Description) {
        description.appendText("find suitable root view ")
    }

    override fun matchesSafely(root: Root): Boolean {
        return root.decorView.isVisible &&
                root.isReady &&
                root.decorView.allViews.any { viewInRoot -> viewMatcher.matches(viewInRoot) }
    }

}

fun Matcher<View>.withSuitableRoot() = UltronEspressoInteraction(onView(this).withSuitableRoot())
fun UltronRecyclerView.withSuitableRoot() = apply { recyclerViewMatcher.withSuitableRoot() }