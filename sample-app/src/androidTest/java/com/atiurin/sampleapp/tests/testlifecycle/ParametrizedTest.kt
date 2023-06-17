package com.atiurin.sampleapp.tests.testlifecycle

import com.atiurin.sampleapp.activity.ComposeElementsActivity
import com.atiurin.sampleapp.framework.Log
import com.atiurin.ultron.core.compose.createUltronComposeRule
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.testlifecycle.rulesequence.RuleSequence
import com.atiurin.ultron.testlifecycle.setupteardown.SetUp
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import com.atiurin.ultron.testlifecycle.setupteardown.TearDown
import com.atiurin.ultron.testlifecycle.setupteardown.TearDownRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ParametrizedTest(private val testValue1: String, private val testValue2: String) {
    var commonSetUpDone = false
    var singleTestSetUpDone = false
    var commonTearDownDone = false
    var singleTestTearDownDone = false
    val activityRule = createUltronComposeRule<ComposeElementsActivity>()

    val beforeEachRule = SetUpRule("Precondition before each test").add {
        commonSetUpDone = true
    }.add(key = "singleTestSetUpDone") {
        singleTestSetUpDone = true
    }
    val afterEachRule = TearDownRule("Post condition after each test").add {
        commonTearDownDone = true
    }.add(key = "singleTestTearDownDone") {
        singleTestTearDownDone = true
    }
    val controlTearDown = TearDownRule()
        .add {
            Assert.assertTrue(commonSetUpDone)
            Assert.assertTrue(singleTestSetUpDone)
            Assert.assertTrue(commonTearDownDone)
            Assert.assertTrue(singleTestTearDownDone)
        }

    @get:Rule
    val ruleSequence = RuleSequence(activityRule, beforeEachRule, afterEachRule, controlTearDown)

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun testData() = listOf(
            arrayOf("param1_1", "param1_2"),
            arrayOf("param2_1", "param2_2"),
        )
    }

    @Test
    @SetUp("singleTestSetUpDone")
    @TearDown("singleTestTearDownDone")
    fun myAwesomeTest() {
        UltronLog.debug("testValue1 = $testValue1")
        UltronLog.debug("testValue2 = $testValue2")
    }
}
