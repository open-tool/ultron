package com.atiurin.ultron.core.espresso.action

import android.util.Log
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import android.widget.SearchView
import androidx.test.espresso.InjectEventSecurityException
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.TypeTextAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.remote.annotation.RemoteMsgConstructor
import androidx.test.espresso.remote.annotation.RemoteMsgField
import androidx.test.espresso.util.HumanReadables
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import java.util.Locale

/** Enables typing text on views.  */
class UltronTypeTextAction (
    @RemoteMsgField(order = 0) val stringToBeTyped: String,
    @RemoteMsgField(order = 1) val tapToFocus: Boolean = true,
    // The click action to use when tapping to focus is needed before typing in text.
    private val clickAction: ViewAction? = defaultClickAction()
) :
    ViewAction {

    /**
     * Constructs [androidx.test.espresso.action.TypeTextAction] with given string. If the string is empty it results in no-op
     * (nothing is typed).
     *
     * @param stringToBeTyped String To be typed by [androidx.test.espresso.action.TypeTextAction]
     * @param tapToFocus indicates whether a tap should be sent to the underlying view before typing.
     */
    @RemoteMsgConstructor
    constructor(stringToBeTyped: String, tapToFocus: Boolean) : this(
        stringToBeTyped,
        tapToFocus,
        null
    )

    override fun getConstraints(): Matcher<View> {
        var matchers = Matchers.allOf(ViewMatchers.isDisplayed())
        if (!tapToFocus) {
            matchers = Matchers.allOf(matchers, ViewMatchers.hasFocus())
        }
        // SearchView does not support input methods itself (rather it delegates to an internal text
        // view for input).
        return Matchers.allOf(
            matchers, Matchers.anyOf(
                ViewMatchers.supportsInputMethods(), ViewMatchers.isAssignableFrom(
                    SearchView::class.java
                )
            )
        )
    }

    override fun perform(uiController: UiController, view: View) {
        // No-op if string is empty.
        if (stringToBeTyped.isEmpty()) {
            Log.w(TAG, "Supplied string is empty resulting in no-op (nothing is typed).")
            return
        }

        if (tapToFocus) {
            // Perform a click.
            if (clickAction == null) {
                // Uses the default click action if none is specified.
                defaultClickAction().perform(uiController, view)
            } else {
                clickAction.perform(uiController, view)
            }
            uiController.loopMainThreadUntilIdle()
        }

        try {
            if (!uiController.injectString(stringToBeTyped)) {
                Log.e(
                    TAG,
                    "Failed to type text: $stringToBeTyped"
                )
                throw PerformException.Builder()
                    .withActionDescription(this.description)
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(RuntimeException("Failed to type text: $stringToBeTyped"))
                    .build()
            }
        } catch (e: InjectEventSecurityException) {
            Log.e(
                TAG,
                "Failed to type text: $stringToBeTyped"
            )
            throw PerformException.Builder()
                .withActionDescription(this.description)
                .withViewDescription(HumanReadables.describe(view))
                .withCause(e)
                .build()
        }
    }

    override fun getDescription(): String {
        return String.format(Locale.ROOT, "type text(%s)", stringToBeTyped)
    }

    companion object {
        private val TAG: String = TypeTextAction::class.java.simpleName

        private fun defaultClickAction(): GeneralClickAction {
            return GeneralClickAction(
                Tap.SINGLE,
                GeneralLocation.CENTER,
                Press.FINGER,
                InputDevice.SOURCE_UNKNOWN,
                MotionEvent.BUTTON_PRIMARY
            )
        }
    }
}