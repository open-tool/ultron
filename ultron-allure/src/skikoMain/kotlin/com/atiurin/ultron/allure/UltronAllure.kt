package com.atiurin.ultron.allure

import com.atiurin.ultron.allure.lifecycle.AllureLifecycle
import com.atiurin.ultron.allure.platform.registerProcessExitHandler
import com.atiurin.ultron.allure.video.VideoAttacher
import com.atiurin.ultron.allure.writer.AllureResultsLocation
import com.atiurin.ultron.allure.writer.AllureResultsWriter
import com.atiurin.ultron.allure.writer.platformResultsSink
import com.atiurin.ultron.log.UltronLog

/**
 * Process-wide Allure lifecycle of Ultron's own writer (desktop, iOS, macOS, js, wasmJs). `UltronAllureConfig`
 * keeps this one lifecycle across its `apply` calls and only redirects its writer (see [redirect]), so a test
 * opened before `beforeFirstTest` re-applies the config is still the current test afterwards. This object
 * exists on the Skiko targets only: on Android the `allure-kotlin` pipeline reports instead, and code shared
 * with an Android target cannot reference it.
 */
object UltronAllure {
    var lifecycle: AllureLifecycle = AllureLifecycle(newWriter(null))

    init {
        // The last implicit test of a run has no later test to close it: write it when the process ends.
        registerProcessExitHandler { closeAtExit() }
    }

    /** Set by the Skiko `UltronAllureConfig` when `videoPolicy` is not NONE. */
    internal var videoAttacher: VideoAttacher? = null

    /** The hook of the current `UltronAllureConfig`; it closes the test left open at exit with its video and log. */
    internal var testRunHook: AllureTestRunHook? = null

    /**
     * Writes the test still open when the process exits. Never throws: on Kotlin/Native an exception escaping the
     * `atexit` callback aborts the process. Should the hook's path (video and log first) fail, the lifecycle closes
     * the test without them.
     */
    internal fun closeAtExit() {
        runCatching { testRunHook?.closeAtExit() }
            .onFailure { UltronLog.error("Allure: closing the test open at exit with its video and log failed: ${it.message}") }
        runCatching { lifecycle.closeAtExit() }
            .onFailure { UltronLog.error("Allure: closing the test open at exit failed: ${it.message}") }
    }

    /**
     * Adds a frame of the current Compose scene to the test video, e.g. after an animation the test waited
     * for by other means. No-op when `videoPolicy` is NONE or no test is running.
     */
    fun captureVideoFrame(label: String = "manual frame") {
        videoAttacher?.captureFrame(label)
    }

    /**
     * Adds the parameter [name] = [value] to the current test's result, e.g. the value of a parameterized or repeated
     * test: `runUltronUiTest { UltronAllure.parameter("user", user); ... }`. The test keeps its name and `fullName`,
     * Allure shows the parameter next to `target`, and the `historyId` includes it, so each invocation is a test of
     * its own rather than a retry of the others. The test is the one of the calling thread's `runUltronUiTest` block
     * or `test { }`; a second value for the same [name] replaces the first. `target` is reserved.
     */
    fun parameter(name: String, value: String) {
        runCatching { lifecycle.addParameter(name, value) }
            .onFailure { UltronLog.error("Allure: adding the parameter '$name' failed: ${it.message}") }
    }

    /** Points the current lifecycle at [resultsDirectory] (resolved like `AllureConfigParams.resultsDirectory`), keeping its state. */
    internal fun redirect(resultsDirectory: String?): AllureLifecycle = lifecycle.also { it.writer = newWriter(resultsDirectory) }

    private fun newWriter(resultsDirectory: String?): AllureResultsWriter =
        AllureResultsWriter(platformResultsSink(AllureResultsLocation.resolve(resultsDirectory)))
}
