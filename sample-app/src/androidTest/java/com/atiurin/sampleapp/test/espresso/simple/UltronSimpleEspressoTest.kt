package com.atiurin.sampleapp.test.espresso.simple

import android.widget.Button
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.activity.UiElementsActivity
import com.atiurin.sampleapp.framework.ultronext.appendText
import com.atiurin.sampleapp.test.base.UltronBaseTest
import com.atiurin.ultron.custom.espresso.action.getText
import com.atiurin.ultron.extensions.isDisplayed
import com.atiurin.ultron.extensions.performOnViewForcibly
import com.atiurin.ultron.extensions.withName
import com.atiurin.ultron.page.Screen
import com.atiurin.ultron.utils.getTargetResourceName
import org.junit.Rule
import org.junit.Test

class UltronSimpleEspressoTest : UltronBaseTest() {
    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(UiElementsActivity::class.java)

    @Test
    fun clickSimpleButtonTest() {
        withId(R.id.button1).isDisplayed().hasText("Simple Button").click()
        withId(R.id.last_event_status).isDisplayed().hasText("CLICK: Click 1")
    }

    @Test
    fun clickSimpleScreenButtonTest() {
        UltronSimpleScreen {
            button.isDisplayed().hasText("Simple Button").click()
            event.isDisplayed().hasText("CLICK: Click 1")
        }
    }

    @Test
    fun withAssertionClickTest() {
        UltronSimpleScreen {
            button.withTimeout(10000).withAssertion {
                event.withTimeout(1000).hasText("CLICK: Click 1")
            }.click()
        }
    }

    @Test
    fun performForciblyTest(){
        // to avoid espresso idle state check
        UltronSimpleScreen.button.performOnViewForcibly {
            (this as Button).performClick()
        }
    }
}

object UltronSimpleScreen : Screen<UltronSimpleScreen>(){
    val button = withId(R.id.button1).withName("Simple button").withTimeout(1000)
    val event = withId(R.id.last_event_status).withName("Last event (${getTargetResourceName(R.id.last_event_status)})")
}