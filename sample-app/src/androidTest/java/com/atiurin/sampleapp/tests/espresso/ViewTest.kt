package com.atiurin.sampleapp.tests.espresso

import android.widget.RadioButton
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.activity.CustomClicksActivity
import com.atiurin.sampleapp.activity.CustomClicksActivity.Companion.COMPAT_ASYNC_TASK_TIME_EXECUTION
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.custom.espresso.action.getView
import com.atiurin.ultron.custom.espresso.base.getViewForcibly
import com.atiurin.ultron.extensions.isChecked
import com.atiurin.ultron.extensions.performOnView
import com.atiurin.ultron.extensions.performOnViewForcibly
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import org.junit.Assert
import org.junit.Test

class ViewTest : BaseTest() {

    private lateinit var customClicksActivity: CustomClicksActivity

    private val startActivity = SetUpRule().add {
        ActivityScenario.launch(CustomClicksActivity::class.java).onActivity { activity ->
            customClicksActivity = activity
        }
    }

    init {
        ruleSequence.addLast(startActivity)
    }

    @Test
    fun actionGetView() {
        val view = withId(R.id.rB_top_left).getView()
        Assert.assertNotNull(view)
    }

    @Test
    fun actionGetViewForcibly() {
        val startTime = System.currentTimeMillis()
        customClicksActivity.startCompatAsyncTask()
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
    fun matcherActionPerformOnViewForcibly() {
        val startTime = System.currentTimeMillis()
        customClicksActivity.startCompatAsyncTask()
        withId(R.id.rB_top_left).apply {
            performOnViewForcibly {
                performClick()
                Assert.assertTrue((this as RadioButton).isChecked)
            }
        }
        Assert.assertTrue(System.currentTimeMillis() < COMPAT_ASYNC_TASK_TIME_EXECUTION + startTime)
    }
}