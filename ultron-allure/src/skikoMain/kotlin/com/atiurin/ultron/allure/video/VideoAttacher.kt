package com.atiurin.ultron.allure.video

import com.atiurin.ultron.allure.attach.ArtifactProvider
import com.atiurin.ultron.allure.config.AllureAttachStrategy
import com.atiurin.ultron.allure.config.allows
import com.atiurin.ultron.allure.config.recordsTests
import com.atiurin.ultron.allure.lifecycle.AllureLifecycle
import com.atiurin.ultron.log.UltronLog
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

/**
 * Drives the per-test recording: starts the [FrameRecorder] with the test, adds the last frame while the
 * scene is still alive on failure, and attaches the encoded animation at test level when the policy says so.
 * Recording happens for every test as soon as the policy is not NONE; the policy decides what is attached.
 * One recording per Allure test: several `runUltronUiTest` blocks inside one `UltronTest.test { }` share it.
 * The recording belongs to the [AllureLifecycle.TestState] that was current when it started; frames are never
 * attached to another test, and a test that never entered `runUltronUiTest` gets no video.
 *
 * Source-breaking since the mp4 video: the fourth parameter was `encoder: AnimationEncoder` and is the platform
 * [VideoEncoder] now. A call passing [ApngEncoder] compiles unchanged (it implements both interfaces); an own
 * [AnimationEncoder] is passed as `myEncoder.asVideoEncoder()`. No overload keeps the old signature: it would make
 * every call with [ApngEncoder] ambiguous.
 */
class VideoAttacher(
    private val lifecycle: AllureLifecycle,
    val recorder: FrameRecorder,
    private val frameProvider: ArtifactProvider,
    internal val platformEncoder: VideoEncoder?,
    private val policy: Set<AllureAttachStrategy>,
    internal val format: VideoFormat = VideoFormat.AUTO,
    internal val quality: VideoQuality = VideoQuality.MEDIUM,
) {
    val enabled: Boolean = policy.recordsTests()

    /** The test the running recording is for; `null` while nothing is recorded. */
    private var recordingFor: AllureLifecycle.TestState? = null

    /**
     * Starts recording for the current test. A recording already running for that very test (an earlier
     * `runUltronUiTest` block of the same `UltronTest`) goes on; one left behind by another test is dropped.
     */
    fun onTestStarted() {
        if (!enabled) return
        val current = lifecycle.currentTest ?: return
        if (recorder.isRecording && recordingFor === current) return
        if (recorder.isRecording) UltronLog.warn("Allure: video of test '${recordingFor?.name}' is dropped: the test was closed before its recording finished")
        recorder.start()
        recordingFor = current
    }

    /** Called inside the live scene: the last frame shows what the test saw when it failed. */
    fun onTestFailed() = captureFrame("test failure")

    /**
     * Adds a frame of the current scene now; a no-op when the test is not being recorded, or when called from a block
     * or `test { }` whose test is closed already (it finishes late, see `AllureLifecycle.isCurrentRunLate`).
     */
    fun captureFrame(label: String) {
        if (!enabled || !recorder.isRecording || recordingFor !== lifecycle.currentTest) return
        if (lifecycle.isCurrentRunLate()) return
        runCatching { frameProvider.produce()?.let { recorder.add(it, label) } }
            .onFailure { UltronLog.error("Allure: video frame '$label' capture failed: ${it.message}") }
    }

    /** Attaches the recording to the current test when it is the one recorded, then releases the frames. */
    fun onTestFinished(throwable: Throwable?) {
        if (!enabled) return
        val recorded = recordingFor
        val frames = recorder.frames()
        val dropped = recorder.dropped
        recorder.stop()
        recordingFor = null
        // Nothing to attach to (never open an implicit test for the video), or the frames are another test's.
        val test = lifecycle.currentTest ?: return
        if (test !== recorded) return
        val attach = policy.allows(AllureAttachStrategy.TEST_FINISH) ||
            (throwable != null && policy.allows(AllureAttachStrategy.TEST_FAILURE))
        if (!attach || frames.isEmpty()) return
        if (dropped > 0) UltronLog.warn("Allure: video kept the last ${frames.size} frames, $dropped dropped by limits")
        encodeAndAttach(test, frames)
    }

    private fun chooseEncoder(): VideoEncoder {
        if (format == VideoFormat.APNG) return ApngEncoder
        return platformEncoder ?: ApngEncoder.also {
            UltronLog.info("Allure: mp4 video is not available on this target, the video is attached as APNG")
        }
    }

    /**
     * The encoder runs through `startCoroutine`: JVM and native finish inside the call and the video is attached
     * at once, as before. A browser encoder suspends; the attachment is then reserved in the lifecycle and filled
     * in when the encoder resumes (single threaded there, so the callback cannot run before the reservation).
     * A failing encoder is replaced by APNG of the same frames; only a failing APNG leaves the test without video.
     */
    private fun encodeAndAttach(test: AllureLifecycle.TestState, frames: List<ApngFrame>) {
        val encoder = chooseEncoder()
        var starting = true
        var early: Result<EncodedVideo>? = null
        var pending: AllureLifecycle.PendingAttachment? = null
        suspend { encoder.encode(frames, VideoOptions(quality)) }.startCoroutine(Continuation(EmptyCoroutineContext) { result ->
            if (starting) early = result
            else pending?.let { deliver(it, result, frames) }
        })
        starting = false
        val done = early
        if (done != null) attachNow(done, frames) else pending = lifecycle.deferAttachment(test, ATTACHMENT_NAME, frames)
    }

    private fun attachNow(result: Result<EncodedVideo>, frames: List<ApngFrame>) {
        val video = result.getOrElse { failure -> fallback(failure, frames) } ?: return
        lifecycle.attachToTest(ATTACHMENT_NAME, video.bytes, video.extension, video.mimeType)
    }

    private fun deliver(pending: AllureLifecycle.PendingAttachment, result: Result<EncodedVideo>, frames: List<ApngFrame>) {
        val video = result.getOrElse { failure -> fallback(failure, frames) }
        if (video != null) pending.complete(video) else pending.abandon()
    }

    private fun fallback(failure: Throwable, frames: List<ApngFrame>): EncodedVideo? {
        UltronLog.warn("Allure: mp4 encoding failed (${failure.message}), the video is attached as APNG")
        return runCatching { ApngEncoder.encode(frames) }
            .onFailure { UltronLog.error("Allure: video encoding failed: ${it.message}") }
            .getOrNull()
            ?.let { EncodedVideo(it, ApngEncoder.extension, ApngEncoder.mimeType) }
    }

    companion object {
        const val ATTACHMENT_NAME = "video"
    }
}
