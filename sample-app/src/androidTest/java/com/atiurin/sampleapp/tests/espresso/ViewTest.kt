package com.atiurin.sampleapp.tests.espresso

import android.widget.RadioButton
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.activity.CustomClicksActivity
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

    private val startActivity = SetUpRule().add {
        ActivityScenario.launch(CustomClicksActivity::class.java)
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
        val view = withId(R.id.rB_top_left).getViewForcibly()
        Assert.assertNotNull(view)
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
        withId(R.id.rB_top_left).apply {
            performOnViewForcibly {
                performClick()
                Assert.assertTrue((this as RadioButton).isChecked)
            }
            isChecked()
        }
    }
}