package com.atiurin.ultron.allure.lifecycle

import com.atiurin.ultron.allure.model.AllureJson
import com.atiurin.ultron.allure.video.EncodedVideo
import com.atiurin.ultron.allure.writer.AllureResultsWriter
import com.atiurin.ultron.allure.writer.InMemoryResultsSink
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

/**
 * The browser `runUltronUiTest` ends only after the result of its test is written: here the video completes through
 * a real timer, as a WebCodecs Promise does, while `runTest` would skip a virtual-time wait.
 */
class AwaitResultJsTest {
    @Test
    fun awaitingReturnsOnceTheVideoCompletedAndTheResultIsWritten() = runTest {
        val sink = InMemoryResultsSink()
        val lifecycle = AllureLifecycle(AllureResultsWriter(sink))
        lifecycle.startTest("t")
        val test = lifecycle.currentTest!!
        val pending = lifecycle.deferAttachment(test, "video", emptyList())
        lifecycle.stopTest(null)
        CoroutineScope(Dispatchers.Default).launch {
            delay(100)
            pending.complete(EncodedVideo(byteArrayOf(1, 2), "mp4", "video/mp4"))
        }

        lifecycle.awaitResult(test, 10.seconds)

        val result = sink.snapshot().filterKeys { it.endsWith("-result.json") }.values.single()
        assertEquals("video/mp4", AllureJson.decodeResult(result.decodeToString()).attachments.single().type)
    }
}
