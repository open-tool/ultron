package com.atiurin.ultron.custom.espresso.assertion

import android.view.View
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.core.espresso.assertion.EspressoAssertionType
import com.atiurin.ultron.custom.espresso.base.getVisibleRootViews
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matcher

fun <T> UltronEspressoInteraction<T>.doesNotExistInAnyVisibleRoot() : UltronEspressoInteraction<T> {
    val defaultUltronEspressoInteraction = this
    getVisibleRootViews().forEach { root ->
        this.inRoot(withDecorView(`is`(root.decorView)))
        executeAssertion(
            operationBlock = getInteractionAssertionBlock(ViewAssertions.doesNotExist()),
            name = "DoesNotExist of '${getInteractionMatcher()}'",
            type = EspressoAssertionType.DOES_NOT_EXIST,
            description = "${interaction.className()} assertion '${EspressoAssertionType.DOES_NOT_EXIST}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
        )
    }
    return defaultUltronEspressoInteraction
}

fun Matcher<View>.doesNotExistInAnyVisibleRoot() = UltronEspressoInteraction(onView(this)).doesNotExistInAnyVisibleRoot()
fun ViewInteraction.doesNotExistInAnyVisibleRoot() = UltronEspressoInteraction(this).doesNotExistInAnyVisibleRoot()
fun DataInteraction.doesNotExistInAnyVisibleRoot() = UltronEspressoInteraction(this).doesNotExistInAnyVisibleRoot()