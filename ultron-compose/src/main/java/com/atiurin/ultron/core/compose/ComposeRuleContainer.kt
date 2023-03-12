package com.atiurin.ultron.core.compose

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.TestContext
import androidx.compose.ui.test.junit4.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.extensions.getProperty

object ComposeRuleContainer {
    var rule: ComposeTestRule? = null
    var testContext: TestContext? = null

    fun init(composeRule: ComposeTestRule) {
        rule = composeRule
        testContext = composeRule.getTestContext()
    }

    fun getComposeRule(): ComposeTestRule {
        return rule ?: throw UltronException("Initialise ComposeTestRule using createUltronComposeRule<A>() factory method.")
    }

    fun getComposeTestContext(): TestContext {
        return testContext ?: throw UltronException("Initialise ComposeTestRule using createUltronComposeRule<A>() factory method.")
    }
}

/**
 * Factory method to provide android specific implementation of [createComposeRule], for a given
 * activity class type [A].
 *
 * This method is useful for tests that require a custom Activity. This is usually the case for
 * tests where the compose content is set by that Activity, instead of via the test rule's
 * [setContent][ComposeContentTestRule.setContent]. Make sure that you add the provided activity
 * into your app's manifest file (usually in main/AndroidManifest.xml).
 *
 * This creates a test rule that is using [ActivityScenarioRule] as the activity launcher. If you
 * would like to use a different one you can create [AndroidComposeTestRule] directly and supply
 * it with your own launcher.
 *
 * If your test doesn't require a specific Activity, use [createDefaultUltronComposeRule] instead.
 */
inline fun <reified A : ComponentActivity> createUltronComposeRule(): AndroidComposeTestRule<ActivityScenarioRule<A>, A> {
    val rule = createAndroidComposeRule<A>()
    ComposeRuleContainer.init(rule)
    return rule
}

/**
 * Factory method to provide implementation of [ComposeContentTestRule]
 *
 * This method is useful for tests that doesn't require a custom Activity.
 *
 * It's expected that compose content is set via the test rule's
 * [setContent][ComposeContentTestRule.setContent]
 */
fun createDefaultUltronComposeRule(): ComposeContentTestRule {
    val rule = createComposeRule()
    ComposeRuleContainer.init(rule)
    return rule
}

/**
 * Factory method to provide an implementation of [ComposeTestRule] that doesn't create a compose
 * host for you in which you can set content.
 *
 * This method is useful for tests that need to create their own compose host during the test.
 * The returned test rule will not create a host, and consequently does not provide a
 * `setContent` method. To set content in tests using this rule, use the appropriate `setContent`
 * methods from your compose host.
 *
 * A typical use case on Android is when the test needs to launch an Activity (the compose host)
 * after one or more dependencies have been injected.
 */
fun createEmptyUltronComposeRule(): ComposeTestRule {
    val rule = createEmptyComposeRule()
    ComposeRuleContainer.init(rule)
    return rule
}

fun ComposeTestRule.getTestContext() = this.getProperty<TestContext>("testContext")


