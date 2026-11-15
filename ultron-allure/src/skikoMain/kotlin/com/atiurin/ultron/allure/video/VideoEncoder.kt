package com.atiurin.ultron.allure.video

/** Turns recorded frames into one video attachment. */
interface VideoEncoder {
    /**
     * Encodes [frames]; throws when it cannot. The encoder picks the container (mp4, or webm in a browser
     * without H.264), the caller falls back to APNG on any failure. `suspend` only because browser codecs
     * are Promise based: JVM and native encoders never suspend.
     */
    suspend fun encode(frames: List<ApngFrame>, options: VideoOptions): EncodedVideo
}

class EncodedVideo(val bytes: ByteArray, val extension: String, val mimeType: String)

class VideoOptions(val quality: VideoQuality)

enum class VideoQuality { LOW, MEDIUM, HIGH }

enum class VideoFormat {
    /** mp4 (H.264) where the target has an encoder, APNG otherwise. */
    AUTO,

    /**
     * Same as [AUTO] today: asks for mp4 but still falls back to APNG when the encoder is missing or fails,
     * because a report never changes the outcome of a test. Not a guarantee that the attachment is mp4.
     */
    MP4,

    /** Animated PNG, the format before mp4 support. */
    APNG,
}

/** The encoder of the target; `null` when mp4 is not available there (no encoder in the runtime). */
internal expect fun platformVideoEncoder(): VideoEncoder?
