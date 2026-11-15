package com.atiurin.ultron.allure.video

import com.atiurin.ultron.allure.lifecycle.AllureLifecycle

/**
 * Keeps the encoded frames of the current test. Frames are encoded by the caller, so memory is bounded
 * by [Limits.maxBytes] rather than by raw bitmaps; identical consecutive frames are merged and the
 * oldest frames go first when a limit is exceeded.
 */
class FrameRecorder(
    private val limits: Limits = Limits(),
    private val clock: () -> Long = AllureLifecycle.defaultClock,
) {
    data class Limits(
        val maxFrames: Int = 200,
        val maxBytes: Long = 20L * 1024 * 1024,
        /** Longest gap between two frames shown in the animation: a 10 s wait is not a 10 s frame. */
        val maxFrameDurationMs: Int = 3000,
        val lastFrameDurationMs: Int = 1000,
        /** Operations run in milliseconds; a frame needs to stay long enough to be seen. */
        val minFrameDurationMs: Int = 500,
        /**
         * While an operation retries (waits for the UI), a frame is captured at most this often, so a long
         * wait shows its intermediate states instead of one frame. `0` disables frames between retries.
         * Each capture costs a screenshot and a PNG encode on the test thread.
         */
        val retryFrameIntervalMs: Int = 500,
    )

    private class Entry(val bytes: ByteArray, val label: String, val at: Long)

    private val entries = ArrayDeque<Entry>()
    private var totalBytes = 0L
    private var recording = false
    private var lastCaptureAt = Long.MIN_VALUE

    /** Frames dropped because of [Limits.maxFrames] / [Limits.maxBytes] since [start]. */
    var dropped: Int = 0
        private set

    val isRecording: Boolean get() = recording
    val size: Int get() = entries.size

    fun start() {
        reset()
        recording = true
    }

    /** Stops recording and releases the frames: read [frames] and [dropped] before calling it. */
    fun stop() {
        recording = false
        reset()
    }

    private fun reset() {
        entries.clear()
        totalBytes = 0
        dropped = 0
        lastCaptureAt = Long.MIN_VALUE
    }

    fun add(bytes: ByteArray, label: String) {
        if (!recording) return
        lastCaptureAt = clock()
        if (bytes.size > limits.maxBytes) { dropped++; return }
        entries.lastOrNull()?.let { if (it.bytes.contentEquals(bytes)) return }
        entries.addLast(Entry(bytes, label, clock()))
        totalBytes += bytes.size
        while (entries.size > limits.maxFrames || totalBytes > limits.maxBytes) {
            val removed = entries.removeFirst()
            totalBytes -= removed.bytes.size
            dropped++
        }
    }

    /** True when recording and [Limits.retryFrameIntervalMs] has passed since the last captured frame. */
    fun isRetryFrameDue(): Boolean {
        if (!recording || limits.retryFrameIntervalMs <= 0) return false
        return lastCaptureAt == Long.MIN_VALUE || clock() - lastCaptureAt >= limits.retryFrameIntervalMs
    }

    fun frames(): List<ApngFrame> = entries.mapIndexed { index, entry ->
        val delay = if (index + 1 < entries.size) (entries[index + 1].at - entry.at).toInt() else limits.lastFrameDurationMs
        ApngFrame(entry.bytes, delay.coerceIn(limits.minFrameDurationMs, limits.maxFrameDurationMs), entry.label)
    }
}
