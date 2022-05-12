package com.atiurin.sampleapp.tests.espresso

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.activity.CustomClicksActivity
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.extensions.*
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import org.junit.Test

class CustomClicksTest : BaseTest() {

    private val startActivity = SetUpRule().add {
        ActivityScenario.launch(CustomClicksActivity::class.java)
    }

    init {
        ruleSequence.addLast(startActivity)
    }

    @Test
    fun clickTopLeft() {
        withId(R.id.imageView).clickTopLeft(offsetX = 30, offsetY = 30)
        withId(R.id.rB_top_left).isChecked()
    }

    @Test
    fun clickTopCenter() {
        withId(R.id.imageView).clickTopCenter(offsetY = 30)
        withId(R.id.rB_top_center).isChecked()
    }

    @Test
    fun clickTopRight() {
        withId(R.id.imageView).clickTopRight(offsetX = -30, offsetY = 30)
        withId(R.id.rB_top_right).isChecked()
    }

    @Test
    fun clickCenterRight() {
        withId(R.id.imageView).clickCenterRight(offsetX = -30)
        withId(R.id.rB_center_right).isChecked()
    }

    @Test
    fun clickBottomRight() {
        withId(R.id.imageView).clickBottomRight(offsetX = -30, offsetY = -30)
        withId(R.id.rB_bottom_right).isChecked()
    }

    @Test
    fun clickBottomCenter() {
        withId(R.id.imageView).clickBottomCenter(offsetY = -30)
        withId(R.id.rB_bottom_center).isChecked()
    }

    @Test
    fun clickBottomLeft() {
        withId(R.id.imageView).clickBottomLeft(offsetX = 30, offsetY = -30)
        withId(R.id.rB_bottom_left).isChecked()
    }

    @Test
    fun clickCenterLeft() {
        withId(R.id.imageView).clickCenterLeft(offsetX = 30)
        withId(R.id.rB_center_left).isChecked()
    }
}