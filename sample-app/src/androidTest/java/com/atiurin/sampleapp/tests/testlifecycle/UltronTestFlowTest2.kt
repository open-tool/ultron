package com.atiurin.sampleapp.tests.testlifecycle

import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.annotations.ExperimentalUltronApi
import com.atiurin.ultron.log.UltronLog
import org.junit.Test

class UltronTestFlowTest2 : BaseTest() {
    var order = 0
    var beforeAllTestCounter = 0
    @OptIn(ExperimentalUltronApi::class)
    override val beforeFirstTests = {
        beforeAllTestCounter = order
        order++
        UltronLog.info("Before Class")
    }
    @Test
    fun someTest1() = test {
        var beforeOrder = -1
        var afterOrder = -1
        var goOrder = -1
        order++
        before {
            beforeOrder = order
            order++
            UltronLog.info("Before TestMethod 1")
        }.go {
            goOrder = order
            order++
            UltronLog.info("Run TestMethod 1")
        }.after {
            afterOrder = order
            assert(beforeAllTestCounter == 0, lazyMessage = { "beforeAllTests block should run before all test" })
            assert(beforeOrder > beforeAllTestCounter, lazyMessage = { "Before block should run after 'Before All'" })
            assert(beforeOrder < goOrder, lazyMessage = { "Before block should run before 'go'" })
            assert(goOrder < afterOrder, lazyMessage = { "After block should run after 'go'" })
        }
    }

    @Test
    fun someTest2() = test(suppressCommonBefore = true) {
        before {
            UltronLog.info("Before TestMethod 2")
        }.after {
            UltronLog.info("After TestMethod 2")
        }.go {
            assert(beforeAllTestCounter == 0, lazyMessage = { "beforeAllTests block should run only once" })
            UltronLog.info("Run TestMethod 2")
        }
    }
}