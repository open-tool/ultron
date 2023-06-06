package com.atiurin.ultron.extensions

import android.view.View
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.custom.espresso.action.CustomEspressoActionType.PERFORM_ON_VIEW
import com.atiurin.ultron.custom.espresso.action.CustomEspressoActionType.PERFORM_ON_VIEW_FORCIBLY
import com.atiurin.ultron.custom.espresso.action.getView
import com.atiurin.ultron.custom.espresso.base.UltronViewFinder
import com.atiurin.ultron.utils.runOnUiThread
import org.hamcrest.Matcher

/**
 * Ultron is not responsible for the outcome of these actions.
 * If you use this approach, you clearly understand what you are doing.
 */
fun View.performOnView(action: View.() -> Unit) {
    runOnUiThread {
        action(this)
    }
}

fun <T> UltronEspressoInteraction<T>.performOnView(action: View.() -> Unit) {
    val view = this.getView()
    executeAction(
        operationBlock = { view.performOnView(action) },
        name = "Perform on view with '${getInteractionMatcher()}' in root '${getInteractionRootMatcher()}'",
        type = PERFORM_ON_VIEW,
        description = "${interaction.className()} perform $action on view '$PERFORM_ON_VIEW' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms",
    )
}

fun <T> UltronEspressoInteraction<T>.performOnViewForcibly(action: View.() -> Unit) {
    executeAction(
        operationBlock = { UltronViewFinder(interaction).view.performOnView(action) },
        name = "Perform forcibly on view with '${getInteractionMatcher()}' in root '${getInteractionRootMatcher()}'",
        type = PERFORM_ON_VIEW_FORCIBLY,
        description = "${interaction.className()} perform $action forcibly on view '$PERFORM_ON_VIEW_FORCIBLY' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms",
    )
}

/**
 * Performs the given [action] on the view matched by this matcher on the MainThread.
 *
 * Obtaining of the view is based on the common espresso idle state mechanism.
 */
fun Matcher<View>.performOnView(action: View.() -> Unit) = UltronEspressoInteraction(onView(this)).performOnView(action)
fun ViewInteraction.performOnView(action: View.() -> Unit) = UltronEspressoInteraction(this).performOnView(action)
fun DataInteraction.performOnView(action: View.() -> Unit) = UltronEspressoInteraction(this).performOnView(action)

/**
 * Performs the given [action] on the view matched by this matcher on the MainThread.
 *
 * The difference between `performOnViewForcibly()` and `performOnView()` is that in the first method,
 * obtaining of the view is not bound to the common espresso idle state mechanism.
 */
fun Matcher<View>.performOnViewForcibly(action: View.() -> Unit) = UltronEspressoInteraction(onView(this)).performOnViewForcibly(action)
fun ViewInteraction.performOnViewForcibly(action: View.() -> Unit) = UltronEspressoInteraction(this).performOnViewForcibly(action)
fun DataInteraction.performOnViewForcibly(action: View.() -> Unit) = UltronEspressoInteraction(this).performOnViewForcibly(action)