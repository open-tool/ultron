package com.atiurin.ultron.core.espresso.action

import android.graphics.Rect
import android.view.View
import android.view.ViewConfiguration
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.CoordinatesProvider
import androidx.test.espresso.action.PrecisionDescriber
import androidx.test.espresso.action.Swiper
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.util.HumanReadables
import org.hamcrest.Matcher
import java.util.Locale

/** Enables swiping across a view. */
class UltronSwipeAction(
    private val swiper: Swiper,
    private val startCoordinatesProvider: CoordinatesProvider,
    private val endCoordinatesProvider: CoordinatesProvider,
    private val precisionDescriber: PrecisionDescriber
    ) : ViewAction {
        companion object {
            /** Maximum number of times to attempt sending a swipe action. */
            const val MAX_TRIES: Int = 3

            /** The minimum amount of a view that must be displayed in order to swipe across it. */
            const val VIEW_DISPLAY_PERCENTAGE: Int = 90

            /**
             * The distance of a swipe's start position from the view's edge, in terms of the view's length.
             * We do not start the swipe exactly on the view's edge, but somewhat more inward, since swiping
             * from the exact edge may behave in an unexpected way (e.g. may open a navigation drawer).
             */
            const val EDGE_FUZZ_FACTOR = 0.083f
        }

    override fun getConstraints(): Matcher<View> {
        return withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
    }

    override fun perform(uiController: UiController, view: View) {
        if (!isDisplayingAtLeast(VIEW_DISPLAY_PERCENTAGE).matches(view)) {
            val rect = Rect()
            view.getDrawingRect(rect)
            view.requestRectangleOnScreen(rect, true) // immediate is set to true, scrolling will not be animated.
            uiController.loopMainThreadUntilIdle()
        }

        if (!isDisplayingAtLeast(VIEW_DISPLAY_PERCENTAGE).matches(view)) {
            throw PerformException.Builder()
                .withActionDescription(this.description)
                .withViewDescription(HumanReadables.describe(view))
                .withCause(
                    java.lang.RuntimeException(
                        "Auto Scroll to view was attempted, but view is not displayed at least <$VIEW_DISPLAY_PERCENTAGE> %"
                    )
                )
                .build()
        }

        val startCoordinates = startCoordinatesProvider.calculateCoordinates(view)
        val endCoordinates = endCoordinatesProvider.calculateCoordinates(view)
        val precision = precisionDescriber.describePrecision()

        var status = Swiper.Status.FAILURE

        var tries = 0
        while (tries < MAX_TRIES && status != Swiper.Status.SUCCESS) {
            try {
                status = swiper.sendSwipe(uiController, startCoordinates, endCoordinates, precision)
            } catch (re: RuntimeException) {
                throw PerformException.Builder()
                    .withActionDescription(this.description)
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(re)
                    .build()
            }

            val duration = ViewConfiguration.getPressedStateDuration().toLong()
            // ensures that all work enqueued to process the swipe has been run.
            if (duration > 0) {
                uiController.loopMainThreadForAtLeast(duration)
            }
            tries++
        }

        if (status == Swiper.Status.FAILURE) {
            throw PerformException.Builder()
                .withActionDescription(description)
                .withViewDescription(HumanReadables.describe(view))
                .withCause(
                    RuntimeException(
                        String.format(
                            Locale.ROOT,
                            "Couldn't swipe from: %s,%s to: %s,%s precision: %s, %s . Swiper: %s "
                                    + "start coordinate provider: %s precision describer: %s. Tried %s times",
                            startCoordinates[0],
                            startCoordinates[1],
                            endCoordinates[0],
                            endCoordinates[1],
                            precision[0],
                            precision[1],
                            swiper,
                            startCoordinatesProvider,
                            precisionDescriber,
                            MAX_TRIES)))
                .build()
        }
    }

    override fun getDescription(): String {
        return swiper.toString().lowercase(Locale.getDefault()) + " swipe"
    }
}