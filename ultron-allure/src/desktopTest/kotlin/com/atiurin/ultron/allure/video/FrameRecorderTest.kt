package com.atiurin.ultron.allure.video

import com.atiurin.ultron.allure.testutil.ManualClock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FrameRecorderTest {
    private val clock = ManualClock(now = 0)
    private fun recorder(maxFrames: Int = 100, maxBytes: Long = 1_000_000, maxFrameMs: Int = 3000, lastFrameMs: Int = 1000, minFrameMs: Int = 0, retryIntervalMs: Int = 500) =
        FrameRecorder(FrameRecorder.Limits(maxFrames, maxBytes, maxFrameMs, lastFrameMs, minFrameMs, retryIntervalMs)) { clock.now }

    @Test
    fun `a retry frame is due only after the retry interval since the last capture`() {
        val r = recorder(retryIntervalMs = 500)
        r.start()
        assertTrue(r.isRetryFrameDue(), "nothing captured yet")
        r.add(bytes(1), "a")
        assertTrue(!r.isRetryFrameDue())
        clock.tick(499)
        assertTrue(!r.isRetryFrameDue())
        clock.tick(1)
        assertTrue(r.isRetryFrameDue())
    }

    @Test
    fun `a merged identical frame still counts as a capture for the retry interval`() {
        val r = recorder(retryIntervalMs = 500)
        r.start()
        r.add(bytes(1), "a"); clock.tick(500)
        r.add(bytes(1), "same")
        assertEquals(1, r.frames().size)
        assertTrue(!r.isRetryFrameDue(), "the screen was just inspected, do not encode it again at once")
    }

    @Test
    fun `retry frames are disabled by a non-positive interval and while not recording`() {
        val r = recorder(retryIntervalMs = 0)
        r.start()
        assertTrue(!r.isRetryFrameDue())
        val stopped = recorder(retryIntervalMs = 500)
        assertTrue(!stopped.isRetryFrameDue())
    }

    @Test
    fun `delay is raised to the minimum frame duration so fast operations stay visible`() {
        val r = recorder(minFrameMs = 500)
        r.start()
        r.add(bytes(1), "a"); clock.tick(40)
        r.add(bytes(2), "b")
        assertEquals(listOf(500, 1000), r.frames().map { it.delayMs })
    }

    private fun bytes(vararg b: Int) = ByteArray(b.size) { b[it].toByte() }

    @Test
    fun `frame delay is the time until the next frame and the last frame gets the configured delay`() {
        val r = recorder()
        r.start()
        r.add(bytes(1), "a"); clock.tick(120)
        r.add(bytes(2), "b"); clock.tick(80)
        r.add(bytes(3), "c")

        assertEquals(listOf(120, 80, 1000), r.frames().map { it.delayMs })
        assertEquals(listOf("a", "b", "c"), r.frames().map { it.label })
    }

    @Test
    fun `identical consecutive frames are merged and extend the previous delay`() {
        val r = recorder()
        r.start()
        r.add(bytes(1), "a"); clock.tick(100)
        r.add(bytes(1), "b"); clock.tick(100)
        r.add(bytes(2), "c")

        assertEquals(2, r.frames().size)
        assertEquals(200, r.frames()[0].delayMs)
        assertEquals("a", r.frames()[0].label)
    }

    @Test
    fun `delay is clamped to the maximum frame duration`() {
        val r = recorder(maxFrameMs = 500)
        r.start()
        r.add(bytes(1), "a"); clock.tick(10_000)
        r.add(bytes(2), "b")
        assertEquals(500, r.frames()[0].delayMs)
    }

    @Test
    fun `only the last maxFrames frames are kept`() {
        val r = recorder(maxFrames = 2)
        r.start()
        r.add(bytes(1), "a"); r.add(bytes(2), "b"); r.add(bytes(3), "c")
        assertEquals(listOf("b", "c"), r.frames().map { it.label })
        assertTrue(r.dropped > 0)
    }

    @Test
    fun `oldest frames are dropped when the byte budget is exceeded`() {
        val r = recorder(maxBytes = 5)
        r.start()
        r.add(bytes(1, 1, 1), "a"); r.add(bytes(2, 2, 2), "b")
        assertEquals(listOf("b"), r.frames().map { it.label })
    }

    @Test
    fun `a frame larger than the whole budget is not recorded`() {
        val r = recorder(maxBytes = 2)
        r.start()
        r.add(bytes(1, 1, 1), "a")
        assertTrue(r.frames().isEmpty())
    }

    @Test
    fun `start clears previous frames and nothing is recorded before start`() {
        val r = recorder()
        r.add(bytes(9), "ignored")
        assertTrue(r.frames().isEmpty())
        r.start()
        r.add(bytes(1), "a")
        r.start()
        assertTrue(r.frames().isEmpty())
    }
}
