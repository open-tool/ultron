package com.atiurin.sampleapp.tests.testlifecycle

import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.annotations.ExperimentalUltronApi
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import com.atiurin.ultron.testlifecycle.setupteardown.TearDownRule
import org.junit.Assert
import org.junit.Test

class UltronTestFlowTest : BaseTest() {
    companion object {
        var order = 0
        var isBeforeFirstTestCounter = 0
        var commonBeforeOrder = -1
        var commonAfterOrder = -1
        var afterOrder = -1
    }
    val ruleSetUp = SetUpRule().add { UltronLog.info("SetUpRule") }
    val tearDownRule = TearDownRule().add { UltronLog.info("TearDownRule") }

    init {
        ruleSequence.add(ruleSetUp, tearDownRule)
    }

    @OptIn(ExperimentalUltronApi::class)
    override val beforeFirstTest = {
        isBeforeFirstTestCounter++
        UltronLog.info("beforeFirstTest")
    }

    override val beforeTest = {
        commonBeforeOrder = order
        order++
        UltronLog.info("Before test common")
    }
    override val afterTest = {
        commonAfterOrder = order
        order++
        assert(afterOrder < commonAfterOrder, lazyMessage = { "CommonAfter block should run after 'after' test block" })
        UltronLog.info("After test common")
    }

    @Test
    fun someTest1() = test {
        var beforeOrder = -1
        var goOrder = -1
        order++
        before {
            assert(isBeforeFirstTestCounter == 1, lazyMessage = { "beforeFirstTest block should run before commonBefore block" })
            beforeOrder = order
            order++
            UltronLog.info("Before TestMethod 1")
        }.go {
            goOrder = order
            order++
            UltronLog.info("Run TestMethod 1")
        }.after {
            afterOrder = order
            order++
            assert(commonBeforeOrder < beforeOrder, lazyMessage = { "beforeOrder block should run after commonBefore block" })
            assert(beforeOrder < goOrder, lazyMessage = { "Before block should run before 'go'" })
            assert(goOrder < afterOrder, lazyMessage = { "After block should run after 'go'" })
        }
    }

    @Test
    fun someTest2() = test(suppressCommonBefore = true) {
        before {
            UltronLog.info("Before TestMethod 2")
        }
        after {
            UltronLog.info("After TestMethod 2")
        }
        go {
            assert(isBeforeFirstTestCounter == 1, lazyMessage = { "beforeFirstTest block should run only once" })
            UltronLog.info("Run TestMethod 2")
        }
    }

    @Test
    fun simpleTest() = test {
        assert(isBeforeFirstTestCounter == 1, lazyMessage = { "beforeAllTests block should run only once" })
        UltronLog.info("UltronTest simpleTest")
    }

    @Test
    fun afterBlockExecutedOnFailedTest() {
        var isAfterExecuted = false
        runCatching {
            test {
                go{
                    throw RuntimeException("test exception")
                }
                after {
                    isAfterExecuted = true
                }
            }
        }
        Assert.assertTrue(isAfterExecuted)
    }
}