package com.atiurin.ultron.allure.discovery

private fun readCurrentTest(): JsAny? = js("(typeof window !== 'undefined' && window.__ultronCurrentTest) || null")
private fun readCurrentSuite(): JsAny? = js("(typeof window !== 'undefined' && window.__ultronCurrentSuite) || null")

/**
 * Set by the documented Karma adapter: `window.__ultronCurrentTest` is the `kotlin.test` method,
 * `window.__ultronCurrentSuite` the enclosing suites joined with `.` (package and class), absent otherwise.
 */
actual fun discoverTestName(): DiscoveredTest? {
    val name = (readCurrentTest() as? JsString)?.toString() ?: return null
    val suite = (readCurrentSuite() as? JsString)?.toString()?.takeIf { it.isNotEmpty() }
    return DiscoveredTest(name, suite)
}
