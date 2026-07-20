package com.atiurin.ultron.testlifecycle.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import androidx.annotation.GuardedBy
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.State
import androidx.test.internal.platform.ServiceLoaderWrapper
import androidx.test.internal.platform.app.ActivityInvoker
import androidx.test.internal.platform.os.ControlledLooper
import androidx.test.internal.util.Checks
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleCallback
import androidx.test.runner.lifecycle.ActivityLifecycleMonitor
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import androidx.tracing.Trace
import java.io.Closeable
import java.util.Arrays
import java.util.EnumMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock


/**
 * ActivityScenario provides APIs to start and drive an Activity's lifecycle state for testing. It
 * works with arbitrary activities and works consistently across different versions of the Android
 * framework.
 *
 *
 * The ActivityScenario API uses [State] extensively. If you are unfamiliar with [ ] components, please read [lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle#lc)
 * before starting.
 *
 *
 * It is crucial to understand the difference between [State] and [Event].
 *
 *
 * [SimpleActivityScenario.moveToState] allows you to transition your Activity's state to
 * [State.CREATED], [State.STARTED], [State.RESUMED], or [State.DESTROYED].
 * There are two paths for an Activity to reach [State.CREATED]: after [Event.ON_CREATE]
 * happens but before [Event.ON_START], or after [Event.ON_STOP]. ActivityScenario
 * always moves the Activity's state using the second path. The same applies to [ ][State.STARTED].
 *
 *
 * [State.DESTROYED] is the terminal state. You cannot move your Activity to other state
 * once it reaches to that state. If you want to test recreation of Activity instance, use [ ][.recreate].
 *
 *
 * ActivityScenario does't clean up device state automatically and may leave the activity keep
 * running after the test finishes. Call [.close] in your test to clean up the state or use
 * try-with-resources statement. This is optional but highly recommended to improve the stability of
 * your tests. Also, consider using [androidx.test.ext.junit.rules.ActivityScenarioRule].
 *
 *
 * This class is a replacement of ActivityController in Robolectric and ActivityTestRule in ATSL.
 *
 *
 * Following are the example of common use cases.
 *
 * <pre>`Before:
 * MyActivity activity = Robolectric.setupActivity(MyActivity.class);
 * assertThat(activity.getSomething()).isEqualTo("something");
 *
 * After:
 * try(ActivityScenario<MyActivity> scenario = ActivityScenario.launch(MyActivity.class)) {
 * scenario.onActivity(activity -> {
 * assertThat(activity.getSomething()).isEqualTo("something");
 * });
 * }
 *
 * Before:
 * ActivityController<MyActivity> controller = Robolectric.buildActivity(MyActivity.class);
 * controller.create().start().resume();  // Moves the activity state to State.RESUMED.
 * controller.pause();    // Moves the activity state to State.STARTED. (ON_PAUSE is an event).
 * controller.stop();     // Moves the activity state to State.CREATED. (ON_STOP is an event).
 * controller.destroy();  // Moves the activity state to State.DESTROYED.
 *
 * After:
 * try(ActivityScenario<MyActivity> scenario = ActivityScenario.launch(MyActivity.class)) {
 * scenario.moveToState(State.RESUMED);    // Moves the activity state to State.RESUMED.
 * scenario.moveToState(State.STARTED);    // Moves the activity state to State.STARTED.
 * scenario.moveToState(State.CREATED);    // Moves the activity state to State.CREATED.
 * scenario.moveToState(State.DESTROYED);  // Moves the activity state to State.DESTROYED.
 * }
`</pre> *
 */
// suppress AutoCloseable usage error
class UltronActivityScenario<A : Activity?> : AutoCloseable, Closeable {
    /** A lock that is used to block the main thread until the Activity becomes a requested state.  */
    private val lock = ReentrantLock()

    /** A condition object to be notified when the activity state changes.  */
    private val stateChangedCondition: Condition = lock.newCondition()

    /** An intent to start a testing Activity.  */
    private lateinit var startActivityIntent: Intent

