package com.atiurin.ultron.core.test

import com.atiurin.ultron.core.config.UltronCommonConfig
import com.atiurin.ultron.core.test.context.UltronTestContext

class TestMethod(testContext: UltronTestContext) {
    init {
        UltronCommonConfig.testContext = testContext
    }

    private var beforeTest: TestMethod.() -> Unit = {}
    private var afterTest: TestMethod.() -> Unit = {}
    internal var test: TestMethod.() -> Unit = {}

    fun runTest() {
        beforeTest()
        test()
        afterTest()
    }

    fun before(block: TestMethod.() -> Unit) = apply {
        beforeTest = block
    }

    fun after(block: TestMethod.() -> Unit) = apply {
        afterTest = block
    }

    fun run(block: TestMethod.() -> Unit) {
        test = block
    }
}
