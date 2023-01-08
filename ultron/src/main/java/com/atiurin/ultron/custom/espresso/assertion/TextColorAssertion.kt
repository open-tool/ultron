package com.atiurin.ultron.custom.espresso.assertion

import android.view.View
import androidx.annotation.ColorRes
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.custom.espresso.matcher.textViewHasCurrentHintTextColor
import org.hamcrest.Matcher
import com.atiurin.ultron.custom.espresso.matcher.textViewHasCurrentTextColor
import com.atiurin.ultron.custom.espresso.matcher.textViewHasHighlightColor
import com.atiurin.ultron.custom.espresso.matcher.textViewHasShadowColor
import com.atiurin.ultron.utils.getColorHex
import com.atiurin.ultron.utils.getTargetColor

/**
 * Assert TextView has current text color = [colorResId]
 */
fun <T> UltronEspressoInteraction<T>.hasCurrentTextColor(@ColorRes colorResId: Int) = apply {
    val hexColor = getColorHex(getTargetColor(colorResId))
    executeAssertion(
        operationBlock = getInteractionAssertionBlock(textViewHasCurrentTextColor(colorResId)),
        name = "Has current text color $hexColor in '${getInteractionMatcher()}'",
        type = CustomEspressoAssertionType.HAS_CURRENT_TEXT_COLOR,
        description = "${interaction.className()} assertion '${CustomEspressoAssertionType.HAS_CURRENT_TEXT_COLOR}' with color resource = '$hexColor' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms",
    )
}

fun DataInteraction.hasCurrentTextColor(@ColorRes colorRes: Int) = UltronEspressoInteraction(this).hasCurrentTextColor(colorRes)
fun ViewInteraction.hasCurrentTextColor(@ColorRes colorRes: Int) = UltronEspressoInteraction(this).hasCurrentTextColor(colorRes)
fun Matcher<View>.hasCurrentTextColor(@ColorRes colorRes: Int) = UltronEspressoInteraction(Espresso.onView(this)).hasCurrentTextColor(colorRes)

/**
 * Assert TextView has current hint text color = [colorResId]
 */
fun <T> UltronEspressoInteraction<T>.hasCurrentHintTextColor(@ColorRes colorResId: Int) = apply {
    val hexColor = getColorHex(getTargetColor(colorResId))
    executeAssertion(
        operationBlock = getInteractionAssertionBlock(textViewHasCurrentHintTextColor(colorResId)),
        name = "Has current hint text color $hexColor in '${getInteractionMatcher()}'",
        type = CustomEspressoAssertionType.HAS_CURRENT_HINT_TEXT_COLOR,
        description = "${interaction.className()} assertion '${CustomEspressoAssertionType.HAS_CURRENT_HINT_TEXT_COLOR}' with color resource = '$hexColor' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms",
    )
}

fun DataInteraction.hasCurrentHintTextColor(@ColorRes colorRes: Int) = UltronEspressoInteraction(this).hasCurrentHintTextColor(colorRes)
fun ViewInteraction.hasCurrentHintTextColor(@ColorRes colorRes: Int) = UltronEspressoInteraction(this).hasCurrentHintTextColor(colorRes)
fun Matcher<View>.hasCurrentHintTextColor(@ColorRes colorRes: Int) = UltronEspressoInteraction(Espresso.onView(this)).hasCurrentHintTextColor(colorRes)

/**
 * Assert TextView has highlight color = [colorResId]
 */
fun <T> UltronEspressoInteraction<T>.hasHighlightColor(@ColorRes colorResId: Int) = apply {
    val hexColor = getColorHex(getTargetColor(colorResId))
    executeAssertion(
        operationBlock = getInteractionAssertionBlock(textViewHasHighlightColor(colorResId)),
        name = "Has highlight color $hexColor in '${getInteractionMatcher()}'",
        type = CustomEspressoAssertionType.HAS_HIGHLIGHT_COLOR,
        description = "${interaction.className()} assertion '${CustomEspressoAssertionType.HAS_HIGHLIGHT_COLOR}' with color resource = '$hexColor' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms",
    )
}

fun DataInteraction.hasHighlightColor(@ColorRes colorRes: Int) = UltronEspressoInteraction(this).hasHighlightColor(colorRes)
fun ViewInteraction.hasHighlightColor(@ColorRes colorRes: Int) = UltronEspressoInteraction(this).hasHighlightColor(colorRes)
fun Matcher<View>.hasHighlightColor(@ColorRes colorRes: Int) = UltronEspressoInteraction(Espresso.onView(this)).hasHighlightColor(colorRes)


/**
* Assert TextView has shadow color = [colorResId]
*/
fun <T> UltronEspressoInteraction<T>.hasShadowColor(@ColorRes colorResId: Int) = apply {
    val hexColor = getColorHex(getTargetColor(colorResId))
    executeAssertion(
        operationBlock = getInteractionAssertionBlock(textViewHasShadowColor(colorResId)),
        name = "Has shadow color $hexColor in '${getInteractionMatcher()}'",
        type = CustomEspressoAssertionType.HAS_SHADOW_COLOR,
        description = "${interaction.className()} assertion '${CustomEspressoAssertionType.HAS_SHADOW_COLOR}' with color resource = '$hexColor' in '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getAssertionTimeout()} ms",
    )
}

fun DataInteraction.hasShadowColor(@ColorRes colorRes: Int) = UltronEspressoInteraction(this).hasShadowColor(colorRes)
fun ViewInteraction.hasShadowColor(@ColorRes colorRes: Int) = UltronEspressoInteraction(this).hasShadowColor(colorRes)
fun Matcher<View>.hasShadowColor(@ColorRes colorRes: Int) = UltronEspressoInteraction(Espresso.onView(this)).hasShadowColor(colorRes)
