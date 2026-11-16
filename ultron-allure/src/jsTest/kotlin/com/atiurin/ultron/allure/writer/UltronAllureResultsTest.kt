package com.atiurin.ultron.allure.writer

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** The in-memory store and console transport of js/wasmJs. */
class UltronAllureResultsTest {
    @AfterTest
    fun cleanup() {
        UltronAllureResults.keepInMemory = true
        UltronAllureResults.sink.clear()
    }

    @Test
    fun consoleLineIsPrefixNameAndBase64() {
        val line = UltronAllureResults.consoleLine("x-result.json", "{}".encodeToByteArray())
        assertEquals("${UltronAllureResults.CONSOLE_PREFIX}x-result.json e30=", line)
        assertTrue(line.startsWith("ULTRON_ALLURE_RESULT "))
    }

    @Test
    fun consoleLineOfBinaryContentHasNoLineBreaks() {
        val bytes = ByteArray(300) { it.toByte() }
        val line = UltronAllureResults.consoleLine("a-attachment.png", bytes)
        assertTrue('\n' !in line && '\r' !in line, "one line per file")
        assertEquals(3, line.split(' ').size, "prefix, name, payload")
    }

    @Test
    fun platformSinkIsTheSharedInMemoryStoreAndKeepsFilesByDefault() {
        val sink = platformResultsSink("ignored/directory")
        sink.write("one.json", "1")
        assertEquals(setOf("one.json"), UltronAllureResults.snapshot().keys)
        assertTrue(sink.description.startsWith("in-memory"))
    }

    @Test
    fun keepInMemoryOffStreamsWithoutRetaining() {
        UltronAllureResults.sink.write("kept.json", "1")
        UltronAllureResults.keepInMemory = false
        UltronAllureResults.sink.write("streamed.json", "2")
        assertEquals(setOf("kept.json"), UltronAllureResults.snapshot().keys)
    }

    @Test
    fun dumpToConsolePrintsOneLinePerStoredFile() {
        UltronAllureResults.sink.write("a.json", "1")
        UltronAllureResults.sink.write("b.png", byteArrayOf(1, 2))
        val lines = mutableListOf<String>()
        UltronAllureResults.dumpToConsole { lines += it }
        assertEquals(
            listOf(UltronAllureResults.consoleLine("a.json", "1".encodeToByteArray()), UltronAllureResults.consoleLine("b.png", byteArrayOf(1, 2))),
            lines,
        )
    }

    @Test
    fun browserHasNoEnvironment() {
        assertNull(platformEnv("ULTRON_ALLURE_RESULTS_DIR"))
        assertEquals(AllureResultsLocation.DEFAULT, AllureResultsLocation.resolve(null))
    }
}
