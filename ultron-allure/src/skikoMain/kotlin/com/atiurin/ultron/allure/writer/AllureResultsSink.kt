package com.atiurin.ultron.allure.writer

import okio.FileSystem
import okio.Path

/** Where result and attachment files go. One implementation per runtime, see [platformResultsSink]. */
interface AllureResultsSink {
    /** Human readable target, logged once on first write. */
    val description: String
    fun write(fileName: String, bytes: ByteArray)
    fun write(fileName: String, text: String) = write(fileName, text.encodeToByteArray())
}

/** Writes into [directory], creating it on first write. Used on JVM, Android and Kotlin/Native. */
class FileSystemResultsSink(
    private val fileSystem: FileSystem,
    private val directory: Path,
) : AllureResultsSink {
    override val description: String get() = directory.toString()

    override fun write(fileName: String, bytes: ByteArray) {
        fileSystem.createDirectories(directory)
        fileSystem.write(directory / fileName) { write(bytes) }
    }
}

/**
 * Keeps files in memory. Used on js/wasmJs, where the sample tests run in a browser.
 *
 * [onWrite] is called with every file as soon as it is written, so a host-side transport (the browser
 * console, see `UltronAllureResults`) receives results and attachments while the run is still going and
 * keeps them if the page crashes before the end of the run. With [keepFiles] off the sink only streams:
 * nothing is retained for [snapshot], which bounds memory in a long run with many attachments.
 */
class InMemoryResultsSink(
    private val onWrite: ((fileName: String, bytes: ByteArray) -> Unit)? = null,
) : AllureResultsSink {
    private val files = LinkedHashMap<String, ByteArray>()
    override val description: String get() = "in-memory (${files.size} files)"

    /** `false` streams through [onWrite] without keeping the file; files written earlier stay. */
    var keepFiles: Boolean = true

    override fun write(fileName: String, bytes: ByteArray) {
        if (keepFiles) files[fileName] = bytes
        onWrite?.invoke(fileName, bytes)
    }

    fun snapshot(): Map<String, ByteArray> = files.toMap()
    fun clear() = files.clear()
}

/** Environment variable lookup; `null` where the runtime has no environment (browser). */
internal expect fun platformEnv(name: String): String?

/** Default sink for the current runtime, writing under [directory] where a file system exists. */
internal expect fun platformResultsSink(directory: String): AllureResultsSink
