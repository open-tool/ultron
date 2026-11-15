package com.atiurin.ultron.allure.listeners

import com.atiurin.ultron.allure.model.AllureStatus
import com.atiurin.ultron.allure.testutil.FakeOperation
import com.atiurin.ultron.allure.testutil.FakeResult
import com.atiurin.ultron.allure.testutil.FakeType
import com.atiurin.ultron.allure.testutil.Harness
import com.atiurin.ultron.core.config.UltronCommonConfig
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AllureOperationStepListenerTest {
    private val h = Harness()
    private val listener = AllureOperationStepListener(h.lifecycle)

    @AfterTest
    fun cleanup() { UltronCommonConfig.operationsExcludedFromListeners.clear() }

    @Test
    fun `successful operation becomes a passed step named after the operation`() {
        val op = FakeOperation("click on button")
        h.lifecycle.startTest("t")
        listener.before(op); listener.afterSuccess(FakeResult(op)); listener.after(FakeResult(op))
        h.lifecycle.stopTest(null)

        val step = h.single().steps.single()
        assertEquals("click on button", step.name); assertEquals(AllureStatus.PASSED, step.status)
    }

    @Test
    fun `failed operation becomes a failed step with the exception message`() {
        val op = FakeOperation("assert text")
        val r = FakeResult(op, AssertionError("nope"))
        h.lifecycle.startTest("t")
        listener.before(op); listener.afterFailure(r); listener.after(r)
        h.lifecycle.stopTest(null)

        val step = h.single().steps.single()
        assertEquals(AllureStatus.FAILED, step.status); assertEquals("nope", step.statusDetails?.message)
    }

    @Test
    fun `operations nested in LIFO order nest as steps`() {
        val outer = FakeOperation("outer"); val inner = FakeOperation("inner")
        h.lifecycle.startTest("t")
        listener.before(outer)
        listener.before(inner); listener.afterSuccess(FakeResult(inner)); listener.after(FakeResult(inner))
        listener.afterSuccess(FakeResult(outer)); listener.after(FakeResult(outer))
        h.lifecycle.stopTest(null)

        val r = h.single()
        assertEquals("outer", r.steps.single().name); assertEquals("inner", r.steps.single().steps.single().name)
    }

    @Test
    fun `excluded operation types produce no step`() {
        UltronCommonConfig.operationsExcludedFromListeners.add(FakeType.CLICK)
        val op = FakeOperation("skip me")
        h.lifecycle.startTest("t")
        listener.before(op); listener.afterSuccess(FakeResult(op)); listener.after(FakeResult(op))
        h.lifecycle.stopTest(null)

        assertTrue(h.single().steps.isEmpty())
    }
}
