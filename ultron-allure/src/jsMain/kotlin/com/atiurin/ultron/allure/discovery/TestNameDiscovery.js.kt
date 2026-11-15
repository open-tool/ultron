package com.atiurin.ultron.allure.discovery

/**
 * Set by the documented Karma adapter: `window.__ultronCurrentTest` is the `kotlin.test` method,
 * `window.__ultronCurrentSuite` the enclosing suites joined with `.` (package and class), absent otherwise.
 */
actual fun discoverTestName(): DiscoveredTest? {
    val name: String? = js("(typeof window !== 'undefined' && window.__ultronCurrentTest) || null") as? String
    val suite: String? = js("(typeof window !== 'undefined' && window.__ultronCurrentSuite) || null") as? String
    return name?.let { DiscoveredTest(it, suite?.takeIf { s -> s.isNotEmpty() }) }
}
