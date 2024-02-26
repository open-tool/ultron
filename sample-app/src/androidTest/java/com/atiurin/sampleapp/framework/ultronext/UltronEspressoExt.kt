package com.atiurin.sampleapp.framework.ultronext

import android.view.View
import android.widget.TextView
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import com.atiurin.ultron.core.common.UltronOperationType
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.core.espresso.action.UltronEspressoActionParams
import org.hamcrest.Matcher

fun <T> UltronEspressoInteraction<T>.appendText(value: String) = perform(
    UltronEspressoActionParams(
        operationName = "Append text '$value' to ${elementInfo.name}",
        operationDescription = "Some desc",
        operationType = CustomOperationTypes.APPEND_TEXT,
        viewActionConstraints = isAssignableFrom(TextView::class.java)
    )
) { _, view->
    val textView = (view as TextView)
    textView.text = "${textView.text}$value"
}

enum class CustomOperationTypes : UltronOperationType{
    APPEND_TEXT
}

//support action for all Matcher<View>
fun Matcher<View>.appendText(value: String) = UltronEspressoInteraction(onView(this)).appendText(value)

//support action for all ViewInteractions
fun ViewInteraction.appendText(text: String) = UltronEspressoInteraction(this).appendText(text)

//support action for all DataInteractions
fun DataInteraction.appendText(text: String) = UltronEspressoInteraction(this).appendText(text)