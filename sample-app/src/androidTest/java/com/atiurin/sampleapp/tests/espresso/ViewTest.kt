package com.atiurin.sampleapp.tests.espresso

import android.widget.RadioButton
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.activity.CustomClicksActivity
import com.atiurin.sampleapp.async.task.CompatAsyncTask
import com.atiurin.sampleapp.async.task.CompatAsyncTask.Companion.COMPAT_ASYNC_TASK_TIME_EXECUTION
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.custom.espresso.action.getView
import com.atiurin.ultron.custom.espresso.base.getViewForcibly
import com.atiurin.ultron.extensions.isChecked
import com.atiurin.ultron.extensions.performOnView
import com.atiurin.ultron.extensions.performOnViewForcibly
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import com.atiurin.ultron.testlifecycle.setupteardown.TearDown
import com.atiurin.ultron.testlifecycle.setupteardown.TearDownRule
import org.junit.Assert
import org.junit.Test

class ViewTest : BaseTest() {

    private val compatAsyncTask = CompatAsyncTask()

    private val startActivity = SetUpRule()
        .add { ActivityScenario.launch(CustomClicksActivity::class.java) }

    private val tearDownRule = TearDownRule().add(STOP_ASYNC_TASK) {
        compatAsyncTask.stop()
    }

    init {
        ruleSequence.add(tearDownRule).addLast(startActivity)
    }

    @Test
    fun actionGetView() {
        val view = withId(R.id.rB_top_left).getView()
        Assert.assertNotNull(view)
    }

    @Test
    @TearDown(STOP_ASYNC_TASK)
    fun actionGetViewForcibly() {
        val startTime = System.currentTimeMillis()
        compatAsyncTask.start()
        val view = withId(R.id.rB_top_right).getViewForcibly()
        Assert.assertNotNull(view)
        Assert.assertTrue(System.currentTimeMillis() < COMPAT_ASYNC_TASK_TIME_EXECUTION + startTime)
    }

    @Test
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
    @TearDown(STOP_ASYNC_TASK)
    fun matcherActionPerformOnViewForcibly() {
        val startTime = System.currentTimeMillis()
        compatAsyncTask.start()
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
    }
}