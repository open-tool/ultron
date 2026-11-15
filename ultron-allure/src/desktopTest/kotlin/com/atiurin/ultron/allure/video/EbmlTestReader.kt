package com.atiurin.ultron.allure.video

/** Reads EBML back for the WebM tests: a flat list of elements with their payload ranges, masters descended into. */
class EbmlElement(val id: Long, val start: Int, val end: Int, val children: List<EbmlElement>)

object EbmlTestReader {
    private val MASTERS = setOf(0x1A45DFA3L, 0x18538067L, 0x1549A966L, 0x1654AE6BL, 0xAEL, 0xE0L, 0x1F43B675L)

    fun parse(bytes: ByteArray): List<EbmlElement> = parse(bytes, 0, bytes.size)

    private fun parse(bytes: ByteArray, from: Int, to: Int): List<EbmlElement> {
        val out = ArrayList<EbmlElement>()
        var at = from
        while (at < to) {
            val idLength = Integer.numberOfLeadingZeros(bytes[at].toInt() and 0xFF) - 24 + 1
            var id = 0L
            for (i in 0 until idLength) id = (id shl 8) or (bytes[at + i].toLong() and 0xFF)
            at += idLength
            val sizeLength = Integer.numberOfLeadingZeros(bytes[at].toInt() and 0xFF) - 24 + 1
            var size = (bytes[at].toLong() and 0xFF) and ((1L shl (8 - sizeLength)) - 1)
            for (i in 1 until sizeLength) size = (size shl 8) or (bytes[at + i].toLong() and 0xFF)
            at += sizeLength
            val end = at + size.toInt()
            out += EbmlElement(id, at, end, if (id in MASTERS) parse(bytes, at, end) else emptyList())
            at = end
        }
        return out
    }

    fun children(element: EbmlElement, id: Long): List<EbmlElement> = element.children.filter { it.id == id }

    fun uint(bytes: ByteArray, element: EbmlElement): Long {
        var value = 0L
        for (i in element.start until element.end) value = (value shl 8) or (bytes[i].toLong() and 0xFF)
        return value
    }
}
