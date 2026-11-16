package com.atiurin.ultron.core.test

import com.atiurin.ultron.annotations.InternalUltronApi
import com.atiurin.ultron.core.config.UltronCommonConfig
import com.atiurin.ultron.core.test.context.UltronTestContext

class TestMethod(testContext: UltronTestContext) {
    init {
        UltronCommonConfig.testContext = testContext
    }

    private var beforeTest: TestMethod.() -> Unit = {}
    private var afterTest: TestMethod.() -> Unit = {}
    private var test: TestMethod.() -> Unit = {}

    /** The `test { }` this method runs in, set by [UltronTest.test]: the listeners' block events belong to it. */
    internal var activeTest: UltronTestListeners.ActiveTest? = null

    /**
     * Runs `before`, `go` and `after`, then calls [done] with the failure of the test: the `before` failure (the
     * other blocks are skipped then), else the `go` failure, else the `after` one. Each block may start asynchronous
     * work (see [UltronTestAsync]); the next block runs once it has settled.
     */
    internal fun attack(done: (Throwable?) -> Unit) {
        runBlock(TestBlock.BEFORE, beforeTest) { beforeFailure ->
            if (beforeFailure != null) return@runBlock done(beforeFailure)
            runBlock(TestBlock.GO, test) { goFailure ->
                runBlock(TestBlock.AFTER, afterTest) { afterFailure -> done(goFailure ?: afterFailure) }
            }
        }
    }

    /** Runs one block, telling [UltronTestListeners] about it; [then] gets the block's failure as is. */
    @OptIn(InternalUltronApi::class)
    private fun runBlock(block: TestBlock, body: TestMethod.() -> Unit, then: (Throwable?) -> Unit) {
        val owner = activeTest
        owner?.let { UltronTestListeners.fireBeforeBlock(it, block) }
        UltronTestAsync.step({ body() }) { failure ->
            owner?.let { UltronTestListeners.fireAfterBlock(it, block, failure) }
            then(failure)
        }
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
