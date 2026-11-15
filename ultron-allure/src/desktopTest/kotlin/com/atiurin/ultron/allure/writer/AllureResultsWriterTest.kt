package com.atiurin.ultron.allure.writer

import com.atiurin.ultron.allure.model.AllureStatus
import com.atiurin.ultron.allure.model.AllureTestResult
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AllureResultsWriterTest {
    private val dir = Files.createTempDirectory("ultron-allure").toFile().also { it.delete() }
    private val sink = FileSystemResultsSink(FileSystem.SYSTEM, dir.toOkioPath())
    private val writer = AllureResultsWriter(sink)

    private fun result(uuid: String) = AllureTestResult(
        uuid = uuid, historyId = "h", fullName = "A.b", name = "b",
        status = AllureStatus.PASSED, start = 1, stop = 2
    )

    @Test
    fun `writing a result creates uuid-result json in a directory created on demand`() {
        writer.writeResult(result("abc"))

        val file = dir.resolve("abc-result.json")
        assertTrue(file.isFile, "expected $file")
        assertTrue(file.readText().contains("\"fullName\":\"A.b\""))
    }

    @Test
    fun `writing an attachment stores bytes and returns the source name`() {
        val source = writer.writeAttachment(byteArrayOf(1, 2, 3), extension = "png")

        assertTrue(source.endsWith("-attachment.png"), source)
        assertContentEquals(byteArrayOf(1, 2, 3), dir.resolve(source).readBytes())
    }

    @Test
    fun `two attachments get distinct sources`() {
        val a = writer.writeAttachment(byteArrayOf(1), "txt")
        val b = writer.writeAttachment(byteArrayOf(2), "txt")
        assertTrue(a != b)
    }

    @Test
    fun `in memory sink keeps files by name`() {
        val mem = InMemoryResultsSink()
        AllureResultsWriter(mem).writeResult(result("xyz"))

        assertEquals(setOf("xyz-result.json"), mem.snapshot().keys)
    }

    @Test
    fun `in memory sink reports every write as it happens`() {
        val streamed = mutableListOf<Pair<String, Int>>()
        val mem = InMemoryResultsSink { name, bytes -> streamed += name to bytes.size }
        val writer = AllureResultsWriter(mem)

        writer.writeAttachment(byteArrayOf(1, 2, 3), "png")
        assertEquals(1, streamed.size, "attachment reported before anything else is written")
        assertEquals(3, streamed.single().second)

        writer.writeResult(result("xyz"))
        assertEquals(listOf("xyz-result.json"), streamed.drop(1).map { it.first })
        assertEquals(mem.snapshot().getValue("xyz-result.json").size, streamed.last().second)
    }

    @Test
    fun `in-memory sink streams every file and keeps only what keepFiles allows`() {
        val streamed = mutableListOf<String>()
        val sink = InMemoryResultsSink { name, _ -> streamed += name }
        sink.write("kept.json", byteArrayOf(1))
        sink.keepFiles = false
        sink.write("streamed.png", byteArrayOf(2))

        assertEquals(listOf("kept.json", "streamed.png"), streamed)
        assertEquals(setOf("kept.json"), sink.snapshot().keys)
    }
}
