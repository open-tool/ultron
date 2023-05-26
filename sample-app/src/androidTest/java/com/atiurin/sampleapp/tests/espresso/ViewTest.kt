package com.atiurin.sampleapp.tests.espresso

import android.widget.RadioButton
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.activity.CustomClicksActivity
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.custom.espresso.action.getView
import com.atiurin.ultron.custom.espresso.base.findViewForcibly
import com.atiurin.ultron.extensions.perform
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
    fun actionFindViewForcibly() {
        val view = withId(R.id.rB_top_left).findViewForcibly()
        Assert.assertNotNull(view)
    }

    @Test
    fun performViewCheck() {
        val view = withId(R.id.rB_top_left).findViewForcibly()
        view.perform {
            performClick()
        }
        Assert.assertTrue((view as RadioButton).isChecked)
    }
}