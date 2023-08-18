package com.atiurin.ultron.custom.espresso.action

import android.view.View
import androidx.test.espresso.*
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.core.espresso.action.UltronEspressoActionParams
import com.atiurin.ultron.extensions.simpleClassName
import org.hamcrest.Matcher
import java.util.concurrent.atomic.AtomicReference

fun <T> UltronEspressoInteraction<T>.getContentDescription(): String? = execute(
    UltronEspressoActionParams(
        operationName = "GetContentDescription from view with '${getInteractionMatcher()}'",
        operationDescription ="${interaction.simpleClassName()} action '${CustomEspressoActionType.GET_CONTENT_DESCRIPTION}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms",
        operationType = CustomEspressoActionType.GET_CONTENT_DESCRIPTION,
        viewActionConstraints = isAssignableFrom(View::class.java),
        viewActionDescription = "getting content description from view"
    )
) { _, view ->
    view.contentDescription?.toString()
}

fun ViewInteraction.getContentDescription() = UltronEspressoInteraction(this).getContentDescription()
fun DataInteraction.getContentDescription() = UltronEspressoInteraction(this).getContentDescription()
fun Matcher<View>.getContentDescription() = UltronEspressoInteraction(Espresso.onView(this)).getContentDescription()
