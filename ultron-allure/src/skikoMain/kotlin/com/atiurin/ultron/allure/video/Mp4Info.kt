package com.atiurin.ultron.allure.video

/** What the first video track of an mp4 declares; read-only facts for tests and tooling, like [ApngInfo]. */
class Mp4Details(
    /** Types of the top-level boxes in file order. */
    val topLevel: List<String>,
    val trackCount: Int,
    /** Sample entry type of the first track: `avc1` for H.264. */
    val codec: String?,
    val width: Int,
    val height: Int,
    val timescale: Int,
    /** Duration of every sample in timescale ticks, `stts` expanded. */
    val sampleDurations: List<Int>,
    val sampleSizes: List<Int>,
    /** 1-based numbers of the sync samples (`stss`). */
    val syncSamples: List<Int>,
    val avcC: ByteArray?,
) {
    val sampleCount: Int get() = sampleSizes.size
    val totalDuration: Long get() = sampleDurations.sumOf { it.toLong() }
}

object Mp4Info {
    private class Box(val type: String, val start: Int, val end: Int)

    fun isMp4(bytes: ByteArray): Boolean = bytes.size >= 12 && bytes.decodeToString(4, 8) == "ftyp"

    /** `null` when [bytes] is not an mp4 with a video track. */
    fun parse(bytes: ByteArray): Mp4Details? {
        if (!isMp4(bytes)) return null
        val top = boxes(bytes, 0, bytes.size)
        val moov = top.firstOrNull { it.type == "moov" } ?: return null
        val traks = boxes(bytes, moov.start, moov.end).filter { it.type == "trak" }
        val trak = traks.firstOrNull() ?: return null
        val mdia = child(bytes, trak, "mdia") ?: return null
        val mdhd = child(bytes, mdia, "mdhd") ?: return null
        val stbl = child(bytes, child(bytes, mdia, "minf") ?: return null, "stbl") ?: return null
        val timescale = int(bytes, mdhd.start + 12)

        val stsd = child(bytes, stbl, "stsd") ?: return null
        val entry = stsd.start + 8 // version/flags + entry count
        val codec = bytes.decodeToString(entry + 4, entry + 8)
        val avcC = boxes(bytes, entry + 86, stsd.end).firstOrNull { it.type == "avcC" }?.let { bytes.copyOfRange(it.start, it.end) }

        val stts = child(bytes, stbl, "stts")?.let { box ->
            val count = int(bytes, box.start + 4)
            buildList {
                for (i in 0 until count) repeat(int(bytes, box.start + 8 + i * 8)) { add(int(bytes, box.start + 12 + i * 8)) }
            }
        } ?: emptyList()
        val stsz = child(bytes, stbl, "stsz")?.let { box ->
            val count = int(bytes, box.start + 8)
            List(count) { int(bytes, box.start + 12 + it * 4) }
        } ?: emptyList()
        val stss = child(bytes, stbl, "stss")?.let { box ->
            List(int(bytes, box.start + 4)) { int(bytes, box.start + 8 + it * 4) }
        } ?: emptyList()

        return Mp4Details(
            topLevel = top.map { it.type }, trackCount = traks.size, codec = codec,
            width = short(bytes, entry + 8 + 24), height = short(bytes, entry + 8 + 26),
            timescale = timescale, sampleDurations = stts, sampleSizes = stsz, syncSamples = stss, avcC = avcC,
        )
    }

    private fun child(bytes: ByteArray, parent: Box, type: String): Box? = boxes(bytes, parent.start, parent.end).firstOrNull { it.type == type }

    /**
     * Boxes between [from] and [to]; `start` of a box is the first byte after its header. Size `1` means a 64 bit
     * size after the type (JCodec writes `mdat` so), size `0` means "to the end of the file".
     */
    private fun boxes(bytes: ByteArray, from: Int, to: Int): List<Box> {
        val out = ArrayList<Box>()
        var at = from
        while (at + 8 <= to) {
            val size32 = int(bytes, at)
            val header = if (size32 == 1) 16 else 8
            if (at + header > to) break
            val size = when (size32) {
                0 -> (to - at).toLong()
                1 -> (int(bytes, at + 8).toLong() shl 32) or (int(bytes, at + 12).toLong() and 0xFFFFFFFFL)
                else -> size32.toLong() and 0xFFFFFFFFL
            }
            if (size < header || at + size > to) break
            out += Box(bytes.decodeToString(at + 4, at + 8), at + header, at + size.toInt())
            at += size.toInt()
        }
        return out
    }

    private fun int(bytes: ByteArray, at: Int): Int =
        (bytes[at].toInt() and 0xFF shl 24) or (bytes[at + 1].toInt() and 0xFF shl 16) or (bytes[at + 2].toInt() and 0xFF shl 8) or (bytes[at + 3].toInt() and 0xFF)

    private fun short(bytes: ByteArray, at: Int): Int = (bytes[at].toInt() and 0xFF shl 8) or (bytes[at + 1].toInt() and 0xFF)
}
