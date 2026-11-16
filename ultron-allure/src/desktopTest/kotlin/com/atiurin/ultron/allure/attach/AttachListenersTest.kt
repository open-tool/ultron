package com.atiurin.ultron.allure.attach

import com.atiurin.ultron.allure.config.AllureAttachStrategy
import com.atiurin.ultron.allure.config.AllureAttachStrategy.NONE
import com.atiurin.ultron.allure.config.AllureAttachStrategy.OPERATION_FAILURE
import com.atiurin.ultron.allure.config.AllureAttachStrategy.OPERATION_FINISH
import com.atiurin.ultron.allure.config.AllureAttachStrategy.OPERATION_SUCCESS
import com.atiurin.ultron.allure.config.AllureAttachStrategy.TEST_FAILURE
import com.atiurin.ultron.allure.listeners.AttachOnOperationListener
import com.atiurin.ultron.allure.testutil.FakeOperation
import com.atiurin.ultron.allure.testutil.FakeResult
import com.atiurin.ultron.allure.testutil.Harness
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AttachListenersTest {
    private val h = Harness()
    private var captured: ByteArray? = byteArrayOf(0x89.toByte(), 'P'.code.toByte())
    private val provider = object : ArtifactProvider {
        override val kind = "screenshot"
        override val extension = "png"
        override val mimeType = "image/png"
        override fun produce(): ByteArray? = captured
    }

    private fun run(policy: Set<AllureAttachStrategy>, fail: Boolean) {
        val listener = AttachOnOperationListener(h.lifecycle, provider, policy)
        val op = FakeOperation("click")
        val r = if (fail) FakeResult(op, AssertionError("x")) else FakeResult(op)
        h.lifecycle.startTest("t")
        h.lifecycle.startStep(op.name)
        if (fail) listener.afterFailure(r) else listener.afterSuccess(r)
        listener.after(r)
        h.lifecycle.stopStep()
        h.lifecycle.stopTest(null)
    }

    private fun stepAttachmentNames() = h.single().steps.single().attachments.map { it.name }

    @Test
    fun `OPERATION_FAILURE attaches only on failure`() {
        run(setOf(OPERATION_FAILURE), fail = true)
        assertEquals(listOf("screenshot_click"), stepAttachmentNames())
    }

    @Test
    fun `OPERATION_FAILURE does not attach on success`() {
        run(setOf(OPERATION_FAILURE), fail = false)
        assertTrue(stepAttachmentNames().isEmpty())
    }

    @Test
    fun `OPERATION_SUCCESS attaches on success`() {
        run(setOf(OPERATION_SUCCESS), fail = false)
        assertEquals(listOf("screenshot_click"), stepAttachmentNames())
    }

    @Test
    fun `OPERATION_FINISH attaches regardless of outcome`() {
        run(setOf(OPERATION_FINISH), fail = true)
        assertEquals(listOf("screenshot_click"), stepAttachmentNames())
    }

    @Test
    fun `NONE disables even when combined`() {
        run(setOf(NONE, OPERATION_FAILURE, OPERATION_FINISH), fail = true)
        assertTrue(stepAttachmentNames().isEmpty())
    }

    @Test
    fun `provider returning null attaches nothing and keeps the test intact`() {
        captured = null
        run(setOf(OPERATION_FAILURE), fail = true)
        assertTrue(stepAttachmentNames().isEmpty())
        assertEquals(1, h.results().size)
    }

    @Test
    fun `test failure attaches at test level with the test_failure prefix`() {
        h.lifecycle.startTest("t")
        TestFailureAttacher(h.lifecycle, listOf(provider), screenshotPolicy = setOf(TEST_FAILURE), hierarchyPolicy = setOf(TEST_FAILURE))
            .onTestFinished(AssertionError("boom"))
        h.lifecycle.stopTest(AssertionError("boom"))

        assertEquals(listOf("test_failure_screenshot"), h.single().attachments.map { it.name })
    }

    @Test
    fun `test failure attachments go to the test even while a manual step is still open`() {
        h.lifecycle.startTest("t")
        h.lifecycle.startStep("manual step around runUltronUiTest")
        TestFailureAttacher(h.lifecycle, listOf(provider), setOf(TEST_FAILURE), setOf(TEST_FAILURE)).onTestFinished(AssertionError("boom"))
        h.lifecycle.stopStep(AssertionError("boom"))
        h.lifecycle.stopTest(AssertionError("boom"))

        val result = h.single()
        assertEquals(listOf("test_failure_screenshot"), result.attachments.map { it.name })
        assertTrue(result.steps.single().attachments.isEmpty(), "nothing on the step")
    }

    @Test
    fun `test failure attacher is silent on success`() {
        h.lifecycle.startTest("t")
        TestFailureAttacher(h.lifecycle, listOf(provider), setOf(TEST_FAILURE), setOf(TEST_FAILURE)).onTestFinished(null)
        h.lifecycle.stopTest(null)
        assertTrue(h.single().attachments.isEmpty())
    }
}
