package com.atiurin.ultron.allure.model

import kotlin.random.Random

object AllureIds {
    /** Random UUID v4 string, built without platform APIs so it works on every target. */
    fun uuid(): String {
        val b = Random.nextBytes(16)
        b[6] = ((b[6].toInt() and 0x0f) or 0x40).toByte()
        b[8] = ((b[8].toInt() and 0x3f) or 0x80).toByte()
        val hex = b.joinToString("") { it.toUByte().toString(16).padStart(2, '0') }
        return "${hex.substring(0, 8)}-${hex.substring(8, 12)}-${hex.substring(12, 16)}-${hex.substring(16, 20)}-${hex.substring(20)}"
    }

    /**
     * Stable id for a test across runs: FNV-1a 64-bit of its fullName, [target] (see `platformTarget`) and
     * [parameters] sorted by name, as 16 hex chars. The target keeps the same test of two targets apart when their
     * results go into one report: with one id, Allure would show them as retries of one test and hide a failure on one
     * target behind a pass on another. The parameters do the same for the invocations of a parameterized test, while
     * one invocation keeps its id from run to run.
     */
    fun historyId(fullName: String, target: String, parameters: Map<String, String> = emptyMap()): String {
        val key = buildString {
            append(fullName).append('\u0000').append(target)
            parameters.entries.sortedBy { it.key }.forEach { (name, value) -> append('\u0000').append(name).append('=').append(value) }
        }
        var hash = -0x340d631b7bdddcdbL // FNV offset basis 0xcbf29ce484222325
        for (byte in key.encodeToByteArray()) {
            hash = hash xor (byte.toLong() and 0xff)
            hash *= 0x100000001b3L
        }
        return hash.toULong().toString(16).padStart(16, '0')
    }
}
