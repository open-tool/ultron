package com.atiurin.ultron.custom.espresso.assertion

import android.view.View
import androidx.annotation.DrawableRes
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.UltronEspresso
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import com.atiurin.ultron.custom.espresso.matcher.withDrawable
import com.atiurin.ultron.utils.getTargetResourceName
import org.hamcrest.Matcher

fun <T> UltronEspressoInteraction<T>.hasDrawable(@DrawableRes resourceId: Int) = apply {
    val timeout = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
    val resName = getTargetResourceName(resourceId)
    UltronEspresso.executeAssertion(
        UltronEspressoOperation(
            operationBlock = getInteractionAssertionBlock(withDrawable(resourceId)),
            name = "HasDrawable with target resource $resName in '${getInteractionMatcher()}'",
            type = CustomEspressoAssertionType.HAS_DRAWABLE,
            description = "${interaction!!::class.java.simpleName} assertion '${CustomEspressoAssertionType.HAS_DRAWABLE}' with drawable resource '$resName' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeout ms",
            timeoutMs = timeout
        ),
        resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
    )
}

/**
 * Assert view has any drawable
 */
fun <T> UltronEspressoInteraction<T>.hasAnyDrawable() = apply {
    val timeout = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT
    UltronEspresso.executeAssertion(
        UltronEspressoOperation(
            operationBlock = getInteractionAssertionBlock(com.atiurin.ultron.custom.espresso.matcher.hasAnyDrawable()),
            name = "HasAnyDrawable in '${getInteractionMatcher()}'",
            type = CustomEspressoAssertionType.HAS_ANY_DRAWABLE,
            description = "${interaction!!::class.java.simpleName} assertion '${CustomEspressoAssertionType.HAS_ANY_DRAWABLE}' with any drawable in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeout ms",
            timeoutMs = timeout
        ),
        resultHandler = resultHandler ?: UltronConfig.Espresso.ViewAssertionConfig.resultHandler
    )
}

fun DataInteraction.hasDrawable(@DrawableRes resourceId: Int) = UltronEspressoInteraction(this).hasDrawable(resourceId)
fun ViewInteraction.hasDrawable(@DrawableRes resourceId: Int) = UltronEspressoInteraction(this).hasDrawable(resourceId)
fun Matcher<View>.hasDrawable(@DrawableRes resourceId: Int) = UltronEspressoInteraction(Espresso.onView(this)).hasDrawable(resourceId)

fun DataInteraction.hasAnyDrawable() = UltronEspressoInteraction(this).hasAnyDrawable()
fun ViewInteraction.hasAnyDrawable() = UltronEspressoInteraction(this).hasAnyDrawable()
fun Matcher<View>.hasAnyDrawable() = UltronEspressoInteraction(Espresso.onView(this)).hasAnyDrawable()