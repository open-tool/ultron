package com.atiurin.sampleapp.tests.espresso

import android.content.Intent
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.atiurin.sampleapp.activity.BusyActivity
import com.atiurin.sampleapp.activity.MainActivity
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.core.compose.createSimpleUltronComposeRule
import com.atiurin.ultron.core.compose.createUltronComposeRule
import com.atiurin.ultron.testlifecycle.activity.UltronActivityRule
import org.junit.Test
import org.junit.rules.Timeout
import java.util.concurrent.TimeUnit

class UltronActivityRuleTest: BaseTest() {

    //private val activityTestRule = ActivityScenarioRule(MainActivity::class.java)
    //private val activityTestRule = UltronActivityRule(MainActivity::class.java)
    //private val activityTestRule = createUltronComposeRule<MainActivity>()
    private val activityTestRule = createSimpleUltronComposeRule<MainActivity>()
    private val timeoutRule: Timeout = Timeout
        .builder()
        .withTimeout(100, TimeUnit.SECONDS)
        .withLookingForStuckThread(true)
        .build()

    init {
        ruleSequence.add(timeoutRule, activityTestRule)
    }

    @Test
    fun appNotIdle() {
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            BusyActivity::class.java
        )
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        InstrumentationRegistry.getInstrumentation().targetContext.startActivity(intent)
        assert(true)
    }
}