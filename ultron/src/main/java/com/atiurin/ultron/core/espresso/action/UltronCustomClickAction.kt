package com.atiurin.ultron.core.espresso.action

import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.webkit.WebView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.*
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import androidx.test.espresso.util.HumanReadables
import org.hamcrest.Matcher
import java.util.*


class UltronCustomClickAction(
    private val tapper: Tapper,
    private val coordinatesProvider: CoordinatesProvider,
    private val precisionDescriber: PrecisionDescriber = Press.THUMB,
    private val inputDevice: Int = InputDevice.SOURCE_UNKNOWN,
    private val buttonState: Int = MotionEvent.BUTTON_PRIMARY,
    private val areaPercentage: Int,
    /**
     * negative value for LEFT direction, positive value for RIGHT direction
     */
    private val offsetX: Int = 0,
    /**
     * negative value for TOP direction, positive value for BOTTOM direction
     */
    private val offsetY: Int = 0
) : ViewAction {

    override fun getConstraints(): Matcher<View> {
        return isDisplayingAtLeast(areaPercentage)
    }

    override fun getDescription(): String {
        return tapper.toString().lowercase(Locale.ROOT) + " click";
    }

    override fun perform(uiController: UiController, view: View) {
        val coordinates = coordinatesProvider
            .calculateCoordinates(view)
            .apply {
                this[0] += offsetX.toFloat()
                this[1] += offsetY.toFloat()
            }
        val precision = precisionDescriber.describePrecision()

        var status = Tapper.Status.FAILURE
        var loopCount = 0
        // Native event injection is quite a tricky process. A tap is actually 2
        // seperate motion events which need to get injected into the system. Injection
        // makes an RPC call from our app under test to the Android system server, the
        // system server decides which window layer to deliver the event to, the system
        // server makes an RPC to that window layer, that window layer delivers the event
        // to the correct UI element, activity, or window object. Now we need to repeat
        // that 2x. for a simple down and up. Oh and the down event triggers timers to
        // detect whether or not the event is a long vs. short press. The timers are
        // removed the moment the up event is received (NOTE: the possibility of eventTime
        // being in the future is totally ignored by most motion event processors).
        //
        // Phew.
        //
        // The net result of this is sometimes we'll want to do a regular tap, and for
        // whatever reason the up event (last half) of the tap is delivered after long
        // press timeout (depending on system load) and the long press behaviour is
        // displayed (EG: show a context menu). There is no way to avoid or handle this more
        // gracefully. Also the longpress behavour is app/widget specific. So if you have
        // a seperate long press behaviour from your short press, you can pass in a
        // 'RollBack' SmartViewAction which when executed will undo the effects of long press.

        // Native event injection is quite a tricky process. A tap is actually 2
        // seperate motion events which need to get injected into the system. Injection
        // makes an RPC call from our app under test to the Android system server, the
        // system server decides which window layer to deliver the event to, the system
        // server makes an RPC to that window layer, that window layer delivers the event
        // to the correct UI element, activity, or window object. Now we need to repeat
        // that 2x. for a simple down and up. Oh and the down event triggers timers to
        // detect whether or not the event is a long vs. short press. The timers are
        // removed the moment the up event is received (NOTE: the possibility of eventTime
        // being in the future is totally ignored by most motion event processors).
        //
        // Phew.
        //
        // The net result of this is sometimes we'll want to do a regular tap, and for
        // whatever reason the up event (last half) of the tap is delivered after long
        // press timeout (depending on system load) and the long press behaviour is
        // displayed (EG: show a context menu). There is no way to avoid or handle this more
        // gracefully. Also the longpress behavour is app/widget specific. So if you have
        // a seperate long press behaviour from your short press, you can pass in a
        // 'RollBack' SmartViewAction which when executed will undo the effects of long press.
        val action = "perform: ${
            String.format(
                "%s - At Coordinates: %d, %d and precision: %d, %d",
                this.description,
                coordinates[0].toInt(),
                coordinates[1].toInt(),
                precision[0].toInt(),
                precision[1].toInt()
            )
        }"

        while (status != Tapper.Status.SUCCESS && loopCount < 3) {
            try {
                status = tapper.sendTap(
                    uiController, coordinates, precision, inputDevice, buttonState
                )
            } catch (re: RuntimeException) {
                throw PerformException.Builder()
                    .withActionDescription(
                        String.format(
                            "%s - At Coordinates: %d, %d and precision: %d, %d",
                            this.description,
                            coordinates[0].toInt(),
                            coordinates[1].toInt(),
                            precision[0].toInt(),
                            precision[1].toInt()
                        )
                    )
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(re)
                    .build()
            }
            val duration = ViewConfiguration.getPressedStateDuration()
            // ensures that all work enqueued to process the tap has been run.
            if (duration > 0) {
                uiController.loopMainThreadForAtLeast(duration.toLong())
            }
            if (status == Tapper.Status.WARNING) {
                break
            }
            loopCount++
        }
        if (status == Tapper.Status.FAILURE) {
            val actionFail = String.format(
                "Couldn't "
                        + "click at: %s,%s precision: %s, %s . Tapper: %s coordinate provider: %s precision "
                        + "describer: %s. Tried %s times.",
                coordinates[0],
                coordinates[1],
                precision[0],
                precision[1],
                tapper,
                coordinatesProvider,
                precisionDescriber,
                loopCount
            )

            throw PerformException.Builder()
                .withActionDescription(this.description)
                .withViewDescription(HumanReadables.describe(view))
                .withCause(RuntimeException(actionFail)).build()
        }

        if (tapper === Tap.SINGLE && view is WebView) {
            // WebViews will not process click events until double tap
            // timeout. Not the best place for this - but good for now.
            uiController.loopMainThreadForAtLeast(ViewConfiguration.getDoubleTapTimeout().toLong())
        }
    }
}