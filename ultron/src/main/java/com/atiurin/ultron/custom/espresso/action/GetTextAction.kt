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

fun <T> UltronEspressoInteraction<T>.getText(): String = execute(
    UltronEspressoActionParams(
        operationName = "GetText from TextView with '${getInteractionMatcher()}'",
        operationDescription = "${interaction.simpleClassName()} action '${CustomEspressoActionType.GET_TEXT}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms",
        operationType = CustomEspressoActionType.GET_TEXT,
        viewActionDescription = "getting text from TextView",
        viewActionConstraints = isAssignableFrom(TextView::class.java)
    )
) { _, view ->
    (view as TextView).text.toString()
}

fun ViewInteraction.getText() = UltronEspressoInteraction(this).getText()
fun DataInteraction.getText() = UltronEspressoInteraction(this).getText()
fun Matcher<View>.getText() = UltronEspressoInteraction(onView(this)).getText()
