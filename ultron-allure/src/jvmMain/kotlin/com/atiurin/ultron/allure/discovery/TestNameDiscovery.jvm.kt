package com.atiurin.ultron.allure.discovery

private val testAnnotations = setOf(
    "org.junit.Test",
    "org.junit.jupiter.api.Test",
    "org.junit.jupiter.api.RepeatedTest",
    "org.junit.jupiter.params.ParameterizedTest",
    "kotlin.test.Test",
)

/**
 * Walks the current stack for the first frame whose class declares a method of that name carrying a
 * test annotation. Reflective, so cached per class.
 */
actual fun discoverTestName(): DiscoveredTest? {
    val frames = Thread.currentThread().stackTrace
    for (frame in frames) {
        val methods = testMethodsOf(frame.className) ?: continue
        if (frame.methodName in methods) return DiscoveredTest(frame.methodName, frame.className)
    }
    return null
}

private val cache = java.util.concurrent.ConcurrentHashMap<String, Set<String>>()

private fun testMethodsOf(className: String): Set<String>? {
    if (className.startsWith("java.") || className.startsWith("jdk.") || className.startsWith("kotlin.") ||
        className.startsWith("org.junit.") || className.startsWith("org.gradle.") || className.startsWith("sun.")
    ) return null
    return cache.getOrPut(className) {
        val cls = runCatching { Class.forName(className, false, Thread.currentThread().contextClassLoader) }.getOrNull()
            ?: return@getOrPut emptySet()
        cls.declaredMethods
            .filter { m -> m.annotations.any { it.annotationClass.java.name in testAnnotations } }
            .map { it.name }
            .toSet()
    }.takeIf { it.isNotEmpty() }
}
