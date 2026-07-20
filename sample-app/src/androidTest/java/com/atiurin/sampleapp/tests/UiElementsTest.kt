package com.atiurin.sampleapp.tests

import com.atiurin.sampleapp.activity.UiElementsActivity
import com.atiurin.ultron.testlifecycle.activity.UltronActivityRule

abstract class UiElementsTest : BaseTest() {
    val activityRule = UltronActivityRule(UiElementsActivity::class.java)

    init {
        ruleSequence.add(activityRule)
    }
}