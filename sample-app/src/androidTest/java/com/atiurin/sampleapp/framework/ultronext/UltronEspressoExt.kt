package com.atiurin.sampleapp.framework.ultronext

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.core.espresso.action.UltronEspressoActionParams
import com.atiurin.ultron.core.espresso.recyclerview.UltronRecyclerView
import com.atiurin.ultron.core.espresso.recyclerview.UltronRecyclerViewItem
import com.atiurin.ultron.custom.espresso.action.CustomEspressoActionType
import com.atiurin.ultron.extensions.simpleClassName
import org.hamcrest.Matcher

fun <T> UltronEspressoInteraction<T>.appendText(value: String) = perform(
    params = UltronEspressoActionParams(
        operationName = "Append text '$value' to ${getInteractionMatcher()}",
        operationDescription = "Awesome description"
    )
) { _, view ->
    val textView = (view as TextView)
    textView.text = "${textView.text}$value"
}

//support action for all Matcher<View>
fun Matcher<View>.appendText(value: String) = UltronEspressoInteraction(onView(this)).appendText(value)

//support action for all ViewInteractions
fun ViewInteraction.appendText(text: String) = UltronEspressoInteraction(this).appendText(text)

//support action for all DataInteractions
fun DataInteraction.appendText(text: String) = UltronEspressoInteraction(this).appendText(text)

//support action for RecyclerView list. appendText action is useless for RecyclerView.
// This is just an example of how to add new behaviour for UltronRecyclerView
fun UltronRecyclerView.appendText(text: String) = apply { recyclerViewMatcher.appendText(text) }

//support action for RecyclerView item
fun UltronRecyclerViewItem.appendText(text: String) = apply { getMatcher().appendText(text) }

// assertion example
fun <T> UltronEspressoInteraction<T>.assertChecked(expectedState: Boolean) = assertMatches { view ->
    (view as CheckBox).isChecked == expectedState
}
fun Matcher<View>.assertChecked(expectedState: Boolean) = UltronEspressoInteraction(onView(this)).assertChecked(expectedState)
fun ViewInteraction.assertChecked(expectedState: Boolean) = UltronEspressoInteraction(this).assertChecked(expectedState)
fun DataInteraction.assertChecked(expectedState: Boolean) = UltronEspressoInteraction(this).assertChecked(expectedState)

fun <T> UltronEspressoInteraction<T>.getViewSimple(): View = execute { _, view ->
    view
}
fun Matcher<View>.getViewSimple() = UltronEspressoInteraction(onView(this)).getViewSimple()