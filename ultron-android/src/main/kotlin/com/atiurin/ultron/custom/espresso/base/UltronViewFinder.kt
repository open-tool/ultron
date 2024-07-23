package com.atiurin.ultron.custom.espresso.base

import android.os.Looper
import android.view.View
import android.widget.AdapterView
import androidx.test.espresso.AmbiguousViewMatcherException
import androidx.test.espresso.AmbiguousViewMatcherException.Builder
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.util.EspressoOptional
import androidx.test.espresso.util.TreeIterables
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.custom.espresso.action.CustomEspressoActionType.GET_VIEW_FORCIBLY
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.extensions.getTargetMatcher
import com.atiurin.ultron.extensions.getViewMatcher
import com.atiurin.ultron.extensions.simpleClassName
import com.atiurin.ultron.utils.runOnUiThread
import com.google.common.base.Joiner
import com.google.common.base.Preconditions
import com.google.common.base.Predicate
import com.google.common.collect.Iterables
import com.google.common.collect.Iterators
import com.google.common.collect.Lists
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
        checkMainThread()
        val matcherPredicate: Predicate<View> =
            MatcherPredicateAdapter(Preconditions.checkNotNull(viewMatcher))
        val matchedViewIterator: Iterator<View> =
            Iterables.filter(TreeIterables.breadthFirstViewTraversal(root), matcherPredicate)
                .iterator()
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
                        *Iterators.toArray(
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
            val adapterViewPredicate: Predicate<View> =
                MatcherPredicateAdapter(
                    ViewMatchers.isAssignableFrom(
                        AdapterView::class.java
                    )
                )
            val adapterViews: List<View> = Lists.newArrayList(
                Iterables.filter(
                    TreeIterables.breadthFirstViewTraversal(root),
                    adapterViewPredicate
                ).iterator()
            )
            if (adapterViews.isEmpty()) {
                throw NoMatchingViewException.Builder()
                    .withViewMatcher(viewMatcher)
                    .withRootView(root)
                    .build()
            }
            val warning = String.format(
                Locale.ROOT,
                "\n"
                        + "If the target view is not part of the view hierarchy, you may need to use"
                        + " Espresso.onData to load it from one of the following AdapterViews:%s",
                Joiner.on("\n- ").join(adapterViews)
            )
            throw NoMatchingViewException.Builder()
                .withViewMatcher(viewMatcher)
                .withRootView(root)
                .withAdapterViews(adapterViews)
                .withAdapterViewWarning(EspressoOptional.of(warning))
                .build()
        } else {
            return matchedView
        }
    }

    private fun checkMainThread() {
        Preconditions.checkState(
            (Thread.currentThread() == Looper.getMainLooper().thread),
            "Executing a query on the view hierarchy outside of the main thread (on: %s)",
            Thread.currentThread().name
        )
    }

    private class MatcherPredicateAdapter<T>(matcher: Matcher<in T>) : Predicate<T> {
        private val matcher: Matcher<in T>

        init {
            this.matcher = Preconditions.checkNotNull(matcher)
        }

        override fun apply(input: T): Boolean {
            return matcher.matches(input)
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