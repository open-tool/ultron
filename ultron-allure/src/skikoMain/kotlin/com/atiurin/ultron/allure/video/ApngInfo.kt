package com.atiurin.ultron.allure.video

/** Read-only facts about an (A)PNG, for tests and tooling that check what was attached. */
object ApngInfo {
    fun isPng(bytes: ByteArray): Boolean = bytes.size >= 8 && bytes.copyOfRange(0, 8).contentEquals(PngChunks.SIGNATURE)

    /** Number of frames declared in `acTL`, or `null` for a still PNG or a non-PNG. */
    fun frameCount(bytes: ByteArray): Int? {
        if (!isPng(bytes)) return null
        val actl = runCatching { PngChunks.parse(bytes) }.getOrNull()?.firstOrNull { it.type == "acTL" } ?: return null
        return PngChunks.readInt(actl.data, 0)
    }
}
