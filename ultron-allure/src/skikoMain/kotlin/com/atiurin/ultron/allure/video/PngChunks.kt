package com.atiurin.ultron.allure.video

/** Minimal PNG chunk reader/writer: enough to repack still PNGs into an APNG without a zlib dependency. */
internal object PngChunks {
    val SIGNATURE = byteArrayOf(0x89.toByte(), 'P'.code.toByte(), 'N'.code.toByte(), 'G'.code.toByte(), 0x0D, 0x0A, 0x1A, 0x0A)

    class Chunk(val typeBytes: ByteArray, val data: ByteArray, val crc: Int) {
        val type: String get() = typeBytes.decodeToString()
    }

    fun parse(png: ByteArray): List<Chunk> {
        require(png.size >= 8 && png.copyOfRange(0, 8).contentEquals(SIGNATURE)) { "not a PNG: bad signature" }
        val chunks = mutableListOf<Chunk>()
        var pos = 8
        while (pos + 8 <= png.size) {
            val length = readInt(png, pos)
            require(length >= 0 && pos + 12 + length <= png.size) { "corrupt PNG chunk at offset $pos" }
            val typeBytes = png.copyOfRange(pos + 4, pos + 8)
            val data = png.copyOfRange(pos + 8, pos + 8 + length)
            val crc = readInt(png, pos + 8 + length)
            chunks += Chunk(typeBytes, data, crc)
            pos += 12 + length
        }
        return chunks
    }

    fun chunk(type: String, data: ByteArray): ByteArray {
        val typeBytes = type.encodeToByteArray()
        require(typeBytes.size == 4) { "chunk type must be 4 ASCII bytes" }
        val out = ByteArray(12 + data.size)
        writeInt(out, 0, data.size)
        typeBytes.copyInto(out, 4)
        data.copyInto(out, 8)
        writeInt(out, 8 + data.size, crc32(typeBytes + data))
        return out
    }

    fun readInt(bytes: ByteArray, offset: Int): Int =
        ((bytes[offset].toInt() and 0xFF) shl 24) or
            ((bytes[offset + 1].toInt() and 0xFF) shl 16) or
            ((bytes[offset + 2].toInt() and 0xFF) shl 8) or
            (bytes[offset + 3].toInt() and 0xFF)

    fun readShort(bytes: ByteArray, offset: Int): Int =
        ((bytes[offset].toInt() and 0xFF) shl 8) or (bytes[offset + 1].toInt() and 0xFF)

    fun writeInt(target: ByteArray, offset: Int, value: Int) {
        target[offset] = (value ushr 24).toByte()
        target[offset + 1] = (value ushr 16).toByte()
        target[offset + 2] = (value ushr 8).toByte()
        target[offset + 3] = value.toByte()
    }

    fun writeShort(target: ByteArray, offset: Int, value: Int) {
        target[offset] = (value ushr 8).toByte()
        target[offset + 1] = value.toByte()
    }

    private val crcTable = IntArray(256) { n ->
        var c = n
        repeat(8) { c = if (c and 1 != 0) 0xEDB88320.toInt() xor (c ushr 1) else c ushr 1 }
        c
    }

    fun crc32(bytes: ByteArray): Int {
        var c = -1
        for (b in bytes) c = crcTable[(c xor b.toInt()) and 0xFF] xor (c ushr 8)
        return c.inv()
    }

    /** Test fixture helper: rewrites a PNG so that its image data is spread over [pieces] IDAT chunks. */
    fun splitIdat(png: ByteArray, pieces: Int): ByteArray {
        val chunks = parse(png)
        val idat = chunks.filter { it.type == "IDAT" }.fold(ByteArray(0)) { acc, c -> acc + c.data }
        val size = (idat.size + pieces - 1) / pieces
        val out = ArrayList<Byte>(png.size + pieces * 12)
        out.addAll(SIGNATURE.toList())
        var idatWritten = false
        chunks.forEach { c ->
            when (c.type) {
                "IDAT" -> if (!idatWritten) {
                    idatWritten = true
                    idat.toList().chunked(size).forEach { part -> out.addAll(chunk("IDAT", part.toByteArray()).toList()) }
                }
                else -> out.addAll(chunk(c.type, c.data).toList())
            }
        }
        return out.toByteArray()
    }
}
