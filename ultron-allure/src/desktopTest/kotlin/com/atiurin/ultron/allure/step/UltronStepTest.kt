package com.atiurin.ultron.allure.step

import com.atiurin.ultron.allure.model.AllureStatus
import com.atiurin.ultron.allure.testutil.Harness
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UltronStepTest {
    private val h = Harness()

    @Test
    fun `manual step wraps inner steps and returns the block value`() {
        h.lifecycle.startTest("t")
        val value = step("open settings", h.lifecycle) {
            h.lifecycle.startStep("click"); h.lifecycle.stopStep(AllureStatus.PASSED)
            42
        }
        h.lifecycle.stopTest(null)

        assertEquals(42, value)
        val s = h.single().steps.single()
        assertEquals("open settings", s.name); assertEquals(AllureStatus.PASSED, s.status)
        assertEquals("click", s.steps.single().name)
    }

    @Test
    fun `manual step that throws is failed and rethrows`() {
        h.lifecycle.startTest("t")
        assertFailsWith<AssertionError> { step("bad", h.lifecycle) { throw AssertionError("x") } }
        h.lifecycle.stopTest(null)

        assertEquals(AllureStatus.FAILED, h.single().steps.single().status)
    }
}
