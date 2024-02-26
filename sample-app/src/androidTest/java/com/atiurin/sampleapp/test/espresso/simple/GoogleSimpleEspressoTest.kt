package com.atiurin.sampleapp.test.espresso.simple

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.atiurin.sampleapp.activity.UiElementsActivity
import org.junit.Rule
import org.junit.Test
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.atiurin.sampleapp.R

class GoogleSimpleEspressoTest {
    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(UiElementsActivity::class.java)

    @Test
    fun clickSimpleButtonTest(){
        onView(withId(R.id.button1))
            .check(matches(isDisplayed()))
            .check(matches(withText("Simple Button")))
            .perform(click())
        onView(withId(R.id.last_event_status))
            .check(matches(isDisplayed()))
            .check(matches(withText("CLICK: Click 1")))
    }
}