package com.atiurin.ultron.custom.espresso.action

import android.view.View
import androidx.test.espresso.*
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import org.hamcrest.Matcher
import java.util.concurrent.atomic.AtomicReference

class GetContentDescriptionAction(private val textContainer: AtomicReference<String?>) : ViewAction {
    override fun getConstraints(): Matcher<View> = isAssignableFrom(View::class.java)

    override fun getDescription(): String = "getting content description from view"

    override fun perform(uiController: UiController, view: View) {
        textContainer.set(view.contentDescription?.toString())
    }
}

fun <T> UltronEspressoInteraction<T>.getContentDescription(): String? {
    val textContainer = AtomicReference<String?>()
    executeAction(
        operationBlock = getInteractionActionBlock(GetContentDescriptionAction(textContainer)),
        name = "GetContentDescription from view with '${getInteractionMatcher()}'",
        type = CustomEspressoActionType.GET_CONTENT_DESCRIPTION,
        description = "${interaction.className()} action '${CustomEspressoActionType.GET_CONTENT_DESCRIPTION}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms",
    )
    return textContainer.get()
}

fun ViewInteraction.getContentDescription() = UltronEspressoInteraction(this).getContentDescription()
fun DataInteraction.getContentDescription() = UltronEspressoInteraction(this).getContentDescription()
fun Matcher<View>.getContentDescription() = UltronEspressoInteraction(Espresso.onView(this)).getContentDescription()