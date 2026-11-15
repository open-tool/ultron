package com.atiurin.ultron.allure.video

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JcodecMp4EncoderTest {
    /** Noisy enough that the quantiser changes the size, deterministic so the tests do not flake. */
    private fun png(width: Int, height: Int, seed: Int): ByteArray {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        for (x in 0 until width) for (y in 0 until height) {
            val v = (x * 7 + y * 13 + seed * 50 + (x * y) % 29) and 0xFF
            image.setRGB(x, y, (0xFF shl 24) or (v shl 16) or ((255 - v) shl 8) or (seed * 40 and 0xFF))
        }
        return ByteArrayOutputStream().also { ImageIO.write(image, "png", it) }.toByteArray()
    }

    private fun encode(frames: List<ApngFrame>, quality: VideoQuality = VideoQuality.MEDIUM): ByteArray {
        // The JVM encoder never suspends, so the coroutine is complete when startCoroutine returns.
        var outcome: Result<EncodedVideo>? = null
        suspend { JcodecMp4Encoder().encode(frames, VideoOptions(quality)) }
            .startCoroutine(Continuation(EmptyCoroutineContext) { outcome = it })
        val result = assertNotNull(outcome).getOrThrow()
        assertEquals("mp4", result.extension)
        assertEquals("video/mp4", result.mimeType)
        return result.bytes
    }

    @Test
    fun `four frames of the portrait scene become four samples with the recorded durations`() {
        val delays = listOf(500, 900, 1300, 1000)
        val bytes = encode(delays.mapIndexed { i, delay -> ApngFrame(png(375, 667, i), delay) })

        assertTrue(Mp4Info.isMp4(bytes))
        val info = assertNotNull(Mp4Info.parse(bytes))
        assertEquals("ftyp", info.topLevel.first())
        assertEquals(1, info.trackCount)
        assertEquals("avc1", info.codec)
        assertEquals(4, info.sampleCount)
        assertEquals(delays, info.sampleDurations)
        assertEquals(1000, info.timescale)
        assertEquals(376 to 668, info.width to info.height)
        assertEquals(1, info.syncSamples.first())
        assertAvcPayload(bytes, info)
    }

    @Test
    fun `samples are length-prefixed NAL units and the decoder config sits in avcC`() {
        val bytes = encode(List(3) { ApngFrame(png(48, 40, it), 700) })
        assertAvcPayload(bytes, assertNotNull(Mp4Info.parse(bytes)))
    }

    /**
     * A player needs `avcC` (version 1 with SPS and PPS) and AVCC samples: a 4-byte length before every NAL unit.
     * An Annex B sample, or one converted twice, does not tile the `mdat` payload with valid NAL headers.
     */
    private fun assertAvcPayload(bytes: ByteArray, info: Mp4Details) {
        val avcC = assertNotNull(info.avcC, "avcC is missing")
        assertEquals(1, avcC[0].toInt(), "avcC version")
        assertEquals(3, avcC[4].toInt() and 0x03, "NAL length size must be 4")
        assertTrue((avcC[5].toInt() and 0x1F) >= 1, "avcC has no SPS")

        val (start, end) = mdatPayload(bytes)
        assertEquals(info.sampleSizes.sum(), end - start, "mdat payload is the samples")
        var at = start
        var nals = 0
        val types = mutableSetOf<Int>()
        while (at < end) {
            val length = int(bytes, at)
            // An Annex B start code 00 00 00 01 would read as length 1.
            assertTrue(length in 2..(end - at - 4),"NAL length $length at ${at - start} runs past mdat")
            val header = bytes[at + 4].toInt() and 0xFF
            assertEquals(0, header and 0x80, "forbidden_zero_bit at ${at - start}")
            types += header and 0x1F
            at += 4 + length
            nals++
        }
        assertTrue(nals >= info.sampleCount)
        assertTrue(5 in types, "no IDR slice among NAL types $types")
        assertTrue(types.all { it in 1..5 }, "only slice NAL units belong in samples, got $types")
    }

    private fun mdatPayload(bytes: ByteArray): Pair<Int, Int> {
        var at = 0
        while (at + 8 <= bytes.size) {
            val size32 = int(bytes, at)
            val type = bytes.decodeToString(at + 4, at + 8)
            val header = if (size32 == 1) 16 else 8
            val size = when (size32) {
                1 -> ((int(bytes, at + 8).toLong() shl 32) or (int(bytes, at + 12).toLong() and 0xFFFFFFFFL)).toInt()
                0 -> bytes.size - at
                else -> size32
            }
            if (type == "mdat") return (at + header) to (at + size)
            at += size
        }
        error("no mdat")
    }

    private fun int(bytes: ByteArray, at: Int): Int =
        (bytes[at].toInt() and 0xFF shl 24) or (bytes[at + 1].toInt() and 0xFF shl 16) or
            (bytes[at + 2].toInt() and 0xFF shl 8) or (bytes[at + 3].toInt() and 0xFF)

    @Test
    fun `identical frames merged by the recorder give a single sample`() {
        val recorder = FrameRecorder()
        recorder.start()
        val same = png(64, 48, 1)
        recorder.add(same, "a")
        recorder.add(same.copyOf(), "b")
        assertEquals(1, recorder.size)

        val info = assertNotNull(Mp4Info.parse(encode(listOf(ApngFrame(same, 1000)))))
        assertEquals(1, info.sampleCount)
        assertEquals(listOf(1000), info.sampleDurations)
    }

    @Test
    fun `a key frame comes every ten frames`() {
        val bytes = encode(List(12) { ApngFrame(png(32, 32, it), 500) })
        assertEquals(listOf(1, 11), assertNotNull(Mp4Info.parse(bytes)).syncSamples)
    }

    @Test
    fun `lower quality gives a smaller file`() {
        val frames = List(2) { ApngFrame(png(128, 96, it), 500) }
        val low = encode(frames, VideoQuality.LOW).size
        val high = encode(frames, VideoQuality.HIGH).size
        assertTrue(low < high, "LOW=$low must be smaller than HIGH=$high")
    }
}
