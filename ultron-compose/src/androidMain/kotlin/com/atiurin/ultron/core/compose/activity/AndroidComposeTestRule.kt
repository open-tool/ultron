package com.atiurin.ultron.core.compose.activity

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.AndroidComposeUiTestEnvironment
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.IdlingResource
import androidx.compose.ui.test.MainTestClock
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import androidx.compose.ui.test.waitUntilDoesNotExist
import androidx.compose.ui.test.waitUntilExactlyOneExists
import androidx.compose.ui.test.waitUntilNodeCount
import androidx.compose.ui.unit.Density
import com.atiurin.ultron.testlifecycle.activity.UltronActivityRule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ExperimentalTestApi::class)
class AndroidComposeTestRule<R : TestRule, A : ComponentActivity> private constructor(
    val activityRule: R,
    private val environment: AndroidComposeUiTestEnvironment<A>
) : ComposeContentTestRule {
    private val composeTest = environment.test

    /**
     * Android specific implementation of [ComposeContentTestRule], where compose content is hosted
     * by an Activity.
     *
     * The Activity is normally launched by the given [activityRule] before the test starts, but it
     * is possible to pass a test rule that chooses to launch an Activity on a later time. The
     * Activity is retrieved from the [activityRule] by means of the [activityProvider], which can be
     * thought of as a getter for the Activity on the [activityRule]. If you use an [activityRule]
     * that launches an Activity on a later time, you should make sure that the Activity is launched
     * by the time or while the [activityProvider] is called.
     *
     * The [AndroidComposeTestRule] wraps around the given [activityRule] to make sure the Activity
     * is launched _after_ the [AndroidComposeTestRule] has completed all necessary steps to control
     * and monitor the compose content.
     *
     * @param activityRule Test rule to use to launch the Activity.
     * @param activityProvider Function to retrieve the Activity from the given [activityRule].
     */
    constructor(
        activityRule: R,
        activityProvider: (R) -> A
    ) : this(
        activityRule = activityRule,
        effectContext = EmptyCoroutineContext,
        activityProvider = activityProvider,
    )

    /**
     * Android specific implementation of [ComposeContentTestRule], where compose content is hosted
     * by an Activity.
     *
     * The Activity is normally launched by the given [activityRule] before the test starts, but it
     * is possible to pass a test rule that chooses to launch an Activity on a later time. The
     * Activity is retrieved from the [activityRule] by means of the [activityProvider], which can be
     * thought of as a getter for the Activity on the [activityRule]. If you use an [activityRule]
     * that launches an Activity on a later time, you should make sure that the Activity is launched
     * by the time or while the [activityProvider] is called.
     *
     * The [AndroidComposeTestRule] wraps around the given [activityRule] to make sure the Activity
     * is launched _after_ the [AndroidComposeTestRule] has completed all necessary steps to control
     * and monitor the compose content.
     *
     * @param activityRule Test rule to use to launch the Activity.
     * @param effectContext The [CoroutineContext] used to run the composition. The context for
     * `LaunchedEffect`s and `rememberCoroutineScope` will be derived from this context.
     * @param activityProvider Function to retrieve the Activity from the given [activityRule].
     */
    @ExperimentalTestApi
    constructor(
        activityRule: R,
        effectContext: CoroutineContext = EmptyCoroutineContext,
        activityProvider: (R) -> A,
    ) : this(
        activityRule,
        AndroidComposeUiTestEnvironment(effectContext) { activityProvider(activityRule) },
    )

    /**
     * Provides the current activity.
     *
     * Avoid calling often as it can involve synchronization and can be slow.
     */
    val activity: A get() = checkNotNull(composeTest.activity) { "Host activity not found" }

    override fun apply(base: Statement, description: Description): Statement {
        val testStatement = activityRule.apply(base, description)
        return object : Statement() {
            override fun evaluate() {
                environment.runTest {
                    testStatement.evaluate()
                }
            }
        }
    }

    @Deprecated(
        message = "Do not instantiate this Statement, use AndroidComposeTestRule instead",
        level = DeprecationLevel.ERROR
    )
    inner class AndroidComposeStatement(private val base: Statement) : Statement() {
        override fun evaluate() {
            base.evaluate()
        }
    }

    /*
     * WHEN THE NAME AND SHAPE OF THE NEW COMMON INTERFACES HAS BEEN DECIDED,
     * REPLACE ALL OVERRIDES BELOW WITH DELEGATION: ComposeTest by composeTest
     */

    override val density: Density get() = composeTest.density

    override val mainClock: MainTestClock get() = composeTest.mainClock

    override fun <T> runOnUiThread(action: () -> T): T = composeTest.runOnUiThread(action)

    override fun <T> runOnIdle(action: () -> T): T = composeTest.runOnIdle(action)

    override fun waitForIdle() = composeTest.waitForIdle()

    override suspend fun awaitIdle() = composeTest.awaitIdle()

    override fun waitUntil(timeoutMillis: Long, condition: () -> Boolean) =
        composeTest.waitUntil(conditionDescription = null, timeoutMillis, condition)

    override fun waitUntil(
        conditionDescription: String,
        timeoutMillis: Long,
        condition: () -> Boolean
    ) {
        composeTest.waitUntil(conditionDescription, timeoutMillis, condition)
    }

    @ExperimentalTestApi
    override fun waitUntilNodeCount(matcher: SemanticsMatcher, count: Int, timeoutMillis: Long) =
        composeTest.waitUntilNodeCount(matcher, count, timeoutMillis)

    @ExperimentalTestApi
    override fun waitUntilAtLeastOneExists(matcher: SemanticsMatcher, timeoutMillis: Long) =
        composeTest.waitUntilAtLeastOneExists(matcher, timeoutMillis)

    @ExperimentalTestApi
    override fun waitUntilExactlyOneExists(matcher: SemanticsMatcher, timeoutMillis: Long) =
        composeTest.waitUntilExactlyOneExists(matcher, timeoutMillis)

    @ExperimentalTestApi
    override fun waitUntilDoesNotExist(matcher: SemanticsMatcher, timeoutMillis: Long) =
        composeTest.waitUntilDoesNotExist(matcher, timeoutMillis)

    override fun registerIdlingResource(idlingResource: IdlingResource) =
        composeTest.registerIdlingResource(idlingResource)

    override fun unregisterIdlingResource(idlingResource: IdlingResource) =
        composeTest.unregisterIdlingResource(idlingResource)

    override fun onNode(
        matcher: SemanticsMatcher,
        useUnmergedTree: Boolean
    ): SemanticsNodeInteraction = composeTest.onNode(matcher, useUnmergedTree)

    override fun onAllNodes(
        matcher: SemanticsMatcher,
        useUnmergedTree: Boolean
    ): SemanticsNodeInteractionCollection = composeTest.onAllNodes(matcher, useUnmergedTree)

    override fun setContent(composable: @Composable () -> Unit) = composeTest.setContent(composable)

    fun cancelAndRecreateRecomposer() {
        environment.cancelAndRecreateRecomposer()
    }
}

private fun <A : ComponentActivity> getActivityFromTestRule(rule: UltronActivityRule<A>): A {
    var activity: A? = null
    rule.getScenario().onActivity { activity = it }
    if (activity == null) {
        throw IllegalStateException("Activity was not set in the ActivityScenarioRule!")
    }
    return activity!!
}

fun <A : ComponentActivity> createAndroidComposeRule(
    activityClass: Class<A>
): AndroidComposeTestRule<UltronActivityRule<A>, A> = AndroidComposeTestRule(
    activityRule = UltronActivityRule(activityClass),
    activityProvider = ::getActivityFromTestRule
)