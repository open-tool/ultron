package com.atiurin.ultron.allure.video

/** One encoded VP8/VP9 frame with its presentation time. */
class WebmChunk(val data: ByteArray, val ptsMs: Long, val keyFrame: Boolean)

/**
 * Minimal WebM (EBML/Matroska) writer for one video track, in memory. The fallback of the browser encoder for
 * Chromium builds without H.264; see [Mp4Muxer] for why this is written here. Timecodes are in milliseconds
 * (`TimecodeScale` 1 000 000 ns), a new cluster starts at least every 30 s so a block's relative time fits `int16`.
 */
object WebmMuxer {
    private const val CLUSTER_SPAN_MS = 30_000L

    fun mux(codecId: String, width: Int, height: Int, durationMs: Long, chunks: List<WebmChunk>): ByteArray {
        require(chunks.isNotEmpty()) { "webm needs at least one chunk" }
        val header = element(
            0x1A45DFA3,
            element(0x4286, uint(1)), element(0x42F7, uint(1)), element(0x42F2, uint(4)), element(0x42F3, uint(8)),
            element(0x4282, "webm".encodeToByteArray()), element(0x4287, uint(4)), element(0x4285, uint(2)),
        )
        val info = element(
            0x1549A966,
            element(0x2AD7B1, uint(1_000_000)), element(0x4D80, "ultron-allure".encodeToByteArray()),
            element(0x5741, "ultron-allure".encodeToByteArray()), element(0x4489, float64(durationMs.toDouble())),
        )
        val tracks = element(
            0x1654AE6B,
            element(
                0xAE,
                element(0xD7, uint(1)), element(0x73C5, uint(1)), element(0x83, uint(1)), element(0x86, codecId.encodeToByteArray()),
                element(0xE0, element(0xB0, uint(width.toLong())), element(0xBA, uint(height.toLong()))),
            ),
        )
        return Mp4Muxer.concat(header, element(0x18538067, info, tracks, *clusters(chunks).toTypedArray()))
    }

    private fun clusters(chunks: List<WebmChunk>): List<ByteArray> {
        val out = ArrayList<ByteArray>()
        var start = chunks.first().ptsMs
        var blocks = ArrayList<ByteArray>()
        fun close() {
            if (blocks.isNotEmpty()) out += element(0x1F43B675, element(0xE7, uint(start)), *blocks.toTypedArray())
            blocks = ArrayList()
        }
        for (chunk in chunks) {
            if (chunk.ptsMs - start >= CLUSTER_SPAN_MS) { close(); start = chunk.ptsMs }
            val relative = (chunk.ptsMs - start).toInt()
            val flags = if (chunk.keyFrame) 0x80 else 0
            blocks += element(0xA3, byteArrayOf(0x81.toByte(), (relative shr 8).toByte(), relative.toByte(), flags.toByte()), chunk.data)
        }
        close()
        return out
    }

    private fun element(id: Long, vararg parts: ByteArray): ByteArray {
        val size = parts.sumOf { it.size }
        return Mp4Muxer.concat(idBytes(id), sizeBytes(size.toLong()), *parts)
    }

    private fun element(id: Int, vararg parts: ByteArray) = element(id.toLong() and 0xFFFFFFFFL, *parts)

    private fun idBytes(id: Long): ByteArray {
        val length = when {
            id > 0xFFFFFF -> 4
            id > 0xFFFF -> 3
            id > 0xFF -> 2
            else -> 1
        }
        return ByteArray(length) { (id shr (8 * (length - 1 - it))).toByte() }
    }

    /** EBML variable length size: the leading one bit of the first byte gives the length. */
    private fun sizeBytes(size: Long): ByteArray {
        var length = 1
        while (length < 8 && size >= (1L shl (7 * length)) - 1) length++
        val marked = size or (1L shl (7 * length))
        return ByteArray(length) { (marked shr (8 * (length - 1 - it))).toByte() }
    }

    private fun uint(value: Long): ByteArray {
        var length = 1
        while (length < 8 && value >= (1L shl (8 * length))) length++
        return ByteArray(length) { (value shr (8 * (length - 1 - it))).toByte() }
    }

    private fun uint(value: Int) = uint(value.toLong())

    private fun float64(value: Double): ByteArray {
        val bits = value.toRawBits()
        return ByteArray(8) { (bits shr (8 * (7 - it))).toByte() }
    }
}
