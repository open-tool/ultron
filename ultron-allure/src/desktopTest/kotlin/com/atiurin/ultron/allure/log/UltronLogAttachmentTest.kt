package com.atiurin.ultron.allure.log

import com.atiurin.ultron.allure.AllureTestRunHook
import com.atiurin.ultron.allure.attach.TestFailureAttacher
import com.atiurin.ultron.allure.test.AllureUltronTestListener
import com.atiurin.ultron.allure.testutil.Harness
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UltronLogAttachmentTest {
    private val h = Harness()

    @Test
    fun `each test gets only its own log lines`() {
        val buffer = AllureLogBuffer()
        buffer.info("first line")
        AllureLogAttacher(h.lifecycle, buffer).let { attacher ->
            h.lifecycle.startTest("t1"); attacher.onTestFinished(); h.lifecycle.stopTest(null)
            buffer.info("second line")
            h.lifecycle.startTest("t2"); attacher.onTestFinished(); h.lifecycle.stopTest(null)
        }

        val (r1, r2) = h.results()
        val text1 = h.sink.snapshot().getValue(r1.attachments.single().source).decodeToString()
        val text2 = h.sink.snapshot().getValue(r2.attachments.single().source).decodeToString()
        assertEquals("ultron_log.txt", r1.attachments.single().name)
        assertTrue("first line" in text1); assertFalse("second line" in text1)
        assertTrue("second line" in text2); assertFalse("first line" in text2)
    }

    @Test
    fun `log of a test closed while a step is still open goes to the test, not into the step`() {
        val buffer = AllureLogBuffer()
        val attacher = AllureLogAttacher(h.lifecycle, buffer)
        h.lifecycle.startTest("hung")
        h.lifecycle.startStep("waiting")
        buffer.info("inside the hung test")

        attacher.onTestFinished()
        h.lifecycle.startTest("next"); h.lifecycle.stopTest(null)

        val hung = h.results().single { it.name == "hung" }
        assertEquals("ultron_log.txt", hung.attachments.single().name)
        assertTrue(hung.steps.single().attachments.isEmpty(), "nothing attached to the open step")
    }

    private fun attachmentText(index: Int): String =
        h.sink.snapshot().getValue(h.results()[index].attachments.single { it.name == "ultron_log.txt" }.source).decodeToString()

    @Test
    fun `lines logged between tests are discarded when runUltronUiTest starts the next one`() {
        val buffer = AllureLogBuffer()
        val hook = AllureTestRunHook(h.lifecycle, TestFailureAttacher(h.lifecycle, emptyList(), emptySet(), emptySet()), AllureLogAttacher(h.lifecycle, buffer))

        buffer.info("config applied before any test")
        hook.beforeTest("t1")
        buffer.info("inside t1")
        hook.afterTest(null)
        buffer.info("between t1 and t2")
        hook.beforeTest("t2")
        buffer.info("inside t2")
        hook.afterTest(null)

        assertEquals("I inside t1", attachmentText(0))
        assertEquals("I inside t2", attachmentText(1))
    }

    @Test
    fun `lines logged in beforeFirstTest are not in the first UltronTest log`() {
        val buffer = AllureLogBuffer()
        val logAttacher = AllureLogAttacher(h.lifecycle, buffer)
        val hook = AllureTestRunHook(h.lifecycle, TestFailureAttacher(h.lifecycle, emptyList(), emptySet(), emptySet()), logAttacher)
        val listener = AllureUltronTestListener(h.lifecycle, hook::closeTest, logAttacher)

        buffer.info("beforeFirstTest")
        listener.beforeTest("MyTest", "t")
        buffer.info("inside")
        listener.afterTest("MyTest", null)

        assertEquals("I inside", attachmentText(0))
    }

    @Test
    fun `a test closed at process exit gets its log`() {
        val buffer = AllureLogBuffer()
        val hook = AllureTestRunHook(h.lifecycle, TestFailureAttacher(h.lifecycle, emptyList(), emptySet(), emptySet()), AllureLogAttacher(h.lifecycle, buffer))
        h.lifecycle.startStep("operation without a test")
        buffer.info("inside the implicit test")
        h.lifecycle.stopStep()

        hook.closeAtExit()

        assertEquals("I inside the implicit test", attachmentText(0))
    }

    /** A thread a timeout abandoned may still log while the exit handler drains the buffer on another one. */
    @Test
    fun `the buffer can be drained while another thread logs`() {
        val buffer = AllureLogBuffer()
        val running = AtomicBoolean(true)
        val logger = thread(name = "abandoned test") { var i = 0; while (running.get()) buffer.info("line ${i++}") }
        val failure = runCatching { repeat(20_000) { buffer.drain() } }.exceptionOrNull()
        running.set(false)
        logger.join(10_000)
        assertNull(failure, "drain failed: $failure")
    }
}
