package com.atiurin.ultron.allure.platform

internal actual fun platformName(): String = "desktop"

internal actual fun platformTarget(): String = "desktop"

private val testSkipClasses = setOf(
    "org.junit.AssumptionViolatedException",
    "org.junit.internal.AssumptionViolatedException",
    "org.opentest4j.TestAbortedException",
)

internal actual fun isTestSkip(throwable: Throwable): Boolean =
    generateSequence<Class<*>>(throwable.javaClass) { it.superclass }.any { it.name in testSkipClasses }

internal actual fun registerProcessExitHandler(handler: () -> Unit) {
    Runtime.getRuntime().addShutdownHook(Thread(handler, "ultron-allure-exit"))
}
