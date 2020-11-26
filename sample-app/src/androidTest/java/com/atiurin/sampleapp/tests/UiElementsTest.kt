package com.atiurin.sampleapp.tests

import androidx.test.rule.ActivityTestRule
import com.atiurin.sampleapp.activity.UiElementsActivity
import com.atiurin.sampleapp.pages.UiElementsPage
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.uiautomator.UiAutomatorConfig

abstract class UiElementsTest : BaseTest() {
    val activityRule = ActivityTestRule(UiElementsActivity::class.java)

    init {
        ruleSequence.add(activityRule)
        UiAutomatorConfig.setIdlingTimeout(0L)
    }


}