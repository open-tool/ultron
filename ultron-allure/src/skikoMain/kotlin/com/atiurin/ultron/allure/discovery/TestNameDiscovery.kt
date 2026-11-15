package com.atiurin.ultron.allure.discovery

data class DiscoveredTest(val name: String, val className: String?) {
    val fullName: String get() = className?.let { "$it.$name" } ?: name
}

/** Best-effort lookup of the running test's name; `null` where the runtime offers nothing. */
expect fun discoverTestName(): DiscoveredTest?
