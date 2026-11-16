package com.atiurin.ultron.allure.video

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ApngAssemblerTest {

    private fun png(width: Int, height: Int, rgb: Int): ByteArray {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        for (x in 0 until width) for (y in 0 until height) image.setRGB(x, y, rgb)
        return ByteArrayOutputStream().also { ImageIO.write(image, "png", it) }.toByteArray()
    }

    private fun chunkTypes(bytes: ByteArray) = PngChunks.parse(bytes).map { it.type }

    @Test
    fun `two frames become IHDR acTL fcTL IDAT fcTL fdAT IEND with valid CRCs`() {
        val apng = ApngAssembler.assemble(
            listOf(
                ApngFrame(png(4, 3, 0xFFFF0000.toInt()), delayMs = 100),
                ApngFrame(png(4, 3, 0xFF00FF00.toInt()), delayMs = 250),
            )
        )

        assertTrue(apng.copyOfRange(0, 8).contentEquals(PngChunks.SIGNATURE), "PNG signature")
        val chunks = PngChunks.parse(apng)
        val types = chunks.map { it.type }
        assertEquals("IHDR", types.first())
        assertEquals("IEND", types.last())
        assertEquals(listOf("acTL", "fcTL", "IDAT", "fcTL", "fdAT"), types.filter { it in setOf("acTL", "fcTL", "IDAT", "fdAT") })
        chunks.forEach { assertEquals(it.crc, PngChunks.crc32(it.typeBytes + it.data), "CRC of ${it.type}") }

        val actl = chunks.single { it.type == "acTL" }.data
        assertEquals(2, PngChunks.readInt(actl, 0), "num_frames")
        assertEquals(0, PngChunks.readInt(actl, 4), "num_plays = infinite")

        val sequence = chunks.filter { it.type == "fcTL" || it.type == "fdAT" }.map { PngChunks.readInt(it.data, 0) }
        assertEquals(listOf(0, 1, 2), sequence, "sequence numbers shared by fcTL and fdAT")

        val secondFctl = chunks.filter { it.type == "fcTL" }[1].data
        assertEquals(250, PngChunks.readShort(secondFctl, 20), "delay_num")
        assertEquals(1000, PngChunks.readShort(secondFctl, 22), "delay_den")
        assertEquals(0, secondFctl[24].toInt(), "dispose_op = NONE")
        assertEquals(0, secondFctl[25].toInt(), "blend_op = SOURCE")
    }

    @Test
    fun `result is still a readable PNG whose default image is the first frame`() {
        val apng = ApngAssembler.assemble(
            listOf(ApngFrame(png(2, 2, 0xFF0000FF.toInt()), 50), ApngFrame(png(2, 2, 0xFFFFFFFF.toInt()), 50))
        )
        val decoded = ImageIO.read(ByteArrayInputStream(apng))
        assertEquals(2, decoded.width)
        assertEquals(0xFF0000FF.toInt(), decoded.getRGB(0, 0))
    }

    @Test
    fun `several IDAT chunks of one frame are merged into one fdAT`() {
        val whole = png(3, 3, 0xFF123456.toInt())
        val split = PngChunks.splitIdat(whole, pieces = 3)
        assertEquals(3, chunkTypes(split).count { it == "IDAT" }, "fixture really has three IDATs")

        val apng = ApngAssembler.assemble(listOf(ApngFrame(whole, 10), ApngFrame(split, 10)))
        val types = chunkTypes(apng)
        assertEquals(1, types.count { it == "fdAT" })
        val fdat = PngChunks.parse(apng).single { it.type == "fdAT" }.data
        val originalIdat = PngChunks.parse(whole).filter { it.type == "IDAT" }.flatMap { it.data.toList() }
        assertEquals(originalIdat, fdat.drop(4), "fdAT payload = concatenated IDAT data after the sequence number")
    }

    @Test
    fun `frames with different IHDR are rejected`() {
        assertFailsWith<IllegalArgumentException> {
            ApngAssembler.assemble(listOf(ApngFrame(png(2, 2, 0), 10), ApngFrame(png(3, 2, 0), 10)))
        }
    }

    @Test
    fun `delay is clamped to the u16 range and a single frame is a plain animation of one`() {
        val apng = ApngAssembler.assemble(listOf(ApngFrame(png(1, 1, 0), delayMs = 10_000_000)))
        val fctl = PngChunks.parse(apng).single { it.type == "fcTL" }.data
        assertEquals(65535, PngChunks.readShort(fctl, 20))
        assertEquals(1, PngChunks.readInt(PngChunks.parse(apng).single { it.type == "acTL" }.data, 0))
    }

    @Test
    fun `empty input is rejected`() {
        assertFailsWith<IllegalArgumentException> { ApngAssembler.assemble(emptyList()) }
    }
}
