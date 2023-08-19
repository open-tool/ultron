package com.atiurin.ultron.custom.espresso.action

import android.view.View
import android.widget.TextView
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.core.espresso.action.UltronEspressoActionParams
import com.atiurin.ultron.extensions.simpleClassName
import org.hamcrest.Matcher
import java.util.concurrent.atomic.AtomicReference

fun <T> UltronEspressoInteraction<T>.getView(): View = execute(
    UltronEspressoActionParams(
        operationName = "Get view with '${getInteractionMatcher()}'",
        operationDescription = "${interaction.simpleClassName()} action '${CustomEspressoActionType.GET_VIEW}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms",
        operationType = CustomEspressoActionType.GET_VIEW,
        viewActionDescription = "getting view",
        viewActionConstraints = isAssignableFrom(View::class.java)
    )
) { _, view ->
    view
}

/**
 * Gets the view that is matched by the matcher.
 *
 * This method is bound to the common Espresso idle state mechanism. This means that it will
 * wait for the view to become visible before returning it.
 *
 * @return The view that is matched by the matcher.
 *
 * In case you need to bypass Espresso idle state mechanism use `getViewForcibly()` extension method.
 */
fun Matcher<View>.getView() = UltronEspressoInteraction(onView(this)).getView()
fun ViewInteraction.getView() = UltronEspressoInteraction(this).getView()
fun DataInteraction.getView() = UltronEspressoInteraction(this).getView()
