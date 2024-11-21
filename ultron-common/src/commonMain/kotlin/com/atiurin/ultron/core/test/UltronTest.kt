package com.atiurin.ultron.core.test

import com.atiurin.ultron.core.config.UltronCommonConfig.testContext
import com.atiurin.ultron.core.test.context.DefaultUltronTestContextProvider
import com.atiurin.ultron.core.test.context.UltronTestContextProvider

open class UltronTest(
    private val testContextProvider: UltronTestContextProvider = DefaultUltronTestContextProvider()
) {
    companion object {
        private var isBeforeClassExecuted = false
        private var isAfterClassExecuted = false
    }

    open val beforeClass: () -> Unit = {}
    open val afterClass: () -> Unit = {}
    open val beforeTest: () -> Unit = {}
    open val afterTest: () -> Unit = {}

    fun test(
        suppressCommonBefore: Boolean = false,
        suppressCommonAfter: Boolean = false,
        testBlock: TestMethod.() -> Unit
    ) {
        TestMethod(testContextProvider.provide()).apply {
            testContext = testContext
            if (!isBeforeClassExecuted) {
                beforeClass()
                isBeforeClassExecuted = true
            }
            if (!suppressCommonBefore) {
                beforeTest()
            }
            testBlock()
            runTest()
            if (!suppressCommonAfter) {
                afterTest()
            }
            if (!isAfterClassExecuted) {
                afterClass()
                isAfterClassExecuted = true
            }
        }
    }
}
