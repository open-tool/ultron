package com.atiurin.ultron.core.espresso.viewfinding

import android.view.View
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.ViewInteraction
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.extensions.getRootMatcher
import com.atiurin.ultron.extensions.getTargetMatcher
import com.atiurin.ultron.extensions.getViewFinder
import com.atiurin.ultron.utils.runOnUiThread
import org.hamcrest.Matcher
import java.util.concurrent.atomic.AtomicReference

class UltronViewFinderFinderImpl<T>(val interaction: T): UltronViewFinder {

    override fun findView(viewContainer: AtomicReference<View>) {
        runOnUiThread {
            when (interaction) {
                is ViewInteraction -> viewContainer.set(interaction.getViewFinder()?.view ?: throw NullPointerException("ViewFinder is null"))
                is DataInteraction -> viewContainer.set(
                    onView(interaction.getTargetMatcher())
                    .inRoot(interaction.getRootMatcher()).getViewFinder()?.view ?: throw NullPointerException("ViewFinder is null"))
                else -> throw UltronException("Unknown type of interaction provided!")
            }
        }
    }
}

fun <T> UltronEspressoInteraction<T>.getViewImmediately(): View {
    val viewContainer = AtomicReference<View>()
    executeViewFinding(
        operationBlock = { UltronViewFinderFinderImpl(interaction).findView(viewContainer) },
        name = "Find view with '${getInteractionMatcher()}' in root '${getInteractionRootMatcher()}'",
        type = EspressoViewFindingType.VIEW_SEARCHING,
        description = "${interaction.className()} find view '${EspressoViewFindingType.VIEW_SEARCHING}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms",
    )
    return viewContainer.get()
}

fun ViewInteraction.getViewImmediately() = UltronEspressoInteraction(this).getViewImmediately()
fun DataInteraction.getViewImmediately() = UltronEspressoInteraction(this).getViewImmediately()
fun Matcher<View>.getViewImmediately() = UltronEspressoInteraction(onView(this)).getViewImmediately()