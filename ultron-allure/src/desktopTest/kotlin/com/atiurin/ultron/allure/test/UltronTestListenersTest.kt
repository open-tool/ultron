package com.atiurin.ultron.allure.test

import com.atiurin.ultron.core.test.TestBlock
import com.atiurin.ultron.core.test.UltronTest
import com.atiurin.ultron.core.test.UltronTestListener
import com.atiurin.ultron.core.test.UltronTestListeners
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** The common test-lifecycle events of `ultron-common`, exercised through a real [UltronTest] subclass. */
class UltronTestListenersTest {
    /** Listeners receive the qualified class name on the JVM; the sample classes are nested in this one. */
    private val Q = UltronTestListenersTest::class.qualifiedName + "."

    private val events = mutableListOf<String>()
    private val recorder = object : UltronTestListener {
        override val id = "recorder"
        override fun beforeTest(testClass: String, testName: String?) { events += "test:$testClass" + (testName?.let { ":$it" } ?: "") }
        override fun afterTest(testClass: String, throwable: Throwable?) { events += "end:${throwable?.message}" }
        override fun beforeBlock(testClass: String, block: TestBlock) { events += "+$block" }
        override fun afterBlock(testClass: String, block: TestBlock, throwable: Throwable?) { events += "-$block:${throwable?.message}" }
    }

    class SampleUltronTest : UltronTest() {
        fun passing() = test { before { }.go { }.after { } }
        fun failingInGo() = test { before { }.go { throw IllegalStateException("go failed") }.after { } }
        fun plain() = test { }
        fun named() = test(name = "named scenario") { go { } }
        fun namedWithFlags() = test(suppressCommonBefore = true, suppressCommonAfter = true, name = "flagged") { }
    }

    /** The source shape that compiled against master: the suppress flags passed positionally. */
    class PositionalFlagsUltronTest : UltronTest() {
        override val beforeTest: () -> Unit = { events += "beforeTest" }
        override val afterTest: () -> Unit = { events += "afterTest" }
        fun suppressBefore() = test(true) { go { events += "go" } }
        fun suppressBeforeKeepAfter() = test(true, false) { go { events += "go" } }
        fun suppressBoth() = test(true, true) { go { events += "go" } }
        companion object { val events = mutableListOf<String>() }
    }

    class FailingBeforeUltronTest : UltronTest() {
        override val beforeTest: () -> Unit = { throw IllegalStateException("before failed") }
        fun run() = test { go { events += "go ran" } }
        companion object { val events = mutableListOf<String>() }
    }

    @AfterTest
    fun cleanup() = UltronTestListeners.clear()

    @Test
    fun `a failing common beforeTest closes the test with that throwable and skips the blocks`() {
        UltronTestListeners.register(recorder)
        assertFailsWith<IllegalStateException> { FailingBeforeUltronTest().run() }
        assertEquals(listOf("test:${Q}FailingBeforeUltronTest", "end:before failed"), events)
        assertEquals(emptyList(), FailingBeforeUltronTest.events)
    }

    @Test
    fun `blocks and the test itself are reported in order with the class name`() {
        UltronTestListeners.register(recorder)
        SampleUltronTest().passing()
        assertEquals(
            listOf("test:${Q}SampleUltronTest", "+BEFORE", "-BEFORE:null", "+GO", "-GO:null", "+AFTER", "-AFTER:null", "end:null"),
            events
        )
    }

    @Test
    fun `a failing go block is reported on the block and on the test, after still runs`() {
        UltronTestListeners.register(recorder)
        assertFailsWith<IllegalStateException> { SampleUltronTest().failingInGo() }
        assertEquals(
            listOf("test:${Q}SampleUltronTest", "+BEFORE", "-BEFORE:null", "+GO", "-GO:go failed", "+AFTER", "-AFTER:null", "end:go failed"),
            events
        )
    }

    @Test
    fun `a test without blocks still reports the empty blocks`() {
        UltronTestListeners.register(recorder)
        SampleUltronTest().plain()
        assertEquals("test:${Q}SampleUltronTest", events.first())
        assertEquals("end:null", events.last())
    }

    @Test
    fun `a throwing listener never breaks the test and registering twice replaces by id`() {
        UltronTestListeners.register(object : UltronTestListener {
            override val id = "recorder"
            override fun beforeTest(testClass: String, testName: String?) = throw IllegalStateException("listener exploded")
        })
        UltronTestListeners.register(recorder)
        SampleUltronTest().passing()
        assertEquals(1, UltronTestListeners.all().size)
        assertEquals("test:${Q}SampleUltronTest", events.first())
    }

    @Test
    fun `the explicit test name is passed to the listener`() {
        UltronTestListeners.register(recorder)
        SampleUltronTest().named()
        assertEquals("test:${Q}SampleUltronTest:named scenario", events.first())
    }

    @Test
    fun `the name can be combined with the suppress flags`() {
        UltronTestListeners.register(recorder)
        SampleUltronTest().namedWithFlags()
        assertEquals("test:${Q}SampleUltronTest:flagged", events.first())
    }

    @Test
    fun `the suppress flags keep compiling and working when passed positionally`() {
        UltronTestListeners.register(recorder)
        PositionalFlagsUltronTest.events.clear()
        PositionalFlagsUltronTest().suppressBefore()
        assertEquals(listOf("go", "afterTest"), PositionalFlagsUltronTest.events)
        PositionalFlagsUltronTest.events.clear()
        PositionalFlagsUltronTest().suppressBeforeKeepAfter()
        assertEquals(listOf("go", "afterTest"), PositionalFlagsUltronTest.events)
        PositionalFlagsUltronTest.events.clear()
        PositionalFlagsUltronTest().suppressBoth()
        assertEquals(listOf("go"), PositionalFlagsUltronTest.events)
        assertEquals(listOf("test:${Q}PositionalFlagsUltronTest", "end:null"), events.filter { it.startsWith("test:") || it.startsWith("end:") }.take(2))
    }
}
