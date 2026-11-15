package com.atiurin.ultron.allure.video

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class WebmMuxerTest {
    private val chunks = listOf(
        WebmChunk(byteArrayOf(1, 1), 0, keyFrame = true),
        WebmChunk(byteArrayOf(2, 2, 2), 500, keyFrame = false),
        WebmChunk(byteArrayOf(3), 1400, keyFrame = false),
    )

    @Test
    fun `three VP9 chunks become a webm with one track and three blocks at their timecodes`() {
        val bytes = WebmMuxer.mux("V_VP9", 376, 668, 2400, chunks)
        val top = EbmlTestReader.parse(bytes)

        val header = top.single { it.id == 0x1A45DFA3L }
        assertEquals("webm", bytes.decodeToString(EbmlTestReader.children(header, 0x4282).single().start, EbmlTestReader.children(header, 0x4282).single().end))

        val segment = top.single { it.id == 0x18538067L }
        val track = EbmlTestReader.children(EbmlTestReader.children(segment, 0x1654AE6B).single(), 0xAE).single()
        val codec = EbmlTestReader.children(track, 0x86).single()
        assertEquals("V_VP9", bytes.decodeToString(codec.start, codec.end))
        val video = EbmlTestReader.children(track, 0xE0).single()
        assertEquals(376, EbmlTestReader.uint(bytes, EbmlTestReader.children(video, 0xB0).single()))
        assertEquals(668, EbmlTestReader.uint(bytes, EbmlTestReader.children(video, 0xBA).single()))

        val blocks = EbmlTestReader.children(segment, 0x1F43B675).flatMap { cluster ->
            val clusterTime = EbmlTestReader.uint(bytes, EbmlTestReader.children(cluster, 0xE7).single())
            EbmlTestReader.children(cluster, 0xA3).map { block ->
                val relative = (bytes[block.start + 1].toInt() shl 8) or (bytes[block.start + 2].toInt() and 0xFF)
                Triple(clusterTime + relative, bytes[block.start + 3].toInt() and 0x80 != 0, bytes.copyOfRange(block.start + 4, block.end))
            }
        }
        assertEquals(listOf(0L, 500L, 1400L), blocks.map { it.first })
        assertEquals(listOf(true, false, false), blocks.map { it.second })
        assertContentEquals(byteArrayOf(2, 2, 2), blocks[1].third)
    }

    @Test
    fun `a long recording starts a new cluster so relative timecodes fit`() {
        val long = listOf(
            WebmChunk(byteArrayOf(1), 0, true),
            WebmChunk(byteArrayOf(2), 31_000, true),
            WebmChunk(byteArrayOf(3), 70_000, true),
        )
        val bytes = WebmMuxer.mux("V_VP8", 2, 2, 71_000, long)
        val segment = EbmlTestReader.parse(bytes).single { it.id == 0x18538067L }
        val clusters = EbmlTestReader.children(segment, 0x1F43B675)
        assertEquals(listOf(0L, 31_000L, 70_000L), clusters.map { EbmlTestReader.uint(bytes, EbmlTestReader.children(it, 0xE7).single()) })
    }
}