    /** An ActivityInvoker to use. Implementation class can be configured by service provider.  */
    @SuppressLint("RestrictedApi")
    private val activityInvoker: ActivityInvoker = ServiceLoaderWrapper.loadSingleService(
        ActivityInvoker::class.java
    ) { UltronInstrumentationActivityInvoker() }

    @SuppressLint("RestrictedApi")
    private val controlledLooper: ControlledLooper = ServiceLoaderWrapper.loadSingleService(
        ControlledLooper::class.java
    ) { ControlledLooper.NO_OP_CONTROLLED_LOOPER }

    /**
     * A current activity stage. This variable is updated by [ActivityLifecycleMonitor] from the
     * main thread.
     */
    @GuardedBy("lock")
    private var currentActivityStage = Stage.PRE_ON_CREATE

    /**
     * A current activity. This variable is updated by [ActivityLifecycleMonitor] from the main
     * thread.
     */
    @GuardedBy("lock")
    private var currentActivity: A? = null

    /** Private constructor. Use [.launch] to instantiate this class.  */
    @SuppressLint("RestrictedApi")
    private constructor(activityClass: Class<A>) {
        this.startActivityIntent =
            Checks.checkNotNull(
                activityInvoker.getIntentForActivity(
                    Checks.checkNotNull(
                        activityClass
                    )
                )
            )
    }

    /**
     * An internal helper method to perform initial launch operation for the given scenario instance
     * along with preconditions checks around device's configuration.
     *
     * @param activityOptions activity options bundle to be passed when launching this activity
     * @param launchActivityForResult whether or not activity result code and data is needed
     */
    @SuppressLint("RestrictedApi")
    private fun launchInternal(activityOptions: Bundle?, launchActivityForResult: Boolean) {
        Checks.checkState(
            Settings.System.getInt(
                InstrumentationRegistry.getInstrumentation().targetContext.contentResolver,
                Settings.Global.ALWAYS_FINISH_ACTIVITIES,
                0
            )
                    == 0,
            "\"Don't keep activities\" developer options must be disabled for ActivityScenario"
        )

        Checks.checkNotMainThread()

        Trace.beginSection("ActivityScenario launch")
        try {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()

            ActivityLifecycleMonitorRegistry.getInstance()
                .addLifecycleCallback(activityLifecycleObserver)

            // prefer the single argument variant for startActivity for backwards compatibility with older
            // Robolectric versions
            if (activityOptions == null) {
                if (launchActivityForResult) {
                    activityInvoker.startActivityForResult(startActivityIntent)
                } else {
                    activityInvoker.startActivity(startActivityIntent)
                }
            } else {
                if (launchActivityForResult) {
                    activityInvoker.startActivityForResult(startActivityIntent, activityOptions)
                } else {
                    activityInvoker.startActivity(startActivityIntent, activityOptions)
                }
            }

            // Accept any steady states. An activity may start another activity in its onCreate method.
            // Such
            // an activity goes back to created or started state immediately after it is resumed.
            waitForActivityToBecomeAnyOf(*STEADY_STATES.values.toTypedArray<State>())
        } finally {
            Trace.endSection()
        }
    }

    /**
     * Finishes the managed activity and cleans up device's state. This method blocks execution until
     * the activity becomes [State.DESTROYED].
     *
     *
     * It is highly recommended to call this method after you test is done to keep the device state
     * clean although this is optional.
     *
     *
     * You may call this method more than once. If the activity has been finished already, this
     * method does nothing.
     *
     *
     * Avoid calling this method directly. Consider one of the following options instead:
     *
     * <pre>`Option 1, use try-with-resources:
     *
     * try (ActivityScenario<MyActivity> scenario = ActivityScenario.launch(MyActivity.class)) {
     * // Your test code goes here.
     * }
     *
     * Option 2, use ActivityScenarioRule:
     *
    ` * @Rule `public ActivityScenarioRule<MyActivity> rule = new ActivityScenarioRule<>(MyActivity.class);
     *
    ` * @Test`public void myTest() {
     * ActivityScenario<MyActivity> scenario = rule.getScenario();
     * // Your test code goes here.
     * }
    `</pre> *
     */
    override fun close() {
        Trace.beginSection("ActivityScenario close")
        try {
            moveToState(State.DESTROYED)
            ActivityLifecycleMonitorRegistry.getInstance()
                .removeLifecycleCallback(activityLifecycleObserver)
        } finally {
            Trace.endSection()
        }
    }

