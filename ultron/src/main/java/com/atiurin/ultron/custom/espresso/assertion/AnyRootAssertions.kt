package com.atiurin.ultron.custom.espresso.assertion

import android.view.View
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.core.espresso.assertion.EspressoAssertionType
import com.atiurin.ultron.custom.espresso.base.getRootViewsList
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matcher

fun <T> UltronEspressoInteraction<T>.doesNotExistInAnyRoot() = apply {
    executeAssertion(
        operationBlock = {
            getRootViewsList().forEach { root ->
                this@doesNotExistInAnyRoot.apply {
                    inRoot(RootMatchers.withDecorView(`is`(root.decorView)))
                    getInteractionAssertionBlock(ViewAssertions.doesNotExist())
                }
            }
        },
        name = "DoesNotExist of '${getInteractionMatcher()}'",
        type = EspressoAssertionType.DOES_NOT_EXIST,
        description = "${interaction.className()} assertion '${EspressoAssertionType.DOES_NOT_EXIST}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms"
    )
}

fun Matcher<View>.doesNotExistInAnyRoot() = UltronEspressoInteraction(onView(this)).doesNotExistInAnyRoot()
fun ViewInteraction.doesNotExistInAnyRoot() = UltronEspressoInteraction(this).doesNotExistInAnyRoot()
fun DataInteraction.doesNotExistInAnyRoot() = UltronEspressoInteraction(this).doesNotExistInAnyRoot()