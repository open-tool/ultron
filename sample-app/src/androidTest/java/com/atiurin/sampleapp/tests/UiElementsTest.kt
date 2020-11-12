package com.atiurin.sampleapp.tests

import androidx.test.rule.ActivityTestRule
import com.atiurin.sampleapp.activity.UiElementsActivity
import com.atiurin.sampleapp.pages.UiElementsPage

abstract class UiElementsTest : BaseTest() {
    val activityRule = ActivityTestRule(UiElementsActivity::class.java)

    init {
        ruleSequence.add(activityRule)
    }

    val page = UiElementsPage
}