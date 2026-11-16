package com.atiurin.ultron.allure.config

import com.atiurin.ultron.allure.screenrecord.ScreenRecordOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test

/** The members `AllureConfigParams` had as a data class on Android (2.6.6 and earlier) keep working. */
class AllureConfigParamsTest {
    @Test
    fun `the data class constructor takes named parameters with the old defaults`() {
        val params = AllureConfigParams(attachLogcat = false)
        assertFalse(params.attachLogcat)
        assertTrue(params.attachUltronLog)
        assertTrue(params.addConditionsToReport)
        assertTrue(params.detailedAllureReport)
        val failures = mutableSetOf(AllureAttachStrategy.TEST_FAILURE, AllureAttachStrategy.OPERATION_FAILURE)
        assertEquals(failures, params.addScreenshotPolicy)
        assertEquals(failures, params.addHierarchyPolicy)
        assertEquals(mutableSetOf(AllureAttachStrategy.NONE), params.recordVideoPolicy)
    }

    @Test
    fun `the data class constructor takes the six parameters positionally`() {
        val params = AllureConfigParams(
            mutableSetOf(AllureAttachStrategy.NONE),
            mutableSetOf(AllureAttachStrategy.TEST_FAILURE),
            false, false, false, false
        )
        assertEquals(mutableSetOf(AllureAttachStrategy.NONE), params.addScreenshotPolicy)
        assertEquals(mutableSetOf(AllureAttachStrategy.TEST_FAILURE), params.addHierarchyPolicy)
        assertFalse(params.attachUltronLog)
        assertFalse(params.attachLogcat)
        assertFalse(params.addConditionsToReport)
        assertFalse(params.detailedAllureReport)
    }

    @Test
    fun `copy keeps the current values and changes only the given ones`() {
        val original = AllureConfigParams(attachLogcat = false).apply {
            recordVideoPolicy = mutableSetOf(AllureAttachStrategy.TEST_FAILURE)
            screenRecordOptions = ScreenRecordOptions(timeLimitSec = 30)
        }
        val copy = original.copy(attachUltronLog = false)
        assertNotSame(original, copy)
        assertFalse(copy.attachUltronLog)
        assertFalse(copy.attachLogcat)
        assertTrue(original.attachUltronLog)
        assertEquals(mutableSetOf(AllureAttachStrategy.TEST_FAILURE), copy.recordVideoPolicy)
        assertEquals(ScreenRecordOptions(timeLimitSec = 30), copy.screenRecordOptions)
        assertEquals(original.copy(attachUltronLog = false), copy)
    }

    @Test
    fun `instances with the same values are equal and share a hash code`() {
        val first = AllureConfigParams(attachLogcat = false)
        val second = AllureConfigParams(attachLogcat = false)
        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
        assertEquals(AllureConfigParams(), AllureConfigParams())
        assertNotEquals(first, AllureConfigParams())
        assertNotEquals(first, second.copy(screenRecordOptions = ScreenRecordOptions(bitRate = 1)))
    }

    @Test
    fun `destructuring keeps the six data class components`() {
        val (screenshots, hierarchy, ultronLog, logcat, conditions, detailed) = AllureConfigParams(attachLogcat = false)
        assertEquals(AllureConfigParams().addScreenshotPolicy, screenshots)
        assertEquals(AllureConfigParams().addHierarchyPolicy, hierarchy)
        assertTrue(ultronLog)
        assertFalse(logcat)
        assertTrue(conditions)
        assertTrue(detailed)
    }
}