    /**
     * Blocks the current thread until activity transition completes and its state becomes one of a
     * given state.
     */
    private fun waitForActivityToBecomeAnyOf(vararg expectedStates: State) {
        // Wait for idle sync otherwise we might hit transient state.
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        val expectedStateSet: Set<State?> = HashSet(Arrays.asList(*expectedStates))
        lock.lock()
        try {
            if (expectedStateSet.contains(STEADY_STATES[currentActivityStage])) {
                return
            }

            var now = SystemClock.elapsedRealtime()
            val deadline = now + TIMEOUT_MILLISECONDS
            while (now < deadline
                && !expectedStateSet.contains(STEADY_STATES[currentActivityStage])
            ) {
                stateChangedCondition.await(deadline - now, TimeUnit.MILLISECONDS)
                now = SystemClock.elapsedRealtime()
            }

            if (!expectedStateSet.contains(STEADY_STATES[currentActivityStage])) {
                throw AssertionError(
                    String.format(
                        "Activity never becomes requested state \"%s\" (last lifecycle transition ="
                                + " \"%s\")",
                        expectedStateSet, currentActivityStage
                    )
                )
            }
        } catch (e: InterruptedException) {
            throw AssertionError(
                String.format(
                    "Activity never becomes requested state \"%s\" (last lifecycle transition = \"%s\")",
                    expectedStateSet, currentActivityStage
                ),
                e
            )
        } finally {
            lock.unlock()
        }
    }

    /** Observes an Activity lifecycle change events and updates ActivityScenario's internal state.  */
    private val activityLifecycleObserver =
        ActivityLifecycleCallback { activity, stage ->
            if (!activityMatchesIntent(startActivityIntent, activity)) {
                Log.v(
                    TAG,
                    String.format(
                        ("Activity lifecycle changed event received but ignored because the intent does"
                                + " not match. startActivityIntent=%s, activity.getIntent()=%s,"
                                + " activity=%s"),
                        startActivityIntent, activity.intent, activity
                    )
                )
                return@ActivityLifecycleCallback
            }
            lock.lock()
            try {
                when (currentActivityStage) {
                    Stage.PRE_ON_CREATE, Stage.DESTROYED ->
                        // The initial state (or after destroyed when the activity is being recreated)
                        // transition must be to CREATED. Ignore events with non-created stage, which are
                        // likely come from activities that the previous test starts and doesn't clean up.
                        if (stage != Stage.CREATED) {
                            Log.v(
                                TAG,
                                String.format(
                                    ("Activity lifecycle changed event received but ignored because the"
                                            + " reported transition was not ON_CREATE while the last known"
                                            + " transition was %s"),
                                    currentActivityStage
                                )
                            )
                            return@ActivityLifecycleCallback
                        }

                    else ->
                        // Make sure the received event is about the activity which this ActivityScenario
                        // is monitoring. The Android framework may start multiple instances of a same
                        // activity class and intent at a time. Also, there can be a race condition between
                        // an activity that is used by the previous test and being destroyed and an activity
                        // that is being resumed.
                        if (currentActivity !== activity) {
                            Log.v(
                                TAG,
                                String.format(
                                    ("Activity lifecycle changed event received but ignored because the"
                                            + " activity instance does not match. currentActivity=%s,"
                                            + " receivedActivity=%s"),
                                    currentActivity, activity
                                )
                            )
                            return@ActivityLifecycleCallback
                        }
                }

                // Update the internal state to be synced with the Android system. Don't hold activity
                // reference if the new state is destroyed. It's not good idea to access to destroyed
                // activity since the system may reuse the instance or want to garbage collect.
                currentActivityStage = stage
                currentActivity = (if (stage != Stage.DESTROYED) activity else null) as A?

                Log.v(
                    TAG,
                    String.format(
                        "Update currentActivityStage to %s, currentActivity=%s",
                        currentActivityStage, currentActivity
                    )
                )

                stateChangedCondition.signal()
            } finally {
                lock.unlock()
            }
        }

