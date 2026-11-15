package com.atiurin.ultron.allure.config

import com.atiurin.ultron.allure.config.AllureAttachStrategy.NONE
import com.atiurin.ultron.allure.config.AllureAttachStrategy.OPERATION_FAILURE
import com.atiurin.ultron.allure.config.AllureAttachStrategy.OPERATION_FINISH
import com.atiurin.ultron.allure.config.AllureAttachStrategy.TEST_FAILURE
import com.atiurin.ultron.allure.config.AllureAttachStrategy.TEST_FINISH
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** The predicate that decides whether `ScreenRecordRunListener` is registered for `recordVideoPolicy`. */
class RecordVideoPolicyTest {
    @Test
    fun `TEST_FAILURE and TEST_FINISH record`() {
        assertTrue(setOf(TEST_FAILURE).recordsTests())
        assertTrue(setOf(TEST_FINISH).recordsTests())
        assertTrue(setOf(OPERATION_FAILURE, TEST_FINISH).recordsTests())
    }

    @Test
    fun `OPERATION_* values alone record nothing`() {
        assertFalse(setOf(OPERATION_FAILURE).recordsTests())
        assertFalse(setOf(OPERATION_FAILURE, OPERATION_FINISH).recordsTests())
        assertFalse(emptySet<AllureAttachStrategy>().recordsTests())
    }

    @Test
    fun `NONE disables recording whatever else is in the set`() {
        assertFalse(setOf(NONE).recordsTests())
        assertFalse(setOf(NONE, TEST_FINISH).recordsTests())
    }
}
