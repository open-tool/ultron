package com.atiurin.ultron.allure.video

/** Turns recorded frames into one animated attachment. */
@Deprecated("Use VideoEncoder")
interface AnimationEncoder {
    val extension: String
    val mimeType: String
    fun encode(frames: List<ApngFrame>): ByteArray
}

/**
 * Plugs an encoder written against [AnimationEncoder] into [VideoAttacher], whose encoder parameter is a
 * [VideoEncoder] now: `VideoAttacher(lifecycle, recorder, provider, myEncoder.asVideoEncoder(), policy)`.
 * With `videoFormat = AUTO` or `MP4` it is used as the platform encoder, and a failure falls back to APNG.
 */
@Suppress("DEPRECATION")
fun AnimationEncoder.asVideoEncoder(): VideoEncoder = this as? VideoEncoder ?: AnimationVideoEncoder(this)

@Suppress("DEPRECATION")
private class AnimationVideoEncoder(private val encoder: AnimationEncoder) : VideoEncoder {
    override suspend fun encode(frames: List<ApngFrame>, options: VideoOptions): EncodedVideo =
        EncodedVideo(encoder.encode(frames), encoder.extension, encoder.mimeType)
}

/**
 * Animated PNG, attached as `image/png`: every Allure 2 version and Allure 3 render it with `<img>`,
 * and browsers animate APNG natively. See `docs/superpowers/specs/2026-10-29-test-video-recording-feasibility.md`.
 * Both interfaces stay: [AnimationEncoder] for consumers that call it directly, [VideoEncoder] for the attacher;
 * an own [AnimationEncoder] reaches the attacher through [asVideoEncoder].
 */
@Suppress("DEPRECATION")
object ApngEncoder : AnimationEncoder, VideoEncoder {
    override val extension = "png"
    override val mimeType = "image/png"
    override fun encode(frames: List<ApngFrame>): ByteArray = ApngAssembler.assemble(frames)
    override suspend fun encode(frames: List<ApngFrame>, options: VideoOptions): EncodedVideo =
        EncodedVideo(encode(frames), extension, mimeType)
}
