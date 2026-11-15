package com.atiurin.ultron.allure.writer

import com.atiurin.ultron.allure.UltronAllure
import com.atiurin.ultron.log.UltronLog
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * js/wasmJs have no file system in the browser, so results are kept here.
 *
 * Every file is also printed to the browser console the moment it is written, as one
 * `ULTRON_ALLURE_RESULT <fileName> <base64>` line, so a host-side collector that stores the console
 * (Karma's `browserConsoleLogOptions.path`) already has the results of every finished test, and the
 * attachments of the current one, if the page crashes later in the run. [dumpToConsole] prints the
 * whole [snapshot] again; a collector that writes files by name is unaffected by the repetition.
 */
object UltronAllureResults {
    internal val sink = InMemoryResultsSink { name, bytes -> println(consoleLine(name, bytes)) }

    /**
     * Whether files stay in memory after they were printed. `true` (default) keeps every file of the run for
     * [snapshot] and [dumpToConsole]; `false` only streams them to the console, so a long run with `TEST_FINISH`
     * videos does not hold tens of megabytes in the page. Files written before the switch are kept.
     */
    var keepInMemory: Boolean
        get() = sink.keepFiles
        set(value) { sink.keepFiles = value }

    /**
     * Every file written so far. Videos still being encoded (the browser encodes through Promises) are settled
     * first as APNG, so a test is never missing from the snapshot; the encoder's late result is dropped.
     */
    fun snapshot(): Map<String, ByteArray> {
        UltronAllure.lifecycle.flushPendingAttachments()
        return sink.snapshot()
    }

    const val CONSOLE_PREFIX = "ULTRON_ALLURE_RESULT "

    @OptIn(ExperimentalEncodingApi::class)
    fun consoleLine(fileName: String, bytes: ByteArray): String = "$CONSOLE_PREFIX$fileName ${Base64.encode(bytes)}"

    fun dumpToConsole(print: (String) -> Unit = ::println) {
        val files = snapshot()
        files.forEach { (name, bytes) -> print(consoleLine(name, bytes)) }
        UltronLog.info("Allure: dumped ${files.size} result files to console")
    }
}
