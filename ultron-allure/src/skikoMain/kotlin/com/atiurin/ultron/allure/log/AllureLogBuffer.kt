package com.atiurin.ultron.allure.log

import com.atiurin.ultron.allure.lifecycle.AllureLifecycle
import com.atiurin.ultron.log.ULogger
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

/**
 * Collects Ultron log lines for the current test. Thread-safe: a test thread a timeout abandoned may still log while
 * the next test, or the process exit handler, drains the buffer. With a [lifecycle], the lines of a block or `test { }`
 * whose test is closed already (it finishes late, see `AllureLifecycle.isCurrentRunLate`) are dropped: they never reach
 * the log of the test in progress. On js/wasmJs, which has no threads to tell them apart, they still do. [lifecycle] is
 * asked for every line, so that a buffer outliving the lifecycle (`UltronAllure.lifecycle` is replaceable) asks the current one.
 */
class AllureLogBuffer(private val lifecycle: () -> AllureLifecycle? = { null }) : ULogger("AllureLogBuffer") {
    private val lines = mutableListOf<String>()
    private val lock = SynchronizedObject()

    fun drain(): String = synchronized(lock) {
        val text = lines.joinToString("\n")
        lines.clear()
        text
    }

    private fun add(level: String, message: String, throwable: Throwable? = null) {
        // Asked before the buffer's lock is taken: the lifecycle's lock is never taken inside it.
        if (lifecycle()?.isCurrentRunLate() == true) return
        val trace = throwable?.stackTraceToString()
        synchronized(lock) {
            lines.add("$level $message")
            trace?.let(lines::add)
        }
    }

    override fun info(message: String) = add("I", message)
    override fun info(message: String, throwable: Throwable) = add("I", message, throwable)
    override fun debug(message: String) = add("D", message)
    override fun debug(message: String, throwable: Throwable) = add("D", message, throwable)
    override fun warn(message: String) = add("W", message)
    override fun warn(message: String, throwable: Throwable) = add("W", message, throwable)
    override fun error(message: String) = add("E", message)
    override fun error(message: String, throwable: Throwable) = add("E", message, throwable)
}

/** Attaches the lines logged during the test as `ultron_log.txt`; lines logged between tests are dropped at the next start. */
class AllureLogAttacher(private val lifecycle: AllureLifecycle, private val buffer: AllureLogBuffer) {
    /** Called when a test starts: whatever was logged since the previous test closed (config, `beforeFirstTest`) is discarded. */
    fun onTestStarted() {
        buffer.drain()
    }

    /** Test level even when a step is still open (a test closed as hung when the next one starts), like the video. */
    fun onTestFinished() {
        val text = buffer.drain()
        if (text.isBlank()) return
        lifecycle.attachToTest("ultron_log.txt", text, "txt", "text/plain")
    }
}
