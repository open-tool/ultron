package com.atiurin.ultron.allure.discovery

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JvmTestNameDiscoveryTest {
    @Test
    fun `finds the running junit test method and class from the stack`() {
        val found = helperThatCallsDiscovery()
        assertEquals("finds the running junit test method and class from the stack", found?.name)
        assertEquals(JvmTestNameDiscoveryTest::class.qualifiedName, found?.className)
    }

    @Test
    fun `returns null when no test method is on the stack`() {
        val fromThread = java.util.concurrent.Executors.newSingleThreadExecutor()
            .submit<DiscoveredTest?> { discoverTestName() }.get()
        assertNull(fromThread)
    }

    @Test
    fun `junit 5 parameterized and repeated tests are test methods too`() {
        assertEquals("parameterized", parameterized()?.name)
        assertEquals("repeated", repeated()?.name)
    }

    /** Not run by JUnit (see the stand-in annotations); called directly, it is the nearest test frame on the stack. */
    @org.junit.jupiter.params.ParameterizedTest
    fun parameterized(): DiscoveredTest? = helperThatCallsDiscovery()

    @org.junit.jupiter.api.RepeatedTest
    fun repeated(): DiscoveredTest? = helperThatCallsDiscovery()

    private fun helperThatCallsDiscovery(): DiscoveredTest? = discoverTestName()
}