    /**
     * ActivityState is a state class that holds a snapshot of an Activity's current state and a
     * reference to the Activity.
     */
    private class ActivityState<A : Activity?>(
        val activity: A?,
        val state: State?,
        val stage: Stage
    )

    private val currentActivityState: ActivityState<A?>
        get() {
            lock.lock()
            try {
                val finishTime = SystemClock.elapsedRealtime() + 10000L
                do {
                    try {
                        if (STEADY_STATES[currentActivityStage] != null) break
                    } catch (_: Exception) {
                    }
                } while (SystemClock.elapsedRealtime() < finishTime)
                return ActivityState(
                    currentActivity,
                    STEADY_STATES[currentActivityStage], currentActivityStage
                )
            } finally {
                lock.unlock()
            }
        }

    /**
     * Moves Activity state to a new state.
     *
     *
     * If a new state and current state are the same, it does nothing. It accepts [ ][State.CREATED], [State.STARTED], [State.RESUMED], and [State.DESTROYED].
     *
     *
     * [State.DESTROYED] is the terminal state. You cannot move the state to other state
     * after the activity reaches that state.
     *
     *
     * The activity must be at the top of the back stack (excluding internal facilitator activities
     * started by this library), otherwise [AssertionError] may be thrown. If the activity
     * starts another activity (such as DialogActivity), make sure you close these activities and
     * bring back the original activity foreground before you call this method.
     *
     *
     * This method cannot be called from the main thread except in Robolectric tests.
     *
     * @throws IllegalArgumentException if unsupported `newState` is given
     * @throws IllegalStateException if Activity is destroyed, finished or finishing
     * @throws AssertionError if Activity never becomes requested state
     */
    @SuppressLint("RestrictedApi")
    fun moveToState(newState: State): UltronActivityScenario<A> {
        Checks.checkNotMainThread()
        if (newState != State.DESTROYED) {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        }

        val currentState = currentActivityState

        if (currentState.state == newState) {
            return this
        }
        Checks.checkState(
            currentState.state != State.DESTROYED && currentState.activity != null,
            String.format(
                "Cannot move to state \"%s\" since the Activity has been destroyed already",
                newState
            )
        )

        when (newState) {
            State.CREATED -> activityInvoker.stopActivity(currentState.activity)
            State.STARTED -> {
                // ActivityInvoker#pauseActivity only accepts resumed or paused activity. Move the state to
                // resumed first.
                moveToState(State.RESUMED)
                activityInvoker.pauseActivity(currentState.activity)
            }

            State.RESUMED -> activityInvoker.resumeActivity(currentState.activity)
            State.DESTROYED -> {
                repeat(3) {
                    InstrumentationRegistry.getInstrumentation().runOnMainSync {
                        listOf(Stage.RESUMED, Stage.PAUSED, Stage.STOPPED).forEach {
                            ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(it).forEach { activity ->
                                if (activity != currentState.activity) {
                                    activity.finish()
                                }
                            }
                        }
                    }
                }
                activityInvoker.finishActivity(currentState.activity)
            }
            else -> throw IllegalArgumentException(
                String.format("A requested state \"%s\" is not supported", newState)
            )
        }

        waitForActivityToBecomeAnyOf(newState)
        return this
    }

