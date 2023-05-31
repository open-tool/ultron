package com.atiurin.ultron.custom.espresso.base

import android.view.View
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.custom.espresso.action.CustomEspressoActionType.GET_VIEW_FORCIBLY
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.extensions.getRootMatcher
import com.atiurin.ultron.extensions.getTargetMatcher
import com.atiurin.ultron.extensions.getViewFinder
import com.atiurin.ultron.utils.runOnUiThread
import org.hamcrest.Matcher
import java.util.concurrent.atomic.AtomicReference

class UltronViewFinder<T>(val interaction: T) {

    val view: View by lazy {
        when (interaction) {
            is ViewInteraction -> runOnUiThread {
                interaction.getViewFinder()?.view
                    ?: throw NullPointerException("ViewFinder is null")
            }
            is DataInteraction -> runOnUiThread {
                onView(interaction.getTargetMatcher())
                    .inRoot(interaction.getRootMatcher()).getViewFinder()?.view
                    ?: throw NullPointerException("ViewFinder is null")
            }
            else -> throw UltronException("Unknown type of interaction provided!")
        }
    }

}


fun <T> UltronEspressoInteraction<T>.getViewForcibly(): View {
    val viewContainer = AtomicReference<View>()
    executeAction(
        operationBlock = { viewContainer.set(UltronViewFinder(interaction).view) },
        name = "Get view forcibly with '${getInteractionMatcher()}' in root '${getInteractionRootMatcher()}'",
        type = GET_VIEW_FORCIBLY,
        description = "${interaction.className()} get view forcibly action '$GET_VIEW_FORCIBLY' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms",
    )
    return viewContainer.get()
}

/**
 * Returns the view associated with this matcher, bypassing the common Espresso idle state mechanism.
 *
 * The `getViewForcibly()` extension method allows obtaining the view directly, without waiting for Espresso's internal idle state.
 * This is useful in scenarios where the idle state mechanism may cause delays or interfere with certain operations.
 *
 * Compared to the `getView()` method, which works with Espresso's idle state mechanism, `getViewForcibly()` provides immediate access to the view without considering the idle state.
 *
 * @receiver The matcher to obtain the view from.
 * @return The view associated with the matcher.
 */
fun Matcher<View>.getViewForcibly() = UltronEspressoInteraction(onView(this)).getViewForcibly()
fun ViewInteraction.getViewForcibly() = UltronEspressoInteraction(this).getViewForcibly()
fun DataInteraction.getViewForcibly() = UltronEspressoInteraction(this).getViewForcibly()