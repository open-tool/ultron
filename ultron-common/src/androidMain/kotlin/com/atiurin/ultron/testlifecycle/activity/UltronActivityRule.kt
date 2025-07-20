package com.atiurin.ultron.testlifecycle.activity

import android.app.Activity
import androidx.annotation.RestrictTo
import com.atiurin.ultron.core.config.UltronAndroidCommonConfig
import org.junit.rules.ExternalResource
import com.atiurin.ultron.testlifecycle.setupteardown.Condition


/**
 * reduced copy of the [androidx.test.ext.junit.rules.ActivityScenarioRule]
 * @param activityClass an activity class to launch
 * Differences:
 * 1. has only one constructor (launch by activityClass)
 * 2. has allure step for setup and teardown
 * 3. finish activity does not await idle state
 * 4. finish all activities in RESUMED, PAUSED and STOPPED stage
 */
open class UltronActivityRule<A : Activity?>(
    activityClass: Class<A>
) : ExternalResource() {
    private var scenarioSupplier: Supplier<UltronActivityScenario<A>>
    private var scenario: UltronActivityScenario<A>? = null

    init {
        scenarioSupplier = Supplier {
            UltronActivityScenario.launch(activityClass)
        }
    }

    /**
     * Same as [java.util.function.Supplier] which requires API level 24.
     *
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    internal fun interface Supplier<T> {
        fun get(): T
    }

    fun launchActivity() {
        scenario = scenarioSupplier.get()
    }

    @Throws(Throwable::class)
    override fun before() {
        val condition = Condition(
            counter = -1,
            key = "LAUNCH ACTIVITY",
            name = "SetUp 'launch activity'",
            actions = { launchActivity() }
        )
        UltronAndroidCommonConfig.Conditions.conditionExecutorWrapper.execute(condition)
    }

    override fun after() {
        val condition = Condition(
            counter = -1,
            key = "FINISH ACTIVITY",
            name = "TearDown 'finish activity'",
            actions = { scenario!!.close() }
        )
        UltronAndroidCommonConfig.Conditions.conditionExecutorWrapper.execute(condition)
    }

    /**
     * Returns [UltronActivityScenario] of the given activity class.
     *
     * @throws NullPointerException if you call this method while test is not running
     * @return a non-null [UltronActivityScenario] instance
     */
    fun getScenario(): UltronActivityScenario<A> {
        return scenario ?: throw IllegalStateException("scenario not initialized")
    }
}