    /**
     * ActivityAction interface should be implemented by any class whose instances are intended to be
     * executed by the main thread. An Activity that is instrumented by the ActivityScenario is passed
     * to [ActivityAction.perform] method.
     *
     * <pre>`Example:
     * ActivityScenario<MyActivity> scenario = ActivityScenario.launch(MyActivity.class);
     * scenario.onActivity(activity -> {
     * assertThat(activity.getSomething()).isEqualTo("something");
     * });
    `</pre> *
     *
     *
     * You should never keep the Activity reference. It should only be accessed in [ ][ActivityAction.perform] scope for two reasons: 1) Android framework may re-create the Activity
     * during lifecycle changes, your holding reference might be stale. 2) It increases the reference
     * counter and it may affect to the framework behavior, especially after you finish the Activity.
     *
     * <pre>`Bad Example:
     * ActivityScenario<MyActivity> scenario = ActivityScenario.launch(MyActivity.class);
     * final MyActivity[] myActivityHolder = new MyActivity[1];
     * scenario.onActivity(activity -> {
     * myActivityHolder[0] = activity;
     * });
     * assertThat(myActivityHolder[0].getSomething()).isEqualTo("something");
    `</pre> *
     */
    fun interface ActivityAction<A : Activity?> {
        /**
         * This method is invoked on the main thread with the reference to the Activity.
         *
         * @param activity an Activity instrumented by the [SimpleActivityScenario]. It never be null.
         */
        fun perform(activity: A)
    }

    /**
     * Runs a given `action` on the current Activity's main thread.
     *
     *
     * Note that you should never keep Activity reference passed into your `action` because
     * it can be recreated at anytime during state transitions.
     *
     * @throws IllegalStateException if Activity is destroyed, finished or finishing
     */
    @SuppressLint("RestrictedApi")
    fun onActivity(action: ActivityAction<A?>): UltronActivityScenario<A> {
        // A runnable to perform given ActivityAction. This runnable should be invoked from the
        // application main thread.
        val runnableAction =
            Runnable {
                Checks.checkMainThread()
                lock.lock()
                try {
                    Checks.checkNotNull(
                        currentActivity,
                        "Cannot run onActivity since Activity has been destroyed already"
                    )
                    action.perform(currentActivity)
                } finally {
                    lock.unlock()
                }
            }

        if (Looper.myLooper() == Looper.getMainLooper()) {
            // execute any queued work on main looper, to make behavior consistent between running
            // on Robolectric with paused main looper and instrumentation
            controlledLooper.drainMainThreadUntilIdle()
            runnableAction.run()
        } else {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            InstrumentationRegistry.getInstrumentation().runOnMainSync(runnableAction)
        }

        return this
    }

    val result: Instrumentation.ActivityResult
        /**
         * Waits for the activity to be finished and returns the activity result.
         *
         *
         * ActivityScenario.launchActivityForResult() must be used to launch an Activity before this
         * method is called.
         *
         *
         * Note: This method doesn't call [Activity.finish]. The activity must be finishing or
         * finished otherwise this method will throws runtime exception after the timeout.
         *
         * <pre>`Example:
         * ActivityScenario<MyActivity> scenario =
         * ActivityScenario.launchActivityForResult(MyActivity.class);
         * // Let's say MyActivity has a button that finishes itself.
         * onView(withId(R.id.finish_button)).perform(click());
         * assertThat(scenario.getResult().getResultCode()).isEqualTo(Activity.RESULT_OK);
        `</pre> *
         *
         * @return activity result of the activity that managed by this scenario class.
         * @throws IllegalStateException when you call this method with an Activity that was not started
         * by [.launchActivityForResult]
         */
        @SuppressLint("RestrictedApi")
        get() = activityInvoker.activityResult

    val state: State
        /**
         * Returns the current activity state. The possible states are [State.CREATED], [ ][State.STARTED], [State.RESUMED], and [State.DESTROYED].
         *
         *
         * This method cannot be called from the main thread except in Robolectric tests.
         */
        @SuppressLint("RestrictedApi")
        get() {
            val currentActivityState =
                currentActivityState
            return Checks.checkNotNull(
                currentActivityState.state,
                "Could not get current state of activity %s due to the transition is incomplete. Current"
                        + " stage = %s",
                currentActivityState.activity,
                currentActivityState.stage
            )
        }

