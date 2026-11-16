package com.atiurin.ultron.allure.screenrecord

/**
 * Pure helpers around the `screenrecord` process. `UiAutomation.executeShellCommand` runs no shell
 * (plain `Runtime.exec`), so every command here is a single argv-style line and all parsing is done in Kotlin.
 */
object ScreenRecordProcess {
    const val REMOTE_DIR = "/data/local/tmp"

    /**
     * The device path of a recording. [processToken] tells the instrumentation processes on one device apart (the
     * recorder passes its pid): two of them recording a same-named test with the same [sequence] write different
     * files, and [findPid], which matches by this path, stops only the process's own `screenrecord`.
     */
    fun fileName(testName: String, sequence: Int, processToken: String): String {
        val safe = testName.replace(Regex("[^A-Za-z0-9_-]+"), "_").trim('_').take(80)
        return "$REMOTE_DIR/ultron_${processToken}_${safe}_$sequence.mp4"
    }

    fun command(options: ScreenRecordOptions, remotePath: String): String = buildString {
        append("screenrecord --time-limit ").append(options.timeLimitSec)
        append(" --bit-rate ").append(options.bitRate)
        options.size?.let { append(" --size ").append(it) }
        append(' ').append(remotePath)
    }

    /** The `ps` invocations tried in order: toybox (API 26+) first, then the toolbox `ps` of API 23-25. */
    val PS_COMMANDS = listOf("ps -A -o PID,ARGS", "ps")

    /**
     * PID of the `screenrecord` whose command line ends with [remotePath]. Accepts both `ps -A -o PID,ARGS`
     * (PID first) and the toolbox `ps` of API 23-25 (`USER PID PPID ... NAME`): the PID is the first
     * all-digit token of the matching line.
     */
    fun findPid(psOutput: String, remotePath: String): Int? =
        psOutput.lineSequence()
            .map { it.trim() }
            .filter { it.contains("screenrecord") && (it.endsWith(" $remotePath") || it.endsWith(" $remotePath ")) }
            .mapNotNull { line -> line.split(Regex("\\s+")).firstNotNullOfOrNull { token -> token.toIntOrNull() } }
            .firstOrNull()
}
