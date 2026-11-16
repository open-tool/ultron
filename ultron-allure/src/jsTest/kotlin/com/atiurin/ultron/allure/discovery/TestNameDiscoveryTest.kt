package com.atiurin.ultron.allure.discovery

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TestNameDiscoveryTest {
    /** Without the Karma adapter (`window.__ultronCurrentTest`) nothing is discovered; Node has no `window` at all. */
    @Test
    fun nothingIsDiscoveredWithoutTheAdapter() {
        assertNull(discoverTestName())
    }

    /** What the adapter publishes: the test name and the suite path, which becomes the class of the result. */
    @Test
    fun theAdapterGlobalsGiveTheNameAndTheSuiteAsTheClass() {
        js("globalThis.window = { __ultronCurrentTest: 'method', __ultronCurrentSuite: 'pkg.MyTest' }")
        try {
            assertEquals(DiscoveredTest("method", "pkg.MyTest"), discoverTestName())
            assertEquals("pkg.MyTest.method", discoverTestName()?.fullName)
            js("globalThis.window = { __ultronCurrentTest: 'method', __ultronCurrentSuite: '' }")
            assertEquals(DiscoveredTest("method", null), discoverTestName())
        } finally {
            js("delete globalThis.window")
        }
    }
}
