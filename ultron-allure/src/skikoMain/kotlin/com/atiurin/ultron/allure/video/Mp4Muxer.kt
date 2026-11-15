package com.atiurin.ultron.allure.video

/** One H.264 access unit in AVCC form (NAL units prefixed by their length, as `avcC` declares). */
class Mp4Sample(val data: ByteArray, val durationMs: Int, val keyFrame: Boolean)

/**
 * Minimal mp4 writer for one H.264 video track, in memory: `ftyp`, `moov` before `mdat` (fast start, every size is
 * known up front), timescale 1000. Written here rather than taken from a library because the browser encoder
 * (WebCodecs) delivers raw H.264 and nothing else, and an npm muxer would change the `kotlin-js-store` locks of
 * every consumer. Pure Kotlin, so the JVM tests parse its output box by box.
 */
object Mp4Muxer {
    const val TIMESCALE = 1000

    fun mux(avcC: ByteArray, width: Int, height: Int, samples: List<Mp4Sample>): ByteArray {
        require(samples.isNotEmpty()) { "mp4 needs at least one sample" }
        val duration = samples.sumOf { it.durationMs.toLong() }.toInt()
        val ftyp = box("ftyp", ascii("isom"), u32(0x200), ascii("isom"), ascii("iso2"), ascii("avc1"), ascii("mp41"))
        // The chunk offset depends on the size of moov, which does not depend on the offset value itself.
        val moovSize = moov(avcC, width, height, samples, duration, 0).size
        val moov = moov(avcC, width, height, samples, duration, ftyp.size + moovSize + 8)
        val mdat = box("mdat", *samples.map { it.data }.toTypedArray())
        return concat(ftyp, moov, mdat)
    }

    private fun moov(avcC: ByteArray, width: Int, height: Int, samples: List<Mp4Sample>, duration: Int, chunkOffset: Int): ByteArray {
        val mvhd = box(
            "mvhd", u32(0), u32(0), u32(0), u32(TIMESCALE), u32(duration), u32(0x10000), u16(0x100), ByteArray(10),
            MATRIX, ByteArray(24), u32(2),
        )
        val tkhd = box(
            "tkhd", u32(0x000003), u32(0), u32(0), u32(1), u32(0), u32(duration), ByteArray(8), u16(0), u16(0), u16(0), u16(0),
            MATRIX, u32(width shl 16), u32(height shl 16),
        )
        val mdhd = box("mdhd", u32(0), u32(0), u32(0), u32(TIMESCALE), u32(duration), u16(0x55C4), u16(0))
        val hdlr = box("hdlr", u32(0), u32(0), ascii("vide"), ByteArray(12), "VideoHandler".encodeToByteArray(), byteArrayOf(0))
        val vmhd = box("vmhd", u32(1), u16(0), ByteArray(6))
        val dinf = box("dinf", box("dref", u32(0), u32(1), box("url ", u32(1))))
        val avc1 = box(
            "avc1", ByteArray(6), u16(1), ByteArray(16), u16(width), u16(height), u32(0x480000), u32(0x480000), u32(0), u16(1),
            ByteArray(32), u16(0x18), u16(0xFFFF), box("avcC", avcC),
        )
        val stsd = box("stsd", u32(0), u32(1), avc1)
        val stts = box("stts", u32(0), sttsEntries(samples))
        val keys = samples.indices.filter { samples[it].keyFrame }
        val stss = box("stss", u32(0), u32(keys.size), *keys.map { u32(it + 1) }.toTypedArray())
        val stsc = box("stsc", u32(0), u32(1), u32(1), u32(samples.size), u32(1))
        val stsz = box("stsz", u32(0), u32(0), u32(samples.size), *samples.map { u32(it.data.size) }.toTypedArray())
        val stco = box("stco", u32(0), u32(1), u32(chunkOffset))
        val stbl = box("stbl", stsd, stts, stss, stsc, stsz, stco)
        val minf = box("minf", vmhd, dinf, stbl)
        val trak = box("trak", tkhd, box("mdia", mdhd, hdlr, minf))
        return box("moov", mvhd, trak)
    }

    /** Equal durations in a row share one entry. */
    private fun sttsEntries(samples: List<Mp4Sample>): ByteArray {
        val runs = ArrayList<Pair<Int, Int>>()
        for (sample in samples) {
            val last = runs.lastOrNull()
            if (last != null && last.second == sample.durationMs) runs[runs.lastIndex] = (last.first + 1) to last.second
            else runs += 1 to sample.durationMs
        }
        return concat(u32(runs.size), *runs.map { concat(u32(it.first), u32(it.second)) }.toTypedArray())
    }

    private val MATRIX = concat(u32(0x10000), u32(0), u32(0), u32(0), u32(0x10000), u32(0), u32(0), u32(0), u32(0x40000000))

    internal fun box(type: String, vararg parts: ByteArray): ByteArray {
        val size = 8 + parts.sumOf { it.size }
        return concat(u32(size), ascii(type), *parts)
    }

    internal fun ascii(text: String): ByteArray = ByteArray(text.length) { text[it].code.toByte() }
    internal fun u16(v: Int): ByteArray = byteArrayOf((v shr 8).toByte(), v.toByte())
    internal fun u32(v: Int): ByteArray = byteArrayOf((v shr 24).toByte(), (v shr 16).toByte(), (v shr 8).toByte(), v.toByte())

    internal fun concat(vararg parts: ByteArray): ByteArray {
        val out = ByteArray(parts.sumOf { it.size })
        var at = 0
        for (part in parts) { part.copyInto(out, at); at += part.size }
        return out
    }
}
