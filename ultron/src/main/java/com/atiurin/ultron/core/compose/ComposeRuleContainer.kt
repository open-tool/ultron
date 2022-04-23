package com.atiurin.ultron.core.compose

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.TestContext
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.extensions.getProperty

object ComposeRuleContainer {
    var rule : ComposeContentTestRule? = null
    var testContext: TestContext? = null

    fun getComposeRule(): ComposeContentTestRule {
        return rule ?: throw UltronException("Initialise ComposeTestRule using createUltronComposeRule<A>() factory method.")
    }

    fun getComposeTestContext(): TestContext {
        return testContext ?: throw UltronException("Initialise ComposeTestRule using createUltronComposeRule<A>() factory method.")
    }
}

inline fun <reified A: ComponentActivity> createUltronComposeRule(): AndroidComposeTestRule<ActivityScenarioRule<A>, A> {
    val rule = createAndroidComposeRule<A>()
    ComposeRuleContainer.rule = rule
    ComposeRuleContainer.testContext = rule.getProperty<TestContext>("testContext")
    return rule
}

