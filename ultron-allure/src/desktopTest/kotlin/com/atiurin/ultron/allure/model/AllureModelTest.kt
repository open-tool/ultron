package com.atiurin.ultron.allure.model

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AllureModelTest {

    private fun golden(): String =
        checkNotNull(javaClass.classLoader.getResourceAsStream("golden/result.json")) { "golden missing" }
            .readBytes().decodeToString()

    @Test
    fun `result with nested steps attachment and label serialises to the golden file`() {
        val result = AllureTestResult(
            uuid = "11111111-2222-3333-4444-555555555555",
            historyId = "4a1c6f2e9b3d7a85",
            fullName = "com.example.LoginTest.loginShowsError",
            name = "loginShowsError",
            status = AllureStatus.FAILED,
            statusDetails = AllureStatusDetails(
                message = "Expected 'Error' but was 'Hello'",
                trace = "AssertionError: Expected 'Error'\n  at LoginTest.kt:12"
            ),
            start = 1000,
            stop = 2000,
            steps = listOf(
                AllureStep(
                    name = "click on button", status = AllureStatus.PASSED, start = 1100, stop = 1200,
                    steps = listOf(AllureStep(name = "wait for idle", status = AllureStatus.PASSED, start = 1110, stop = 1150))
                ),
                AllureStep(
                    name = "assert text", status = AllureStatus.FAILED,
                    statusDetails = AllureStatusDetails(message = "Expected 'Error' but was 'Hello'"),
                    start = 1300, stop = 1900,
                    attachments = listOf(AllureAttachment(name = "screenshot_assert text", source = "aaaa-attachment.png", type = "image/png"))
                )
            ),
            parameters = listOf(AllureParameter("target", "desktop")),
            labels = listOf(AllureLabel("framework", "ultron"))
        )

        val encoded = AllureJson.encode(result)

        assertEquals(Json.parseToJsonElement(golden()), Json.parseToJsonElement(encoded))
        assertEquals(result, AllureJson.decodeResult(encoded))
    }

    @Test
    fun `status values are the five Allure strings`() {
        assertEquals(
            listOf("passed", "failed", "broken", "skipped", "unknown"),
            AllureStatus.entries.map { AllureJson.encode(it).trim('"') }
        )
    }

    @Test
    fun `historyId is stable for the same fullName and target and differs otherwise`() {
        assertEquals(AllureIds.historyId("a.B.c", "desktop"), AllureIds.historyId("a.B.c", "desktop"))
        assertNotEquals(AllureIds.historyId("a.B.c", "desktop"), AllureIds.historyId("a.B.d", "desktop"))
    }

    @Test
    fun `historyId tells parameter values apart and ignores their order`() {
        val alice = AllureIds.historyId("a.B.c", "desktop", mapOf("user" to "alice", "lang" to "en"))
        assertEquals(alice, AllureIds.historyId("a.B.c", "desktop", mapOf("lang" to "en", "user" to "alice")))
        assertNotEquals(alice, AllureIds.historyId("a.B.c", "desktop", mapOf("user" to "bob", "lang" to "en")))
        assertNotEquals(AllureIds.historyId("a.B.c", "desktop"), AllureIds.historyId("a.B.c", "desktop", mapOf("user" to "alice")))
        assertEquals(AllureIds.historyId("a.B.c", "desktop"), AllureIds.historyId("a.B.c", "desktop", emptyMap()))
    }

    @Test
    fun `historyId of the same test differs per target`() {
        val ids = listOf("desktop", "ios", "macos", "js", "wasmJs").map { AllureIds.historyId("a.B.c", it) }
        assertEquals(ids.size, ids.toSet().size, "one historyId per target: $ids")
    }

    @Test
    fun `uuid is a 36 char v4 string and unique per call`() {
        val a = AllureIds.uuid(); val b = AllureIds.uuid()
        assertEquals(36, a.length)
        assertEquals('4', a[14])
        assertNotEquals(a, b)
    }
}
