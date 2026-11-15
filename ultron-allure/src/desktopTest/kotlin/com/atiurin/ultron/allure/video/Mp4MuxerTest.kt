package com.atiurin.ultron.allure.video

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class Mp4MuxerTest {
    private val avcC = byteArrayOf(1, 0x42, 0, 0x1E, 0xFF.toByte(), 0xE1.toByte(), 0, 2, 0x67, 0x42, 1, 0, 1, 0x68)
    private val samples = listOf(
        Mp4Sample(ByteArray(10) { 1 }, 500, keyFrame = true),
        Mp4Sample(ByteArray(7) { 2 }, 900, keyFrame = false),
        Mp4Sample(ByteArray(4) { 3 }, 1000, keyFrame = false),
    )

    @Test
    fun `three samples give ftyp then moov then mdat with the declared tables`() {
        val bytes = Mp4Muxer.mux(avcC, 376, 668, samples)

        assertTrue(Mp4Info.isMp4(bytes))
        val info = assertNotNull(Mp4Info.parse(bytes))
        assertEquals(listOf("ftyp", "moov", "mdat"), info.topLevel)
        assertEquals(1, info.trackCount)
        assertEquals("avc1", info.codec)
        assertEquals(376 to 668, info.width to info.height)
        assertEquals(1000, info.timescale)
        assertContentEquals(avcC, info.avcC)
        assertEquals(listOf(10, 7, 4), info.sampleSizes)
        assertEquals(listOf(500, 900, 1000), info.sampleDurations)
        assertEquals(listOf(1), info.syncSamples)
        assertEquals(2400L, info.totalDuration)
    }

    @Test
    fun `the chunk offset points at the first sample inside mdat`() {
        val bytes = Mp4Muxer.mux(avcC, 2, 2, samples)
        val mdatAt = bytes.size - samples.sumOf { it.data.size }
        // stco is the last box of moov: version/flags, count, offset.
        val stco = String(bytes, Charsets.ISO_8859_1).indexOf("stco")
        val offset = ((bytes[stco + 12].toInt() and 0xFF) shl 24) or ((bytes[stco + 13].toInt() and 0xFF) shl 16) or
            ((bytes[stco + 14].toInt() and 0xFF) shl 8) or (bytes[stco + 15].toInt() and 0xFF)
        assertEquals(mdatAt, offset)
        assertEquals(1, bytes[offset].toInt())
    }

    @Test
    fun `equal consecutive durations share a stts entry and the info expands them`() {
        val same = List(4) { Mp4Sample(ByteArray(3), 500, keyFrame = it == 0) }
        val info = assertNotNull(Mp4Info.parse(Mp4Muxer.mux(avcC, 2, 2, same)))
        assertEquals(List(4) { 500 }, info.sampleDurations)
    }

    @Test
    fun `non mp4 bytes are not parsed`() {
        assertNull(Mp4Info.parse(ByteArray(40)))
        assertTrue(!Mp4Info.isMp4(ByteArray(3)))
    }
}
