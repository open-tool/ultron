package com.atiurin.ultron.core.test

import com.atiurin.ultron.core.config.UltronCommonConfig
import com.atiurin.ultron.core.test.context.UltronTestContext

class TestMethod(testContext: UltronTestContext) {
    init {
        UltronCommonConfig.testContext = testContext
    }

    private var beforeTest: TestMethod.() -> Unit = {}
    private var afterTest: TestMethod.() -> Unit = {}
    private var test: TestMethod.() -> Unit = {}

    internal fun attack() {
        var throwable: Throwable? = null
        beforeTest()
        runCatching(test).onFailure { ex ->
            throwable = ex
        }
        runCatching(afterTest).onFailure { ex ->
            throwable?.let { throw it }
            throw ex
        }
        throwable?.let { throw it }
    }

    fun before(block: TestMethod.() -> Unit) = apply {
        beforeTest = block
    }

    fun after(block: TestMethod.() -> Unit) = apply {
        afterTest = block
    }

    fun go(block: TestMethod.() -> Unit) = apply {
        test = block
    }
}
