package com.atiurin.ultron.allure.config

import com.atiurin.ultron.allure.UltronAllure
import com.atiurin.ultron.allure.lifecycle.AllureLifecycle
import com.atiurin.ultron.allure.listeners.AllureOperationStepListener
import com.atiurin.ultron.allure.listeners.AttachOnOperationListener
import com.atiurin.ultron.allure.model.AllureJson
import com.atiurin.ultron.allure.video.VideoFormat
import com.atiurin.ultron.allure.video.VideoQuality
import com.atiurin.ultron.allure.writer.AllureResultsWriter
import com.atiurin.ultron.allure.writer.platformResultsSink
import com.atiurin.ultron.annotations.ExperimentalUltronApi
import com.atiurin.ultron.core.compose.UltronTestRunHooks
import com.atiurin.ultron.core.config.UltronCommonConfig
import com.atiurin.ultron.core.test.UltronTest
import com.atiurin.ultron.core.test.UltronTestListeners
import com.atiurin.ultron.log.UltronLog
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class UltronAllureConfigTest {
    @AfterTest
    fun cleanup() {
        UltronCommonConfig.clearListeners()
        UltronTestRunHooks.clear()
    }

    @Test
    fun `applyRecommended registers step listener, two attach listeners and the run hook`() {
        UltronAllureConfig.applyRecommended()

        val ids = UltronCommonConfig.getListeners().map { it.id }
        assertTrue(AllureOperationStepListener::class.simpleName!! in ids, ids.toString())
        assertEquals(2, ids.count { it.startsWith(AttachOnOperationListener::class.simpleName!!) }, ids.toString())
        assertNotNull(UltronTestRunHooks.current())
    }

    @Test
    fun `step listener is registered after the attach listeners so OPERATION_FINISH attachments land in the step`() {
        UltronAllureConfig.applyRecommended()

        val ids = UltronCommonConfig.getListeners().map { it.id }
        val step = ids.indexOf(AllureOperationStepListener::class.simpleName!!)
        val attachers = ids.withIndex().filter { it.value.startsWith(AttachOnOperationListener::class.simpleName!!) }.map { it.index }
        assertTrue(attachers.all { it < step }, "attach listeners $attachers must precede the step listener at $step: $ids")
    }

    @Test
    fun `applying twice registers each listener once`() {
        UltronAllureConfig.applyRecommended()
        UltronAllureConfig.applyRecommended()

        assertEquals(3, UltronCommonConfig.getListeners().size)
    }

    @Test
    fun `detailedAllureReport false drops the step listener`() {
        UltronAllureConfig.apply { detailedAllureReport = false }
        assertTrue(UltronCommonConfig.getListeners().none { it.id == AllureOperationStepListener::class.simpleName })
    }

    @Test
    fun `NONE policies drop the attach listeners`() {
        UltronAllureConfig.apply {
            addScreenshotPolicy = mutableSetOf(AllureAttachStrategy.NONE)
            addHierarchyPolicy = mutableSetOf(AllureAttachStrategy.NONE)
        }
        assertTrue(UltronCommonConfig.getListeners().none { it.id.startsWith(AttachOnOperationListener::class.simpleName!!) })
    }

    @Test
    fun `apply reflects Skiko-only and common fields in params`() {
        UltronAllureConfig.apply {
            resultsDirectory = "custom-results"
            videoPolicy = mutableSetOf(AllureAttachStrategy.TEST_FINISH)
            attachUltronLog = false
        }
        assertEquals("custom-results", UltronAllureConfig.params.resultsDirectory)
        assertEquals(setOf(AllureAttachStrategy.TEST_FINISH), UltronAllureConfig.params.videoPolicy)
        assertEquals(false, UltronAllureConfig.params.attachUltronLog)
        assertNotNull(UltronAllureConfig.videoAttacher)
    }

    @Test
    fun `video format and quality default to AUTO and MEDIUM, show in toString and reach the attacher`() {
        val defaults = AllureConfigParams()
        assertEquals(VideoFormat.AUTO, defaults.videoFormat)
        assertEquals(VideoQuality.MEDIUM, defaults.videoQuality)
        assertTrue(defaults.toString().contains("videoFormat=AUTO, videoQuality=MEDIUM"), defaults.toString())

        UltronAllureConfig.apply {
            videoPolicy = mutableSetOf(AllureAttachStrategy.TEST_FINISH)
            videoFormat = VideoFormat.APNG
            videoQuality = VideoQuality.HIGH
        }
        val attacher = assertNotNull(UltronAllureConfig.videoAttacher)
        assertEquals(VideoFormat.APNG, attacher.format)
        assertEquals(VideoQuality.HIGH, attacher.quality)
    }

    @Test
    fun `applyRecommended resets params to the defaults`() {
        UltronAllureConfig.apply { detailedAllureReport = false }
        UltronAllureConfig.applyRecommended()
        assertTrue(UltronAllureConfig.params.detailedAllureReport)
        assertNull(UltronAllureConfig.videoAttacher)
    }
}

