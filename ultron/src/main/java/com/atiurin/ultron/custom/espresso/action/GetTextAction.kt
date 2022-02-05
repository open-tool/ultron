package com.atiurin.ultron.custom.espresso.action

import android.view.View
import android.widget.TextView
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.config.UltronConfig.Espresso.Companion.ACTION_TIMEOUT
import com.atiurin.ultron.core.espresso.UltronEspresso.executeAction
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import org.hamcrest.Matcher
import java.util.concurrent.atomic.AtomicReference

class GetTextAction(val textContainer: AtomicReference<String>) : ViewAction {
    override fun getConstraints(): Matcher<View> = isAssignableFrom(TextView::class.java)

    override fun getDescription(): String = "getting text from TextView"

    override fun perform(uiController: UiController, view: View) {
        textContainer.set((view as TextView).text.toString())
    }
}

fun <T> UltronEspressoInteraction<T>.getText() : String {
    val timeout = timeoutMs ?: ACTION_TIMEOUT
    val textContainer = AtomicReference<String>()
    executeAction(
        UltronEspressoOperation(
            operationBlock = getInteractionActionBlock(GetTextAction(textContainer)),
            name = "GetText from TextView with '${getInteractionMatcher()}'",
            type = CustomEspressoActionType.GET_TEXT,
            description = "${interaction!!::class.java.simpleName} action '${CustomEspressoActionType.GET_TEXT}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during $timeout ms",
            timeoutMs = timeout
        ),
        resultHandler = resultHandler
            ?: UltronConfig.Espresso.ViewActionConfig.resultHandler
    )
    return textContainer.get()
}

fun ViewInteraction.getText() = UltronEspressoInteraction(this).getText()
fun DataInteraction.getText() = UltronEspressoInteraction(this).getText()
fun Matcher<View>.getText() = UltronEspressoInteraction(onView(this)).getText()
