package com.atiurin.sampleapp.tests.testlifecycle

import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.annotations.ExperimentalUltronApi
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import com.atiurin.ultron.testlifecycle.setupteardown.TearDownRule
import org.junit.Test

class UltronTestRuleSequenceMergeTest : BaseTest() {
    private val ruleSetUp = SetUpRule().add { UltronLog.info("SetUpRule") }
    private val tearDownRule = TearDownRule().add { UltronLog.info("TearDownRule") }

    init {
        ruleSequence.add(ruleSetUp, tearDownRule)
    }

    @OptIn(ExperimentalUltronApi::class)
    override val beforeFirstTest = {
        UltronLog.info("beforeFirstTest")
    }

    override val beforeTest = {
        UltronLog.info("Before test common")
    }

    override val afterTest = {
        UltronLog.info("After test common")
    }
    @Test
    fun someTest1() = test {
        before {
            UltronLog.info("Before TestMethod 1")
        }.go {
            UltronLog.info("Run TestMethod 1")
        }.after {
            UltronLog.info("After TestMethod 1")
        }
    }
}