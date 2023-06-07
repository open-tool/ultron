package com.atiurin.sampleapp.tests.espresso

import android.content.Intent
import android.widget.RadioButton
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.activity.CustomClicksActivity
import com.atiurin.sampleapp.async.task.CompatAsyncTask.Companion.ASYNC
import com.atiurin.sampleapp.async.task.CompatAsyncTask.Companion.COMPAT_ASYNC_TASK_TIME_EXECUTION
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.custom.espresso.action.getView
import com.atiurin.ultron.custom.espresso.base.getViewForcibly
import com.atiurin.ultron.extensions.isChecked
import com.atiurin.ultron.extensions.performOnView
import com.atiurin.ultron.extensions.performOnViewForcibly
import com.atiurin.ultron.testlifecycle.setupteardown.SetUp
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import com.atiurin.ultron.testlifecycle.setupteardown.TearDown
import com.atiurin.ultron.testlifecycle.setupteardown.TearDownRule
import org.junit.Assert
import org.junit.Test

class ViewTest : BaseTest() {

    private lateinit var customClicksActivity: CustomClicksActivity

    private val startActivity = SetUpRule()
        .add(START_ACTIVITY) {
            ActivityScenario.launch(CustomClicksActivity::class.java).onActivity { activity ->
                customClicksActivity = activity
            }
        }
        .add(START_ACTIVITY_WITH_ASYNC_TASK) {
            val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, CustomClicksActivity::class.java)
            intent.putExtra(ASYNC, true)
            ActivityScenario.launch<CustomClicksActivity>(intent).onActivity { activity ->
                customClicksActivity = activity
            }
        }

    private val tearDownRule = TearDownRule().add(STOP_ASYNC_TASK) {
        customClicksActivity.stopCompatAsyncTask()
    }

    init {
        ruleSequence.add(startActivity).add(tearDownRule)
    }

    @Test
    @SetUp(START_ACTIVITY)
    fun actionGetView() {
        val view = withId(R.id.rB_top_left).getView()
        Assert.assertNotNull(view)
    }

    @Test
    @SetUp(START_ACTIVITY_WITH_ASYNC_TASK)
    @TearDown(STOP_ASYNC_TASK)
    fun actionGetViewForcibly() {
        val startTime = System.currentTimeMillis()
        val view = withId(R.id.rB_top_right).getViewForcibly()
        Assert.assertNotNull(view)
        Assert.assertTrue(System.currentTimeMillis() < COMPAT_ASYNC_TASK_TIME_EXECUTION + startTime)
    }

    @Test
    @SetUp(START_ACTIVITY)
    fun matcherActionPerformOnView() {
        withId(R.id.rB_top_left).apply {
            performOnView {
                performClick()
                Assert.assertTrue((this as RadioButton).isChecked)
            }
            isChecked()
        }
    }

    @Test
    @SetUp(START_ACTIVITY_WITH_ASYNC_TASK)
    @TearDown(STOP_ASYNC_TASK)
    fun matcherActionPerformOnViewForcibly() {
        val startTime = System.currentTimeMillis()
        withId(R.id.rB_top_left).apply {
            performOnViewForcibly {
                performClick()
                Assert.assertTrue((this as RadioButton).isChecked)
            }
        }
        Assert.assertTrue(System.currentTimeMillis() < COMPAT_ASYNC_TASK_TIME_EXECUTION + startTime)
    }

    companion object {
        const val STOP_ASYNC_TASK = "STOP_ASYNC_TASK"
        const val START_ACTIVITY = "START_ACTIVITY"
        const val START_ACTIVITY_WITH_ASYNC_TASK = "START_ACTIVITY_WITH_ASYNC_TASK"
    }
}