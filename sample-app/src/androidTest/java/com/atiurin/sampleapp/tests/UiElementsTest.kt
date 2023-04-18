package com.atiurin.sampleapp.tests

import androidx.test.rule.ActivityTestRule
import com.atiurin.sampleapp.activity.UiElementsActivity
import com.atiurin.ultron.core.config.UltronConfig
import org.junit.BeforeClass

abstract class UiElementsTest : BaseTest() {
    val activityRule = ActivityTestRule(UiElementsActivity::class.java)

    init {
        ruleSequence.add(activityRule)
    }
}