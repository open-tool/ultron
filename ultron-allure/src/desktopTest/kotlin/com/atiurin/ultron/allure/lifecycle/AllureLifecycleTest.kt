package com.atiurin.ultron.allure.lifecycle

import com.atiurin.ultron.allure.model.AllureIds
import com.atiurin.ultron.allure.model.AllureJson
import com.atiurin.ultron.allure.model.AllureParameter
import com.atiurin.ultron.allure.video.ApngFrame
import com.atiurin.ultron.allure.video.ApngInfo
import com.atiurin.ultron.allure.video.EncodedVideo
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import com.atiurin.ultron.allure.model.AllureStatus
import com.atiurin.ultron.exceptions.UltronWrapperException
import com.atiurin.ultron.allure.testutil.Harness
import com.atiurin.ultron.allure.writer.AllureResultsWriter
import com.atiurin.ultron.allure.writer.InMemoryResultsSink
import com.atiurin.ultron.core.test.UltronTest
import com.atiurin.ultron.core.test.UltronTestListeners
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class AllureLifecycleTest {
    private val h = Harness()
    private val lc = h.lifecycle

    @Test
    fun `parameters of the test follow the target and go into its historyId`() {
        lc.startTest("login", fullName = "Suite.login")
        lc.addParameter("user", "alice")
        lc.addParameter("lang", "de")
        lc.addParameter("lang", "en")
        lc.stopTest(null)

        val r = h.single()
        assertEquals("Suite.login", r.fullName)
        assertEquals(
            listOf(AllureParameter("target", "desktop"), AllureParameter("user", "alice"), AllureParameter("lang", "en")),
            r.parameters,
        )
        assertEquals(AllureIds.historyId("Suite.login", "desktop", mapOf("user" to "alice", "lang" to "en")), r.historyId)
    }

    @Test
    fun `a parameter named target does not replace the target`() {
        lc.startTest("login", fullName = "Suite.login")
        lc.addParameter("target", "mine")
        lc.stopTest(null)

        val r = h.single()
        assertEquals(listOf(AllureParameter("target", "desktop")), r.parameters)
        assertEquals(AllureIds.historyId("Suite.login", "desktop"), r.historyId)
    }

    @Test
    fun `parameters stay with their test`() {
        lc.startTest("login", fullName = "Suite.login")
        lc.addParameter("user", "alice")
        lc.stopTest(null)
        lc.startTest("login", fullName = "Suite.login")
        lc.stopTest(null)

        assertEquals(listOf(listOf("target", "user"), listOf("target")), h.results().map { r -> r.parameters.map { it.name } })
    }

    @Test
    fun `a JUnit 4 assumption failure skips the test and its open step`() {
        lc.startTest("t1", fullName = "Suite.t1")
        lc.startStep("go")
        lc.stopTest(org.junit.AssumptionViolatedException("not on this machine"))

        val r = h.single()
        assertEquals(AllureStatus.SKIPPED, r.status)
        assertEquals(AllureStatus.SKIPPED, r.steps.single().status)
        assertEquals("not on this machine", r.statusDetails?.message)
        assertTrue(h.sink.snapshot().entries.single { it.key.endsWith("-result.json") }.value.decodeToString().contains("\"skipped\""))
    }

    @Suppress("DEPRECATION")
    @Test
    fun `the internal JUnit 4 assumption, JUnit 5 abort and a wrapped assumption skip the test`() {
        listOf(
            org.junit.internal.AssumptionViolatedException("internal"),
            org.opentest4j.TestAbortedException("aborted"),
            RuntimeException("wrapped", org.junit.AssumptionViolatedException("assumed")),
        ).forEach { skip ->
            lc.startTest("t", fullName = "Suite.t")
            lc.stopTest(skip)
        }

        assertEquals(List(3) { AllureStatus.SKIPPED }, h.results().map { it.status })
    }

    @Test
    fun `open test with a passed step then close writes a passed result`() {
        lc.startTest("t1", fullName = "Suite.t1")
        lc.startStep("click"); h.clock.tick(); lc.stopStep(AllureStatus.PASSED)
        h.clock.tick()
        lc.stopTest(null)

        val r = h.single()
        assertEquals("t1", r.name); assertEquals("Suite.t1", r.fullName)
        assertEquals(AllureStatus.PASSED, r.status)
        assertEquals(1, r.steps.size)
        assertEquals("click", r.steps[0].name); assertEquals(AllureStatus.PASSED, r.steps[0].status)
        assertTrue(r.steps[0].stop > r.steps[0].start)
    }

    @Test
    fun `result carries the target as a parameter and in its historyId`() {
        lc.startTest("t1", fullName = "Suite.t1")
        lc.stopTest(null)

        val r = h.single()
        assertEquals(listOf(AllureParameter("target", "desktop")), r.parameters)
        assertEquals(AllureIds.historyId("Suite.t1", "desktop"), r.historyId)
        assertEquals(listOf("desktop"), r.labels.filter { it.name == "host" }.map { it.value })
    }

    @Test
    fun `a writer replaced during an open test applies from the next test, the open one keeps its own`() {
        val other = InMemoryResultsSink()
        lc.startTest("t1", fullName = "Suite.t1")
        lc.startStep("s"); lc.attach("before", "a", "txt", "text/plain"); lc.stopStep()
        val pending = lc.deferAttachment(lc.currentTest!!, "video", fallbackFrames())

        lc.writer = AllureResultsWriter(other) // re-apply with another resultsDirectory
        lc.attachToTest("after", "b", "txt", "text/plain")
        lc.stopTest(null)
        pending.complete(EncodedVideo(byteArrayOf(1), "mp4", "video/mp4"))

        val r = h.single()
        assertEquals(listOf("s"), r.steps.map { it.name }, "steps kept")
        val sources = (r.steps.flatMap { it.attachments } + r.attachments).map { it.source }
        assertEquals(3, sources.size, sources.toString())
        sources.forEach { assertTrue(it in h.sink.snapshot(), "$it next to the result") }
        assertTrue(other.snapshot().isEmpty(), "nothing of t1 in the new directory: ${other.snapshot().keys}")

        lc.startTest("t2")
        lc.attachToTest("next", "c", "txt", "text/plain")
        lc.stopTest(null)
        assertEquals(2, other.snapshot().size, "t2 goes to the new writer: ${other.snapshot().keys}")
        assertEquals(1, h.results().size, "t2 not in the old directory")
    }

    @Test
    fun `assertion error on a step is failed, other exception is broken`() {
        lc.startTest("t")
        lc.startStep("a"); lc.stopStep(AssertionError("boom"))
        lc.startStep("b"); lc.stopStep(RuntimeException("crash"))
        lc.stopTest(null)

        val (a, b) = h.single().steps
        assertEquals(AllureStatus.FAILED, a.status); assertEquals("boom", a.statusDetails?.message)
        assertTrue(a.statusDetails?.trace.orEmpty().contains("boom"))
        assertEquals(AllureStatus.BROKEN, b.status)
    }

    @Test
    fun `runtime exception wrapping an assertion error is failed, wrapping another runtime is broken`() {
        lc.startTest("t")
        lc.startStep("a"); lc.stopStep(RuntimeException("op failed", UltronWrapperException("assert", AssertionError("boom"))))
        lc.startStep("b"); lc.stopStep(RuntimeException("op failed", IllegalStateException("crash")))
        lc.stopTest(RuntimeException("test", AssertionError("boom")))

        val r = h.single()
        assertEquals(AllureStatus.FAILED, r.steps[0].status)
        assertEquals(AllureStatus.BROKEN, r.steps[1].status)
        assertEquals(AllureStatus.FAILED, r.status)
    }

    @Test
    fun `nested start start stop stop nests steps`() {
        lc.startTest("t")
        lc.startStep("outer"); lc.startStep("inner"); lc.stopStep(AllureStatus.PASSED); lc.stopStep(AllureStatus.PASSED)
        lc.stopTest(null)

        val r = h.single()
        assertEquals(listOf("outer"), r.steps.map { it.name })
        assertEquals(listOf("inner"), r.steps[0].steps.map { it.name })
    }

    @Test
    fun `closing with a throwable sets test status and details`() {
        lc.startTest("t")
        lc.stopTest(AssertionError("expected X"))

        val r = h.single()
        assertEquals(AllureStatus.FAILED, r.status)
        assertEquals("expected X", r.statusDetails?.message)
        assertNotNull(r.statusDetails?.trace)
    }

    @Test
    fun `step started with no open test opens an implicit one`() {
        lc.startStep("orphan"); lc.stopStep(AllureStatus.PASSED)
        assertNotNull(lc.currentTest)
        lc.stopTest(null)

        val r = h.single()
        assertEquals("unknown test", r.name)
        assertTrue(r.labels.any { it.name == "ultron.testBoundary" && it.value == "implicit" })
        assertEquals("orphan", r.steps.single().name)
    }

    @Test
    fun `starting a test closes a previous implicit one`() {
        lc.startStep("orphan"); lc.stopStep(AllureStatus.PASSED)
        lc.startTest("real")
        lc.stopTest(null)

        assertEquals(setOf("unknown test", "real"), h.results().map { it.name }.toSet())
    }

    @Test
    fun `implicit test closed by the next test takes the worst status of its steps`() {
        lc.startStep("a"); lc.stopStep(RuntimeException("crash"))
        lc.startStep("outer"); lc.startStep("inner"); lc.stopStep(AssertionError("boom")); lc.stopStep()
        lc.startStep("c"); lc.stopStep(AllureStatus.PASSED)
        lc.startTest("real")
        lc.stopTest(null)

        val implicit = h.results().single { it.name == "unknown test" }
        assertEquals(AllureStatus.FAILED, implicit.status)
        assertEquals("boom", implicit.statusDetails?.message)
        assertEquals(AllureStatus.PASSED, h.results().single { it.name == "real" }.status)
    }

    @Test
    fun `implicit test with only a broken step is broken, with passed steps only it is passed`() {
        lc.startStep("a"); lc.stopStep(RuntimeException("crash"))
        lc.startTest("next")
        assertEquals(AllureStatus.BROKEN, h.results().single().status)

        lc.stopTest(null)
        lc.startStep("b"); lc.stopStep(AllureStatus.PASSED)
        lc.startTest("last")
        assertEquals(AllureStatus.PASSED, h.results().single { it.name == "unknown test" && it.steps.single().name == "b" }.status)
    }

    @Test
    fun `an implicit test opened by the hook keeps the status the hook reports`() {
        lc.startTest(AllureLifecycle.IMPLICIT_TEST_NAME, implicit = true)
        lc.currentTest!!.openedByHook = true
        lc.startStep("soft"); lc.stopStep(AssertionError("soft failure"))
        lc.stopTest(null)

        assertEquals(AllureStatus.PASSED, h.single().status)
    }

    @Test
    fun `closeAtExit writes the open implicit test with the status of its steps`() {
        lc.startStep("a"); lc.stopStep(AssertionError("boom"))
        lc.closeAtExit()

        val r = h.single()
        assertEquals(AllureStatus.FAILED, r.status)
        assertTrue(r.labels.any { it.name == "ultron.testBoundary" && it.value == "implicit" })
        assertNull(lc.currentTest)
    }

    @Test
    fun `closeAtExit writes an open named test as broken and is a no-op without a test`() {
        lc.startTest("hung"); lc.startStep("waiting")
        lc.closeAtExit()
        lc.closeAtExit()

        val r = h.single()
        assertEquals(AllureStatus.BROKEN, r.status)
        assertEquals("test did not finish before the process exited", r.statusDetails?.message)
        assertEquals(AllureStatus.BROKEN, r.steps.single().status)
    }

    @Test
    fun `a test still open when the next one starts is closed as broken with its open steps`() {
        lc.startTest("hung")
        lc.startStep("waiting")
        lc.startTest("next")
        lc.stopTest(null)

        val hung = h.results().single { it.name == "hung" }
        assertEquals(AllureStatus.BROKEN, hung.status)
        assertEquals("test did not finish before 'next' started", hung.statusDetails?.message)
        assertEquals(AllureStatus.BROKEN, hung.steps.single().status)
        assertEquals(AllureStatus.PASSED, h.results().single { it.name == "next" }.status)
    }

    /**
     * `runUltronUiTest` without a name on a target without discovery opens the implicit `unknown test` through the
     * hook. The hook closes it; when it never does (the process dies inside it, a mocha timeout displaces it with
     * the next test), the test must not be written as passed: nothing confirmed that it finished.
     */
    @Test
    fun `an implicit test opened by the hook and displaced by the next test is broken with its open steps`() {
        lc.startTest(AllureLifecycle.IMPLICIT_TEST_NAME, implicit = true)
        lc.currentTest!!.openedByHook = true
        lc.startStep("done"); lc.stopStep(AllureStatus.PASSED)
        lc.startStep("waiting")
        lc.startTest("next")
        lc.stopTest(null)

        val hung = h.results().single { it.name == AllureLifecycle.IMPLICIT_TEST_NAME }
        assertEquals(AllureStatus.BROKEN, hung.status)
        assertEquals("test did not finish before 'next' started", hung.statusDetails?.message)
        assertEquals(AllureStatus.BROKEN, hung.steps.single { it.name == "waiting" }.status)
        assertEquals(AllureStatus.PASSED, hung.steps.single { it.name == "done" }.status)
        assertEquals(AllureStatus.PASSED, h.results().single { it.name == "next" }.status)
    }

    @Test
    fun `closeAtExit writes an implicit test opened by the hook as broken with its open steps`() {
        lc.startTest(AllureLifecycle.IMPLICIT_TEST_NAME, implicit = true)
        lc.currentTest!!.openedByHook = true
        lc.startStep("waiting")
        lc.closeAtExit()

        val r = h.single()
        assertEquals(AllureStatus.BROKEN, r.status)
        assertEquals("test did not finish before the process exited", r.statusDetails?.message)
        assertEquals(AllureStatus.BROKEN, r.steps.single().status)
        assertTrue(r.labels.any { it.name == "ultron.testBoundary" && it.value == "implicit" })
        assertNull(lc.currentTest)
    }

    @Test
    fun `attachment goes to the current step or to the test when no step is open`() {
        lc.startTest("t")
        lc.startStep("s"); lc.attach("shot", byteArrayOf(1), "png", "image/png"); lc.stopStep(AllureStatus.PASSED)
        lc.attach("log", "hello", "txt", "text/plain")
        lc.stopTest(null)

        val r = h.single()
        assertEquals("shot", r.steps[0].attachments.single().name)
        assertEquals("image/png", r.steps[0].attachments.single().type)
        assertEquals("log", r.attachments.single().name)
        val sources = (r.attachments + r.steps[0].attachments).map { it.source }
        assertTrue(sources.all { it in h.sink.snapshot().keys }, "attachment files written")
    }

    @Test
    fun `stop without an open test is a no-op`() {
        assertNull(lc.stopTest(null))
        assertTrue(h.results().isEmpty())
    }

    @Test
    fun `labels carry framework language and host`() {
        lc.startTest("t"); lc.stopTest(null)
        val names = h.single().labels.map { it.name }
        assertTrue("framework" in names && "language" in names && "host" in names, names.toString())
    }

    /**
     * The JVM shutdown hook (or `atexit`) closes the open test on its own thread while a test thread, one a timeout
     * abandoned, may still be writing to it: the exit waits for that write instead of racing it.
     */
    @Test
    fun `closeAtExit waits for a write the test thread has in progress`() {
        val writing = CountDownLatch(1)
        val release = CountDownLatch(1)
        val sink = InMemoryResultsSink { name, _ ->
            if (name.endsWith(".slow")) { writing.countDown(); release.await(10, TimeUnit.SECONDS) }
        }
        val lifecycle = AllureLifecycle(AllureResultsWriter(sink))
        lifecycle.startTest("t")
        val testThread = thread { lifecycle.attach("slow attachment", byteArrayOf(1), "slow", "application/octet-stream") }
        assertTrue(writing.await(10, TimeUnit.SECONDS), "precondition: the test thread is writing")

        val exit = thread { lifecycle.closeAtExit() }
        exit.join(300) // without a lock the exit writes the result right away, without the attachment
        release.countDown()
        testThread.join(10_000); exit.join(10_000)

        val results = sink.snapshot().filterKeys { it.endsWith("-result.json") }.values
            .map { AllureJson.decodeResult(it.decodeToString()) }
        assertEquals(listOf("slow attachment"), results.single().attachments.map { it.name })
    }

    private class BodyScenario : UltronTest() {
        fun run(body: () -> Unit) = test { go { body() } }
    }

    /** A `test { }` bound to the open test finds it, through the lifecycle's lock, until the test is closed. */
    @Test
    fun `a test { } bound to the open test addresses it until it is closed`() {
        BodyScenario().run {
            val ultronTest = assertNotNull(UltronTestListeners.activeTest)
            lc.startTest("t")
            val open = assertNotNull(lc.currentTest)
            assertNull(lc.testOf(ultronTest), "not bound yet")
            lc.bindUltronTest(open, ultronTest)
            assertSame(open, lc.testOf(ultronTest))
            lc.stopTest(null)
            assertNull(lc.testOf(ultronTest), "closed")
            lc.startTest("next")
            lc.bindUltronTest(open, ultronTest)
            assertNull(lc.testOf(ultronTest), "binding to a closed test is a no-op")
            lc.stopTest(null)
        }
    }

    private fun fallbackFrames() = listOf(ApngFrame(tinyPng(), 500))

    private fun tinyPng(): ByteArray {
        val image = BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB)
        return ByteArrayOutputStream().also { ImageIO.write(image, "png", it) }.toByteArray()
    }

    private val mp4 = EncodedVideo(byteArrayOf(7, 7, 7), "mp4", "video/mp4")

    @Test
    fun `a deferred attachment holds the result until complete, then writes it once with the attachment`() {
        lc.startTest("t")
        val test = lc.currentTest!!
        val pending = lc.deferAttachment(test, "video", fallbackFrames())
        h.clock.tick(100)
        lc.stopTest(null)

        assertTrue(h.results().isEmpty(), "the result waits for the attachment")
        h.clock.tick(5_000)
        pending.complete(mp4)

        val result = h.single()
        assertEquals(AllureStatus.PASSED, result.status)
        assertEquals(listOf("video"), result.attachments.map { it.name })
        assertEquals("video/mp4", result.attachments.single().type)
        assertTrue(result.stop < 1_000 + 5_000, "the stop time is fixed when the test closed, not when the video came")
        assertEquals(1, h.sink.snapshot().keys.count { it.endsWith("-result.json") })
    }

    @Test
    fun `flushing before complete writes an APNG video and a late complete adds nothing`() {
        lc.startTest("t")
        val pending = lc.deferAttachment(lc.currentTest!!, "video", fallbackFrames())
        lc.stopTest(null)

        lc.flushPendingAttachments()

        val result = h.single()
        val attachment = result.attachments.single()
        assertEquals("image/png", attachment.type)
        assertEquals(1, ApngInfo.frameCount(h.sink.snapshot().getValue(attachment.source)))

        pending.complete(mp4)
        assertEquals(1, h.single().attachments.size)
        assertEquals(1, h.sink.snapshot().keys.count { it.endsWith("-result.json") })
    }

    @Test
    fun `closeAtExit flushes the pending attachments`() {
        lc.startTest("t")
        lc.deferAttachment(lc.currentTest!!, "video", fallbackFrames())
        lc.stopTest(null)

        lc.closeAtExit()

        assertEquals("image/png", h.single().attachments.single().type)
    }

    @Test
    fun `an abandoned attachment lets the result through without it`() {
        lc.startTest("t")
        val pending = lc.deferAttachment(lc.currentTest!!, "video", fallbackFrames())
        lc.stopTest(null)

        pending.abandon()

        assertTrue(h.single().attachments.isEmpty())
    }

    @Test
    fun `without deferred attachments the result is written at stopTest as before`() {
        lc.startTest("t")
        lc.stopTest(null)
        assertEquals(1, h.results().size)
    }

    /** The browser `runUltronUiTest` resolves only after this: the last test's result is printed before Karma closes the page. */
    @Test
    fun `awaiting a closed test returns once its pending video is complete and its result written`() {
        lc.startTest("t")
        val test = lc.currentTest!!
        val pending = lc.deferAttachment(test, "video", fallbackFrames())
        lc.stopTest(null)
        val encoder = thread(name = "encoder") { Thread.sleep(200); pending.complete(mp4) }

        runBlocking { lc.awaitResult(test, 10.seconds) }

        assertEquals("video/mp4", h.single().attachments.single().type, "written by the encoder, not by a fallback")
        encoder.join()
    }

    @Test
    fun `awaiting past the bound writes the result with the APNG fallback of that test only`() {
        lc.startTest("slow")
        val slow = lc.currentTest!!
        lc.deferAttachment(slow, "video", fallbackFrames())
        lc.stopTest(null)
        lc.startTest("other")
        val other = lc.deferAttachment(lc.currentTest!!, "video", fallbackFrames())

        runBlocking { lc.awaitResult(slow, 100.milliseconds) }

        assertEquals(listOf("slow"), h.results().map { it.name })
        assertEquals("image/png", h.single().attachments.single().type)
        lc.stopTest(null)
        other.complete(mp4)
        assertEquals("video/mp4", h.results().single { it.name == "other" }.attachments.single().type, "other test untouched")
    }

    @Test
    fun `awaiting a test without pending attachments returns at once`() {
        lc.startTest("t")
        val test = lc.currentTest!!
        lc.stopTest(null)
        runBlocking { lc.awaitResult(test, 10.seconds) }
        assertEquals(1, h.results().size)
    }
}
