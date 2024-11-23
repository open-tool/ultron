package com.atiurin.sampleapp.tests.compose

import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.annotations.ExperimentalUltronApi
import com.atiurin.ultron.log.UltronLog
import org.junit.Test

class UltronTestFlowTest2 : BaseTest() {
    @OptIn(ExperimentalUltronApi::class)
    override val beforeAllTests = {
        UltronLog.info("Before Class")
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
        UltronLog.info("UltronTest test 1")
    }

    @Test
    fun someTest2() = test(suppressCommonBefore = true) {
        before {
            UltronLog.info("Before TestMethod 2")
        }.after {
            UltronLog.info("After TestMethod 2")
        }.go {
            UltronLog.info("Run TestMethod 2")
        }
        UltronLog.info("UltronTest test 2")
    }
}