@OptIn(ExperimentalUltronApi::class)
class UltronAllureConfigLifecycleTest {
    /** Two `test { }` calls on two threads: A is abandoned while B runs, and logs late. */
    class LateLogScenario : UltronTest() {
        fun runLate(started: CountDownLatch, release: CountDownLatch) = test(name = "late log: A") {
            go {
                started.countDown()
                release.await(10, TimeUnit.SECONDS)
                UltronLog.info("late line of A")
            }
        }

        fun runNext(release: CountDownLatch, late: Thread) = test(name = "late log: B") {
            go {
                release.countDown()
                late.join(10_000)
            }
        }
    }

    @AfterTest
    fun cleanup() {
        UltronAllureConfig.applyRecommended()
        UltronCommonConfig.clearListeners()
        UltronTestRunHooks.clear()
    }

    @Test
    fun `apply keeps the lifecycle and its open test and redirects the results of the next test`() {
        val first = createTempDirectory("allure-first").toFile()
        val second = createTempDirectory("allure-second").toFile()
        UltronAllureConfig.apply { resultsDirectory = first.path }
        val lifecycle = UltronAllure.lifecycle
        lifecycle.startTest("t")

        UltronAllureConfig.apply { resultsDirectory = second.path }

        assertSame(lifecycle, UltronAllure.lifecycle)
        assertEquals("t", UltronAllure.lifecycle.currentTest?.name)
        UltronAllure.lifecycle.stopTest(null)
        // The open test stays where it started, next to the attachments it already wrote there.
        assertEquals(1, first.listFiles()?.count { it.name.endsWith("-result.json") }, first.list()?.toList().toString())
        assertTrue(second.listFiles().isNullOrEmpty(), "no result in the second directory yet: ${second.list()?.toList()}")
        UltronAllure.lifecycle.startTest("next")
        UltronAllure.lifecycle.stopTest(null)
        assertEquals(1, second.listFiles()?.count { it.name.endsWith("-result.json") }, second.list()?.toList().toString())
    }

    /** The last implicit test of a run is written by the process exit handler: with the Ultron log it collected. */
    @Test
    fun `the test still open at process exit gets its Ultron log`() {
        val dir = createTempDirectory("allure-exit").toFile()
        UltronAllureConfig.apply { resultsDirectory = dir.path }
        UltronAllure.lifecycle.startStep("operation without a test")
        UltronLog.info("logged in the implicit test")
        UltronAllure.lifecycle.stopStep()

        UltronAllure.closeAtExit()

        val result = AllureJson.decodeResult(dir.listFiles()!!.single { it.name.endsWith("-result.json") }.readText())
        val log = result.attachments.single { it.name == "ultron_log.txt" }
        assertTrue("logged in the implicit test" in dir.resolve(log.source).readText())
    }

    /**
     * `UltronAllure.lifecycle` is public and may be replaced: the log buffer asks the lifecycle in place now whether a
     * line comes from a late `test { }`, not the one in place when the config object was created.
     */
    @Test
    fun `the log buffer asks the lifecycle UltronAllure has now`() {
        val saved = UltronAllure.lifecycle
        val dir = createTempDirectory("allure-replaced").toFile()
        try {
            UltronAllureConfig.applyRecommended() // the config object, and its log buffer, exist before the replacement
            UltronAllure.lifecycle = AllureLifecycle(AllureResultsWriter(platformResultsSink(dir.path)))
            UltronAllureConfig.apply { resultsDirectory = dir.path }
            val scenario = LateLogScenario()
            val aStarted = CountDownLatch(1)
            val releaseA = CountDownLatch(1)
            val a = thread(name = "late test") { runCatching { scenario.runLate(aStarted, releaseA) } }
            assertTrue(aStarted.await(10, TimeUnit.SECONDS), "precondition: A is running")
            scenario.runNext(releaseA, a)

            val b = dir.listFiles()!!.filter { it.name.endsWith("-result.json") }
                .map { AllureJson.decodeResult(it.readText()) }
                .single { it.name == "late log: B" }
            val log = b.attachments.singleOrNull { it.name == "ultron_log.txt" }?.let { dir.resolve(it.source).readText() }.orEmpty()
            assertTrue("late line of A" !in log, "A's late line is not in B's log:\n$log")
        } finally {
            UltronAllure.lifecycle = saved
            UltronTestListeners.clear()
        }
    }
}
