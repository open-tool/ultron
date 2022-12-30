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
import org.hamcrest.Matcher
import java.util.concurrent.atomic.AtomicReference

class GetTextAction(val textContainer: AtomicReference<String>) : ViewAction {
    override fun getConstraints(): Matcher<View> = isAssignableFrom(TextView::class.java)

    override fun getDescription(): String = "getting text from TextView"

    override fun perform(uiController: UiController, view: View) {
        textContainer.set((view as TextView).text.toString())
    }
}

fun <T> UltronEspressoInteraction<T>.getText(): String {
    val textContainer = AtomicReference<String>()
    executeAction(
        operationBlock = getInteractionActionBlock(GetTextAction(textContainer)),
        name = "GetText from TextView with '${getInteractionMatcher()}'",
        type = CustomEspressoActionType.GET_TEXT,
        description = "${interaction.className()} action '${CustomEspressoActionType.GET_TEXT}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms",
    )
    return textContainer.get()
}

fun ViewInteraction.getText() = UltronEspressoInteraction(this).getText()
fun DataInteraction.getText() = UltronEspressoInteraction(this).getText()
fun Matcher<View>.getText() = UltronEspressoInteraction(onView(this)).getText()

