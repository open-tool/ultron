package com.atiurin.ultron.allure.video

/** One frame of the recording: an already encoded PNG and how long it stays on screen. */
class ApngFrame(val png: ByteArray, val delayMs: Int, val label: String = "")

/**
 * Builds an animated PNG out of still PNGs by moving their `IDAT` payload into `fdAT` chunks.
 * No decoding and no zlib: the frames keep the compression the platform encoder produced.
 * All frames must share the same `IHDR` (size, bit depth, colour type).
 */
object ApngAssembler {
    private const val DELAY_DEN = 1000
    private const val MAX_U16 = 0xFFFF

    fun assemble(frames: List<ApngFrame>): ByteArray {
        require(frames.isNotEmpty()) { "APNG needs at least one frame" }
        val parsed = frames.map { PngChunks.parse(it.png) }
        val first = parsed.first()
        val ihdr = first.firstOrNull { it.type == "IHDR" } ?: throw IllegalArgumentException("first frame has no IHDR")
        parsed.forEachIndexed { index, chunks ->
            val other = chunks.firstOrNull { it.type == "IHDR" }?.data
            require(other != null && other.contentEquals(ihdr.data)) { "frame $index has a different IHDR than frame 0" }
            require(chunks.any { it.type == "IDAT" }) { "frame $index has no IDAT" }
        }
        val width = PngChunks.readInt(ihdr.data, 0)
        val height = PngChunks.readInt(ihdr.data, 4)

        val out = ArrayList<ByteArray>()
        out += PngChunks.SIGNATURE
        out += PngChunks.chunk("IHDR", ihdr.data)
        out += PngChunks.chunk("acTL", ByteArray(8).also { PngChunks.writeInt(it, 0, frames.size); PngChunks.writeInt(it, 4, 0) })
        // Palette, transparency, gamma etc. of the first frame apply to the whole animation.
        first.takeWhile { it.type != "IDAT" }.filter { it.type != "IHDR" }.forEach { out += PngChunks.chunk(it.type, it.data) }

        var sequence = 0
        parsed.forEachIndexed { index, chunks ->
            out += fctl(sequence++, width, height, frames[index].delayMs)
            val idat = chunks.filter { it.type == "IDAT" }
            if (index == 0) {
                idat.forEach { out += PngChunks.chunk("IDAT", it.data) }
            } else {
                val payload = ByteArray(4 + idat.sumOf { it.data.size })
                PngChunks.writeInt(payload, 0, sequence++)
                var pos = 4
                idat.forEach { it.data.copyInto(payload, pos); pos += it.data.size }
                out += PngChunks.chunk("fdAT", payload)
            }
        }
        out += PngChunks.chunk("IEND", ByteArray(0))

        val result = ByteArray(out.sumOf { it.size })
        var pos = 0
        out.forEach { it.copyInto(result, pos); pos += it.size }
        return result
    }

    private fun fctl(sequence: Int, width: Int, height: Int, delayMs: Int): ByteArray {
        val data = ByteArray(26)
        PngChunks.writeInt(data, 0, sequence)
        PngChunks.writeInt(data, 4, width)
        PngChunks.writeInt(data, 8, height)
        PngChunks.writeInt(data, 12, 0)
        PngChunks.writeInt(data, 16, 0)
        PngChunks.writeShort(data, 20, delayMs.coerceIn(0, MAX_U16))
        PngChunks.writeShort(data, 22, DELAY_DEN)
        data[24] = 0 // dispose_op: APNG_DISPOSE_OP_NONE
        data[25] = 0 // blend_op: APNG_BLEND_OP_SOURCE — every frame is a full screen, nothing to blend
        return PngChunks.chunk("fcTL", data)
    }
}
