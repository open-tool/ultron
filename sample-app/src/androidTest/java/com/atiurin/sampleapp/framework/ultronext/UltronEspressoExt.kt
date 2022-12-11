package com.atiurin.sampleapp.framework.ultronext

import android.view.View
import android.widget.TextView
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.UltronEspresso
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import com.atiurin.ultron.core.espresso.action.EspressoActionType
import com.atiurin.ultron.core.espresso.recyclerview.UltronRecyclerView
import com.atiurin.ultron.core.espresso.recyclerview.UltronRecyclerViewItem
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

//add new action for Ultron framework
fun <T> UltronEspressoInteraction<T>.appendText(text: String) = apply {
    executeAction(
        operationBlock = getInteractionActionBlock(AppendTextAction(text)),
        name = "Append text '$text' to ${getInteractionMatcher()}",
        description = "${interaction!!::class.simpleName} APPEND_TEXT to ${getInteractionMatcher()} during $timeoutMs ms",
    )
}

//support action for all Matcher<View>
fun Matcher<View>.appendText(text: String) = UltronEspressoInteraction(onView(this)).appendText(text)

//support action for all ViewInteractions
fun ViewInteraction.appendText(text: String) = UltronEspressoInteraction(this).appendText(text)

//support action for all DataInteractions
fun DataInteraction.appendText(text: String) = UltronEspressoInteraction(this).appendText(text)

//support action for RecyclerView list. appendText action is useless for RecyclerView.
// This is just an example of how to add new behaviour for UltronRecyclerView
fun UltronRecyclerView.appendText(text: String) = apply { recyclerViewMatcher.appendText(text) }

//support action for RecyclerView item
fun UltronRecyclerViewItem.appendText(text: String) = apply { getMatcher().appendText(text) }

class AppendTextAction(private val value: String) : ViewAction {
    override fun getConstraints() = allOf(isDisplayed(), isAssignableFrom(TextView::class.java))!!

    override fun perform(uiController: UiController, view: View) {
        (view as TextView).apply {
            this.text = "$text$value"
        }
        uiController.loopMainThreadUntilIdle()
    }

    override fun getDescription(): String {
        return "append text $value"
    }
}