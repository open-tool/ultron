package com.atiurin.ultron.custom.espresso.base

import android.view.View
import android.widget.AdapterView
import androidx.test.espresso.AmbiguousViewMatcherException
import androidx.test.espresso.AmbiguousViewMatcherException.Builder
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.util.TreeIterables
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.custom.espresso.action.CustomEspressoActionType.GET_VIEW_FORCIBLY
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.extensions.getTargetMatcher
import com.atiurin.ultron.extensions.getViewMatcher
import com.atiurin.ultron.extensions.simpleClassName
import com.atiurin.ultron.utils.runOnUiThread
import org.hamcrest.Matcher
import java.util.Locale
import java.util.concurrent.atomic.AtomicReference

class UltronViewFinder<T>(val interaction: T) {

    private val viewMatcher: Matcher<View> = when (interaction) {
        is ViewInteraction -> interaction.getViewMatcher()
            ?: throw NullPointerException("Matcher<View> is null")

        is DataInteraction -> interaction.getTargetMatcher()
            ?: throw NullPointerException("Matcher<View> is null")

        else -> throw UltronException("Unknown type of interaction provided!")
    }
    private val root: View by lazy {
        getVisibleRootViews().find { it.isReady }?.decorView
            ?: throw UltronException("There is no root View in ready and visible state")
    }

    val view: View by lazy {
        runOnUiThread {
            fetchView()
        }
    }

    @Throws(AmbiguousViewMatcherException::class, NoMatchingViewException::class)
    fun fetchView(): View {
        Checker.checkMainThread()
        val matchedViewIterator =
            filter(TreeIterables.breadthFirstViewTraversal(root), viewMatcher).iterator()
        var matchedView: View? = null

        while (matchedViewIterator.hasNext()) {
            if (matchedView != null) {
                // Ambiguous!
                throw Builder()
                    .withViewMatcher(viewMatcher)
                    .withRootView(root)
                    .withView1(matchedView)
                    .withView2(matchedViewIterator.next())
                    .withOtherAmbiguousViews(
                        *toArray(
                            matchedViewIterator,
                            View::class.java
                        )
                    )
                    .build()
            } else {
                matchedView = matchedViewIterator.next()
            }
        }
        if (null == matchedView) {
            val adapterViews =
                filterToList(
                    TreeIterables.breadthFirstViewTraversal(root),
                    isAssignableFrom(AdapterView::class.java)
                )

            if (adapterViews.isEmpty()) {
                throw NoMatchingViewException.Builder()
                    .withViewMatcher(viewMatcher)
                    .withRootView(root)
                    .build()
            }

            val warning = String.format(
                Locale.ROOT,
                """
                    If the target view is not part of the view hierarchy, you may need to use Espresso.onData to load it from one of the following AdapterViews:%s
                """.trimIndent(),
                joinToString(adapterViews, "\n- ")
            )
            throw NoMatchingViewException.Builder()
                .withViewMatcher(viewMatcher)
                .withRootView(root)
                .withAdapterViews(adapterViews)
                .withAdapterViewWarning(warning)
                .build()
        } else {
            return matchedView
        }
    }


}


fun <T> UltronEspressoInteraction<T>.getViewForcibly(): View {
    val viewContainer = AtomicReference<View>()
    executeAction(
        operationBlock = { viewContainer.set(UltronViewFinder(interaction).view) },
        name = "Get view forcibly with '${getInteractionMatcher()}' in root '${getInteractionRootMatcher()}'",
        type = GET_VIEW_FORCIBLY,
        description = "${interaction.simpleClassName()} get view forcibly action '$GET_VIEW_FORCIBLY' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms",
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