package com.atiurin.ultron.testlifecycle.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.app.Instrumentation
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.platform.app.ActivityInvoker
import androidx.test.internal.platform.app.ActivityLifecycleTimeout
import androidx.test.internal.util.Checks
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import java.util.Arrays
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


/**
 * On-device [ActivityInvoker] implementation that drives Activity lifecycles using [ ] indirectly via [Context.startActivity] and [ ][Activity.recreate].
 *
 *
 * Some methods in this class are non-blocking API. It's caller's responsibility to wait for
 * activity state to be desired state.
 */
@SuppressLint("RestrictedApi")
internal class UltronInstrumentationActivityInvoker : ActivityInvoker {
    /**
     * BootstrapActivity starts a test target activity specified by the extras bundle with key [ ][.TARGET_ACTIVITY_INTENT_KEY] in the intent that starts this bootstrap activity. The target
     * activity is started by [Activity.startActivityForResult] when the bootstrap activity is
     * created. Upon an arrival of the activity result, the bootstrap activity forwards the result to
     * the instrumentation process by broadcasting the result and finishes itself. This activity also
     * finishes itself when it receives [.FINISH_BOOTSTRAP_ACTIVITY] action.
     */
    class BootstrapActivity : Activity() {
        private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                finishActivity( /*requestCode=*/0)
                finish()
            }
        }

        private var isTargetActivityStarted = false

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            registerBroadcastReceiver(
                this, receiver, IntentFilter(
                    FINISH_BOOTSTRAP_ACTIVITY
                )
            )

            isTargetActivityStarted =
                (savedInstanceState != null
                        && savedInstanceState.getBoolean(IS_TARGET_ACTIVITY_STARTED_KEY, false))

            // disable starting animations
            overridePendingTransition(0, 0)
        }

        override fun finish() {
            super.finish()
            // disable closing animations
            overridePendingTransition(0, 0)
        }

        override fun onResume() {
            super.onResume()

            if (!isTargetActivityStarted) {
                isTargetActivityStarted = true
                val startTargetActivityIntent =
                    Checks.checkNotNull(
                        intent.getParcelableExtra<PendingIntent>(
                            TARGET_ACTIVITY_INTENT_KEY
                        )
                    )
                val options =
                    optInToGrantBalPrivileges(
                        intent.getBundleExtra(TARGET_ACTIVITY_OPTIONS_BUNDLE_KEY)
                    )
                try {
                    if (options == null) {
                        // Override and disable FLAG_ACTIVITY_NEW_TASK flag by flagsMask and flagsValue.
                        // PendingIntentRecord#sendInner() will mask the original intent flag with the flagsMask
                        // then override those bits with the new flagsValue specified here. This override is
                        // necessary because if the activity is started as a new task ActivityStarter disposes
                        // the originator information and the result is never be delivered. Instead you will get
                        // an error "Activity is launching as a new task, so cancelling activity result." and
                        // #onActivityResult() will be invoked immediately with result code
                        // Activity#RESULT_CANCELED.
                        startIntentSenderForResult(
                            startTargetActivityIntent.intentSender,  /*requestCode=*/
                            0,  /*fillInIntent=*/
                            null,  /*flagsMask=*/
                            Intent.FLAG_ACTIVITY_NEW_TASK,  /*flagsValues=*/
                            0,  /*extraFlags=*/
                            0
                        )
                    } else {
                        startIntentSenderForResult(
                            startTargetActivityIntent.intentSender,  /*requestCode=*/
                            0,  /*fillInIntent=*/
                            null,  /*flagsMask=*/
                            Intent.FLAG_ACTIVITY_NEW_TASK,  /*flagsValues=*/
                            0,  /*extraFlags=*/
                            0,
                            options
                        )
                    }
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(TAG, "Failed to start target activity.", e)
                    throw RuntimeException(e)
                }
            }
        }

        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            outState.putBoolean(IS_TARGET_ACTIVITY_STARTED_KEY, isTargetActivityStarted)
        }

        override fun onDestroy() {
            super.onDestroy()
            unregisterReceiver(receiver)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (requestCode == 0) {
                val activityResultReceivedActionIntent = Intent(
                    BOOTSTRAP_ACTIVITY_RESULT_RECEIVED
                )
                activityResultReceivedActionIntent.putExtra(
                    BOOTSTRAP_ACTIVITY_RESULT_CODE_KEY,
                    resultCode
                )
                if (data != null) {
                    activityResultReceivedActionIntent.putExtra(
                        BOOTSTRAP_ACTIVITY_RESULT_DATA_KEY,
                        data
                    )
                }
                sendBroadcast(activityResultReceivedActionIntent)
                finish()
            }
        }

        companion object {
            private val TAG: String = BootstrapActivity::class.java.name
            private const val IS_TARGET_ACTIVITY_STARTED_KEY = "IS_TARGET_ACTIVITY_STARTED_KEY"
        }
    }

    /**
     * ActivityResultWaiter listens broadcast messages and waits for [ ][.BOOTSTRAP_ACTIVITY_RESULT_RECEIVED] action. Upon the reception of that action, it retrieves
     * result code and data from the action and makes a local copy. Clients can access to the result
     * by [.getActivityResult].
     */
    private class ActivityResultWaiter(context: Context) {
        private val latch = CountDownLatch(1)
        private var activityResult: Instrumentation.ActivityResult? = null

        /**
         * Constructs ActivityResultWaiter and starts listening to broadcast with the given context. It
         * keeps subscribing the event until it receives [.BOOTSTRAP_ACTIVITY_RESULT_RECEIVED]
         * action.
         */
        init {
            val receiver: BroadcastReceiver =
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        // Stop listening to the broadcast once we get the result.
                        context.unregisterReceiver(this)

                        if (BOOTSTRAP_ACTIVITY_RESULT_RECEIVED == intent.action) {
                            val resultCode =
                                intent.getIntExtra(
                                    BOOTSTRAP_ACTIVITY_RESULT_CODE_KEY, Activity.RESULT_CANCELED
                                )
                            var resultData = intent.getParcelableExtra<Intent>(
                                BOOTSTRAP_ACTIVITY_RESULT_DATA_KEY
                            )
                            if (resultData != null) {
                                // Make a copy of resultData since the lifetime of the given intent is unknown.
                                resultData = Intent(resultData)
                            }
                            activityResult = Instrumentation.ActivityResult(resultCode, resultData)
                            latch.countDown()
                        }
                    }
                }
            val intentFilter = IntentFilter(BOOTSTRAP_ACTIVITY_RESULT_RECEIVED)
            intentFilter.addAction(CANCEL_ACTIVITY_RESULT_WAITER)
            registerBroadcastReceiver(context, receiver, intentFilter)
        }

        /**
         * Waits for the activity result to be available until the timeout and returns the result.
         *
         * @throws NullPointerException if the result doesn't become available after the timeout
         * @return activity result of which [.startActivity] starts
         */
        fun getActivityResult(): Instrumentation.ActivityResult? {
            try {
                latch.await(ActivityLifecycleTimeout.getMillis(), TimeUnit.MILLISECONDS)
            } catch (e: InterruptedException) {
                Log.i(TAG, "Waiting activity result was interrupted", e)
            }
            Checks.checkNotNull(
                activityResult,
                "onActivityResult never be called after %d milliseconds",
                ActivityLifecycleTimeout.getMillis()
            )
            return activityResult
        }

        companion object {
            private val TAG: String = ActivityResultWaiter::class.java.name
        }
    }

    /**
     * An empty activity with style "android:windowIsFloating = false". The style is set by
     * AndroidManifest.xml via "android:theme".
     *
     *
     * When this activity is resumed, it broadcasts [.EMPTY_ACTIVITY_RESUMED] action to
     * notify the state.
     *
     *
     * This activity finishes itself when it receives [.FINISH_EMPTY_ACTIVITIES] action.
     *
     *
     * This activity is used to send an arbitrary resumed Activity to stopped.
     */
    class EmptyActivity : Activity() {
        private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                finish()
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            registerBroadcastReceiver(
                this, receiver, IntentFilter(
                    FINISH_EMPTY_ACTIVITIES
                )
            )

            // disable starting animations
            overridePendingTransition(0, 0)
        }

        override fun finish() {
            super.finish()
            // disable closing animations
            overridePendingTransition(0, 0)
        }

        override fun onResume() {
            super.onResume()
            sendBroadcast(Intent(EMPTY_ACTIVITY_RESUMED))
        }

        override fun onDestroy() {
            super.onDestroy()
            unregisterReceiver(receiver)
        }
    }

    /**
     * An empty activity with style "android:windowIsFloating = true". The style is set by
     * AndroidManifest.xml via "android:theme".
     *
     *
     * When this activity is resumed, it broadcasts [.EMPTY_FLOATING_ACTIVITY_RESUMED] action
     * to notify the state.
     *
     *
     * This activity finishes itself when it receives [.FINISH_EMPTY_ACTIVITIES] action.
     *
     *
     * This activity is used to send an arbitrary resumed Activity to paused.
     */
    class EmptyFloatingActivity : Activity() {
        private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                finish()
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            registerBroadcastReceiver(
                this, receiver, IntentFilter(
                    FINISH_EMPTY_ACTIVITIES
                )
            )

            // disable starting animations
            overridePendingTransition(0, 0)
        }

        override fun finish() {
            super.finish()
            // disable closing animations
            overridePendingTransition(0, 0)
        }

        override fun onResume() {
            super.onResume()
            sendBroadcast(Intent(EMPTY_FLOATING_ACTIVITY_RESUMED))
        }

        override fun onDestroy() {
            super.onDestroy()
            unregisterReceiver(receiver)
        }
    }

    /** A waiter to observe activity result that is started by [.startActivity].  */
    private var activityResultWaiter: ActivityResultWaiter? = null

    /** Starts an Activity using the given intent.  */
    override fun startActivity(intent: Intent, activityOptions: Bundle?) {
        // make sure the intent can resolve an activity
        val ai = intent.resolveActivityInfo(
            ApplicationProvider.getApplicationContext<Context>().packageManager,
            0
        )
            ?: throw RuntimeException("Unable to resolve activity for: $intent")
        // Close empty activities and bootstrap activity if it's running. This might happen if the
        // previous test crashes before it cleans up the state.
        ApplicationProvider.getApplicationContext<Context>().sendBroadcast(
            Intent(
                FINISH_BOOTSTRAP_ACTIVITY
            )
        )
        ApplicationProvider.getApplicationContext<Context>().sendBroadcast(
            Intent(
                FINISH_EMPTY_ACTIVITIES
            )
        )

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        if (Build.VERSION.SDK_INT < 28) {
            check(activityOptions == null) { "Starting an activity with activityOptions is not supported on APIs below 28." }
            InstrumentationRegistry.getInstrumentation().startActivitySync(intent)
        } else {
            InstrumentationRegistry.getInstrumentation().startActivitySync(intent, activityOptions)
        }
    }

    override fun startActivity(intent: Intent) {
        startActivity(intent, null)
    }

    /** Starts an Activity using the given intent.  */
    override fun startActivityForResult(intent: Intent, activityOptionsBundle: Bundle?) {
        // make sure the intent can resolve an activity
        var activityOptionsBundle = activityOptionsBundle
        val ai = intent.resolveActivityInfo(
            ApplicationProvider.getApplicationContext<Context>().packageManager,
            0
        )
        checkNotNull(ai) { "Unable to resolve activity for: $intent" }
        // Close empty activities and bootstrap activity if it's running. This might happen if the
        // previous test crashes before it cleans up the state.
        ApplicationProvider.getApplicationContext<Context>().sendBroadcast(
            Intent(
                FINISH_BOOTSTRAP_ACTIVITY
            )
        )
        ApplicationProvider.getApplicationContext<Context>().sendBroadcast(
            Intent(
                FINISH_EMPTY_ACTIVITIES
            )
        )

        activityResultWaiter = ActivityResultWaiter(ApplicationProvider.getApplicationContext())

        activityOptionsBundle = optInToGrantBalPrivileges(activityOptionsBundle)

        // make an immutable intent if its implicit
        val intentMutability =
            if (intent.getPackage() == null && intent.component == null)
                PendingIntent.FLAG_IMMUTABLE
            else
                PendingIntent.FLAG_MUTABLE

        // Note: Instrumentation.startActivitySync(Intent) cannot be used here because BootstrapActivity
        // may start in different process. Also, we use PendingIntent because the target activity may
        // set "exported" attribute to false so that it prohibits starting the activity outside of their
        // package. With PendingIntent we delegate the authority to BootstrapActivity.
        val bootstrapIntent =
            getIntentForActivity(BootstrapActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .putExtra(
                    TARGET_ACTIVITY_INTENT_KEY,
                    PendingIntent.getActivity(
                        ApplicationProvider.getApplicationContext(),  /* requestCode= */
                        0,
                        intent,  /* flags= */
                        PendingIntent.FLAG_UPDATE_CURRENT or intentMutability
                    )
                )
                .putExtra(TARGET_ACTIVITY_OPTIONS_BUNDLE_KEY, activityOptionsBundle)

        ApplicationProvider.getApplicationContext<Context>()
            .startActivity(bootstrapIntent, activityOptionsBundle)
    }

    override fun startActivityForResult(intent: Intent) {
        startActivityForResult(intent, null)
    }

    override fun getActivityResult(): Instrumentation.ActivityResult {
        checkNotNull(activityResultWaiter) {
            ("You must start Activity first. Make sure you are using launchActivityForResult() to"
                    + " launch an Activity.")
        }
        return activityResultWaiter!!.getActivityResult()!!
    }

    /** Resumes the tested activity by finishing empty activities.  */
    override fun resumeActivity(activity: Activity) {
        checkActivityStageIsIn(activity, Stage.RESUMED, Stage.PAUSED, Stage.STOPPED)
        ApplicationProvider.getApplicationContext<Context>().sendBroadcast(
            Intent(
                FINISH_EMPTY_ACTIVITIES
            )
        )
    }

    /**
     * Pauses the tested activity by starting [EmptyFloatingActivity] on top of the tested
     * activity.
     */
    override fun pauseActivity(activity: Activity) {
        checkActivityStageIsIn(activity, Stage.RESUMED, Stage.PAUSED)
        startFloatingEmptyActivitySync()
    }

    private fun startFloatingEmptyActivitySync() {
        val latch = CountDownLatch(1)
        val receiver: BroadcastReceiver =
            object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    latch.countDown()
                }
            }
        registerBroadcastReceiver(
            ApplicationProvider.getApplicationContext(), receiver, IntentFilter(
                EMPTY_FLOATING_ACTIVITY_RESUMED
            )
        )

        // Starting an arbitrary Activity (android:windowIsFloating = true) forces the tested Activity
        // to the paused state.
        ApplicationProvider.getApplicationContext<Context>()
            .startActivity(
                getIntentForActivity(EmptyFloatingActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )

        try {
            latch.await(ActivityLifecycleTimeout.getMillis(), TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            throw RuntimeException("Failed to pause activity", e)
        } finally {
            ApplicationProvider.getApplicationContext<Context>().unregisterReceiver(receiver)
        }
    }

    /** Stops the tested activity by starting [EmptyActivity] on top of the tested activity.  */
    override fun stopActivity(activity: Activity) {
        checkActivityStageIsIn(activity, Stage.RESUMED, Stage.PAUSED, Stage.STOPPED)
        startEmptyActivitySync()
    }

    private fun startEmptyActivitySync() {
        val latch = CountDownLatch(1)
        val receiver: BroadcastReceiver =
            object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    latch.countDown()
                }
            }
        registerBroadcastReceiver(
            ApplicationProvider.getApplicationContext(), receiver, IntentFilter(
                EMPTY_ACTIVITY_RESUMED
            )
        )

        // Starting an arbitrary Activity (android:windowIsFloating = false) forces the tested Activity
        // to the stopped state.
        ApplicationProvider.getApplicationContext<Context>()
            .startActivity(
                getIntentForActivity(EmptyActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )

        try {
            latch.await(ActivityLifecycleTimeout.getMillis(), TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            throw RuntimeException("Failed to stop activity", e)
        } finally {
            ApplicationProvider.getApplicationContext<Context>().unregisterReceiver(receiver)
        }
    }

    /**
     * Recreates the Activity by [Activity.recreate].
     *
     *
     * Note that [Activity.recreate]'s behavior differs by Android framework version. For
     * example, the version P brings Activity's lifecycle state to the original state after the
     * re-creation. A stopped Activity goes to stopped state after the re-creation in concrete.
     * Whereas the version O ignores [Activity.recreate] method call when the activity is in
     * stopped state. The version N re-creates stopped Activity but brings back to paused state
     * instead of stopped.
     *
     *
     * In short, make sure to set Activity's state to resumed before calling this method otherwise
     * the behavior is the framework version dependent.
     */
    override fun recreateActivity(activity: Activity) {
        checkActivityStageIsIn(activity, Stage.RESUMED, Stage.PAUSED, Stage.STOPPED)
        InstrumentationRegistry.getInstrumentation().runOnMainSync { activity.recreate() }
    }

    override fun finishActivity(activity: Activity) {
        // Stop the activity before calling Activity#finish() as a workaround for the framework bug in
        // API level 15 to 19 where the framework may not call #onStop and #onDestroy if you call
        // Activity#finish() while it is resumed. The exact root cause is unknown but moving the
        // activity back-and-forth between foreground and background helps the finish operation to be
        // executed so here we try finishing the activity by several means. This hack is not necessary
        // for the API level above 19.
        InstrumentationRegistry.getInstrumentation().runOnMainSync { activity.finish() }
        if (activityResultWaiter != null) {
            ApplicationProvider.getApplicationContext<Context>().sendBroadcast(
                Intent(
                    FINISH_BOOTSTRAP_ACTIVITY
                )
            )
            InstrumentationRegistry.getInstrumentation().runOnMainSync { activity.finish() }
        }
        ApplicationProvider.getApplicationContext<Context>().sendBroadcast(
            Intent(
                FINISH_EMPTY_ACTIVITIES
            )
        )
        if (activityResultWaiter != null) {
            ApplicationProvider.getApplicationContext<Context>().sendBroadcast(
                Intent(
                    CANCEL_ACTIVITY_RESULT_WAITER
                )
            )
        }
    }

    companion object {
        /** A bundle key to retrieve an intent to start test target activity in extras bundle.  */
        private const val TARGET_ACTIVITY_INTENT_KEY =
            "test.junit_custom.rules.activity.InstrumentationActivityInvoker.START_TARGET_ACTIVITY_INTENT_KEY"

        /** A bundle key to retrieve an options bundle to start test target activity in extras bundle.  */
        private const val TARGET_ACTIVITY_OPTIONS_BUNDLE_KEY =
            "test.junit_custom.rules.activity.InstrumentationActivityInvoker.TARGET_ACTIVITY_OPTIONS_BUNDLE_KEY"

        /**
         * An intent action broadcasted by [BootstrapActivity] notifying the activity receives
         * activity result and passes payload back to the instrumentation process.
         */
        private const val BOOTSTRAP_ACTIVITY_RESULT_RECEIVED =
            "test.junit_custom.rules.activity.InstrumentationActivityInvoker.BOOTSTRAP_ACTIVITY_RESULT_RECEIVED"

        /**
         * A bundle key to retrieve an activity result code from the extras bundle of [ ][.BOOTSTRAP_ACTIVITY_RESULT_RECEIVED] action.
         */
        private const val BOOTSTRAP_ACTIVITY_RESULT_CODE_KEY =
            "test.junit_custom.rules.activity.InstrumentationActivityInvoker.BOOTSTRAP_ACTIVITY_RESULT_CODE_KEY"

        /**
         * A bundle key to retrieve an activity result data intent from the extras bundle of [ ][.BOOTSTRAP_ACTIVITY_RESULT_RECEIVED] action.
         */
        private const val BOOTSTRAP_ACTIVITY_RESULT_DATA_KEY =
            "test.junit_custom.rules.activity.InstrumentationActivityInvoker.BOOTSTRAP_ACTIVITY_RESULT_DATA_KEY"

        /**
         * An intent action broadcasted by InstrumentActivityInvoker to clean up any [ ]s that are still registered at the end
         */
        private const val CANCEL_ACTIVITY_RESULT_WAITER =
            "test.junit_custom.rules.activity.InstrumentationActivityInvoker.CANCEL_ACTIVITY_RESULT_WAITER"

        /**
         * An intent action broadcasted by [EmptyActivity] notifying the activity becomes resumed
         * state.
         */
        private const val EMPTY_ACTIVITY_RESUMED =
            "test.junit_custom.rules.activity.InstrumentationActivityInvoker.EMPTY_ACTIVITY_RESUMED"

        /**
         * An intent action broadcasted by [EmptyFloatingActivity] notifying the activity becomes
         * resumed state.
         */
        private const val EMPTY_FLOATING_ACTIVITY_RESUMED =
            "test.junit_custom.rules.activity.InstrumentationActivityInvoker.EMPTY_FLOATING_ACTIVITY_RESUMED"

        /** An intent action to notify [BootstrapActivity] to be finished.  */
        private const val FINISH_BOOTSTRAP_ACTIVITY =
            "test.junit_custom.rules.activity.InstrumentationActivityInvoker.FINISH_BOOTSTRAP_ACTIVITY"

        /**
         * An intent action to notify [EmptyActivity] and [EmptyFloatingActivity] to be
         * finished.
         */
        private const val FINISH_EMPTY_ACTIVITIES =
            "test.junit_custom.rules.activity.InstrumentationActivityInvoker.FINISH_EMPTY_ACTIVITIES"

        private fun optInToGrantBalPrivileges(activityOptionsBundle: Bundle?): Bundle? {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                return activityOptionsBundle
            }
            // Initialize a bundle to grant this activities start privilege.
            val updatedActivityOptions =
                ActivityOptions.makeBasic()
                    .setPendingIntentBackgroundActivityStartMode(ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED)
                    .toBundle()
            // Merge the bundle with the one passed in. This allows overriding the start mode if desired.
            if (activityOptionsBundle != null) {
                updatedActivityOptions.putAll(activityOptionsBundle)
            }
            return updatedActivityOptions
        }

        private fun checkActivityStageIsIn(activity: Activity, vararg expected: Stage) {
            checkActivityStageIsIn(activity, HashSet(Arrays.asList(*expected)))
        }

        private fun checkActivityStageIsIn(activity: Activity, expected: Set<Stage>) {
            InstrumentationRegistry.getInstrumentation()
                .runOnMainSync {
                    val stage =
                        ActivityLifecycleMonitorRegistry.getInstance()
                            .getLifecycleStageOf(activity)
                    Checks.checkState(
                        expected.contains(stage),
                        "Activity's stage must be %s but was %s",
                        expected,
                        stage
                    )
                }
        }

        private fun registerBroadcastReceiver(
            context: Context, broadcastReceiver: BroadcastReceiver, intentFilter: IntentFilter
        ) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(broadcastReceiver, intentFilter)
            } else {
                context.registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_EXPORTED)
            }
        }
    }
}