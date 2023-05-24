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

fun <T> UltronEspressoInteraction<T>.findViewForcibly(): View {
    val viewContainer = AtomicReference<View>()
    executeAction(
        operationBlock = { viewContainer.set(UltronViewFinder(interaction).view) },
        name = "Find view with '${getInteractionMatcher()}' in root '${getInteractionRootMatcher()}'",
        type = GET_VIEW_FORCIBLY,
        description = "${interaction.className()} find view '$GET_VIEW_FORCIBLY' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms",
    )
    return viewContainer.get()
}

fun ViewInteraction.findViewForcibly() = UltronEspressoInteraction(this).findViewForcibly()
fun DataInteraction.findViewForcibly() = UltronEspressoInteraction(this).findViewForcibly()
fun Matcher<View>.findViewForcibly() = UltronEspressoInteraction(onView(this)).findViewForcibly()
