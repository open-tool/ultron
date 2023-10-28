package com.atiurin.ultron.custom.espresso.action

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.core.espresso.action.UltronEspressoActionParams
import com.atiurin.ultron.extensions.simpleClassName
import org.hamcrest.Matcher

fun <T> UltronEspressoInteraction<T>.getDrawable(): Drawable? = execute(
    UltronEspressoActionParams(
        operationName = "GetDrawable from TextView with '${getInteractionMatcher()}'",
        operationDescription = "${interaction.simpleClassName()} action '${CustomEspressoActionType.GET_DRAWABLE}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms",
        operationType = CustomEspressoActionType.GET_DRAWABLE,
        viewActionConstraints = isAssignableFrom(ImageView::class.java),
        viewActionDescription = "getting Drawable from ImageView"
    )
) { _, view ->
    (view as ImageView).drawable
}

fun ViewInteraction.getDrawable() = UltronEspressoInteraction(this).getDrawable()
fun DataInteraction.getDrawable() = UltronEspressoInteraction(this).getDrawable()
fun Matcher<View>.getDrawable() = UltronEspressoInteraction(Espresso.onView(this)).getDrawable()
