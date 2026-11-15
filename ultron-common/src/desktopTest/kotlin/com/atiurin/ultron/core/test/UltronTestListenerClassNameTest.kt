package com.atiurin.ultron.core.test

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UltronTestListenerClassNameTest {
    class Scenario : UltronTest() {
        fun run() = test { go { } }
    }

    @AfterTest
    fun cleanup() = UltronTestListeners.clear()

    @Test
    fun `listeners receive the qualified class name on the JVM`() {
        val seen = mutableListOf<String>()
        UltronTestListeners.register(object : UltronTestListener {
            override val id = "recorder"
            override fun beforeTest(testClass: String, testName: String?) { seen += "before:$testClass" }
            override fun beforeBlock(testClass: String, block: TestBlock) { if (block == TestBlock.GO) seen += "block:$testClass" }
            override fun afterTest(testClass: String, throwable: Throwable?) { seen += "after:$testClass" }
        })

        Scenario().run()

        val expected = "com.atiurin.ultron.core.test.UltronTestListenerClassNameTest.Scenario"
        assertEquals(listOf("before:$expected", "block:$expected", "after:$expected"), seen)
    }
}