    companion object {
        private val TAG: String = UltronActivityScenario::class.java.simpleName

        /**
         * The timeout for [.waitForActivityToBecomeAnyOf] method. If an Activity doesn't become
         * requested state after the timeout, we will throw [AssertionError] to fail tests.
         */
        private const val TIMEOUT_MILLISECONDS: Long = 45000

        /**
         * A map to lookup steady [State] by [Stage]. Transient stages such as [ ][Stage.CREATED], [Stage.STARTED] and [Stage.RESTARTED] are not included in the map.
         */
        private val STEADY_STATES: MutableMap<Stage, State> = EnumMap(
            Stage::class.java
        )

        init {
            STEADY_STATES[Stage.RESUMED] = State.RESUMED
            STEADY_STATES[Stage.PAUSED] = State.STARTED
            STEADY_STATES[Stage.STOPPED] = State.CREATED
            STEADY_STATES[Stage.DESTROYED] = State.DESTROYED
        }

        /**
         * Launches an activity of a given class and constructs ActivityScenario with the activity. Waits
         * for the lifecycle state transitions to be complete. Typically the initial state of the activity
         * is [State.RESUMED] but can be in another state. For instance, if your activity calls
         * [Activity.finish] from your [Activity.onCreate], the state is [ ][State.DESTROYED] when this method returns.
         *
         *
         * If you need to supply parameters to the start activity intent, use [.launch].
         *
         *
         * If you need to get the activity result, use [.launchActivityForResult].
         *
         *
         * This method cannot be called from the main thread except in Robolectric tests.
         *
         * @param activityClass an activity class to launch
         * @throws AssertionError if the lifecycle state transition never completes within the timeout
         * @return ActivityScenario which you can use to make further state transitions
         */
        @SuppressLint("RestrictedApi")
        fun <A : Activity?> launch(activityClass: Class<A>): UltronActivityScenario<A> {
            val scenario = UltronActivityScenario(Checks.checkNotNull(activityClass))
            scenario.launchInternal( /*activityOptions=*/null,  /*launchActivityForResult=*/false)
            return scenario
        }

        /** Determine if the intent matches the given activity.  */
        private fun activityMatchesIntent(
            startActivityIntent: Intent,
            launchedActivity: Activity
        ): Boolean {
            // The logic here is almost the same as Intent.filterEquals
            // but we need to handle case where startActivityIntent does not have component specified
            // (aka is implicit intent). The launchedActivity intent will always have component specified,
            // since
            // the framework populates it

            val activityIntent = launchedActivity.intent
            if (!equals(startActivityIntent.action, activityIntent.action) ||
                !equals(startActivityIntent.data, activityIntent.data) ||
                !equals(startActivityIntent.type, activityIntent.type)
            ) {
                return false
            }

            val isActivityInSamePackage =
                hasPackageEquivalentComponent(startActivityIntent)
                        && hasPackageEquivalentComponent(activityIntent)

            return if (!isActivityInSamePackage
                && !equals(startActivityIntent.getPackage(), activityIntent.getPackage())
            ) {
                false
            } else if (startActivityIntent.component != null
                && !equals(startActivityIntent.component, activityIntent.component)
            ) {
                false
            } else if (!equals(startActivityIntent.categories, activityIntent.categories)) {
                false
            } else if (Build.VERSION.SDK_INT >= 29
                && !equals(startActivityIntent.identifier, activityIntent.identifier)
            ) {
                false
            } else {
                true
            }
        }

        /**
         * Return `true` if the component name is not null and is in the same package that this
         * intent limited to. otherwise return `false`. Note: this code is copied from `Intent#hasPackageEquivalentComponent`.
         */
        private fun hasPackageEquivalentComponent(intent: Intent): Boolean {
            val componentName = intent.component
            val packageName = intent.getPackage()
            // packageName may be null when the resolved Activity is in the same package to this
            // running process.
            return componentName != null
                    && (packageName == null || packageName == componentName.packageName)
        }

        // reimplementation of Objects.equals since it is only available on APIs >= 19
        private fun equals(a: Any?, b: Any?): Boolean {
            return (a === b) || (a != null && a == b)
        }
    }
}