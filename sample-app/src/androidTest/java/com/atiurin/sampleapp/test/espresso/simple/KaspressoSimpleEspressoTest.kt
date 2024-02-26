package com.atiurin.sampleapp.test.espresso.simple

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.atiurin.sampleapp.activity.UiElementsActivity
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.text.KButton
import org.junit.Rule
import org.junit.Test
import com.atiurin.sampleapp.R
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.kakao.text.KTextView

class KaspressoSimpleEspressoTest : TestCase() {
    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(UiElementsActivity::class.java)

    @Test
    fun clickSimpleButtonTest() = run {
        KaspressoSimpleEspressoScreen {
            button {
                isDisplayed()
                hasText("Simple Button")
                click()
            }
            lastEvent {
                isDisplayed()
                hasText("CLICK: Click 1")
            }
        }
    }
}

object KaspressoSimpleEspressoScreen : Screen<KaspressoSimpleEspressoScreen>() {
    val button = KButton {
        withId(R.id.button1)
    }

    val lastEvent = KTextView {
        withId(R.id.last_event_status)
    }
}