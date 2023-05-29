package com.atiurin.ultron.custom.espresso.action

import android.view.View
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import org.hamcrest.Matcher
import java.util.concurrent.atomic.AtomicReference

class GetViewAction(val viewContainer: AtomicReference<View>) : ViewAction {
    override fun getConstraints(): Matcher<View> = isAssignableFrom(View::class.java)

    override fun getDescription(): String = "getting view"

    override fun perform(uiController: UiController, view: View) {
        viewContainer.set(view)
    }
}

fun <T> UltronEspressoInteraction<T>.getView(): View {
    val viewContainer = AtomicReference<View>()
    executeAction(
        operationBlock = getInteractionActionBlock(GetViewAction(viewContainer)),
        name = "Get view with '${getInteractionMatcher()}' in root '${getInteractionRootMatcher()}'",
        type = CustomEspressoActionType.GET_VIEW,
        description = "${interaction.className()} get view action '${CustomEspressoActionType.GET_VIEW}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms",
    )
    return viewContainer.get()
}
/**
 * This method provides view of this matcher according to the common espresso idle state mechanism.
 */
fun Matcher<View>.getView() = UltronEspressoInteraction(onView(this)).getView()
fun ViewInteraction.getView() = UltronEspressoInteraction(this).getView()
fun DataInteraction.getView() = UltronEspressoInteraction(this).getView()