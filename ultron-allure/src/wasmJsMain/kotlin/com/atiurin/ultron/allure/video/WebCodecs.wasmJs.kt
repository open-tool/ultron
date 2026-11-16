package com.atiurin.ultron.allure.video

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.js.JsAny
import kotlin.js.JsBoolean
import kotlin.js.Promise
import kotlin.js.toBoolean

internal actual fun webCodecsBridge(): WebCodecsBridge? = if (hasWebCodecs()) WasmWebCodecsBridge else null

private fun hasWebCodecs(): Boolean = js("typeof VideoEncoder !== 'undefined' && typeof createImageBitmap !== 'undefined'")

private fun jsSupported(codec: String, width: Int, height: Int, bitrate: Int): Promise<JsBoolean> =
    js("($WEBCODECS_SUPPORTED_JS)(codec, width, height, bitrate)")

private fun jsEncode(
    codec: String, width: Int, height: Int, bitrate: Int, pngs: JsAny, pts: JsAny, durations: JsAny, keyEvery: Int,
): Promise<JsAny?> = js("($WEBCODECS_ENCODE_JS)(codec, width, height, bitrate, pngs, pts, durations, keyEvery)")

private fun newArray(): JsAny = js("[]")

private fun pushAny(array: JsAny, value: JsAny) { js("array.push(value)") }

private fun pushNumber(array: JsAny, value: Double) { js("array.push(value)") }

private fun newBytes(length: Int): JsAny = js("new Uint8Array(length)")

private fun setByte(bytes: JsAny, index: Int, value: Int) { js("bytes[index] = value") }

private fun getByte(bytes: JsAny, index: Int): Int = js("bytes[index]")

private fun lengthOf(array: JsAny): Int = js("array.length")

private fun itemAt(array: JsAny, index: Int): JsAny = js("array[index]")

private fun prop(obj: JsAny, key: String): JsAny? = js("obj[key]")

private fun numberProp(obj: JsAny, key: String): Double = js("obj[key]")

private fun boolProp(obj: JsAny, key: String): Boolean = js("!!obj[key]")

private suspend fun <T : JsAny?> Promise<T>.await(): T = suspendCoroutine { continuation ->
    then(
        { value -> continuation.resume(value); null },
        { error -> continuation.resumeWithException(RuntimeException("WebCodecs failed: $error")); null },
    )
}

// Bytes cross the wasm boundary one at a time: the frames are small PNGs of a screen, a few kilobytes each.
private fun toJsBytes(bytes: ByteArray): JsAny {
    val out = newBytes(bytes.size)
    for (i in bytes.indices) setByte(out, i, bytes[i].toInt() and 0xFF)
    return out
}

private fun fromJsBytes(bytes: JsAny): ByteArray = ByteArray(lengthOf(bytes)) { getByte(bytes, it).toByte() }

private object WasmWebCodecsBridge : WebCodecsBridge {
    override suspend fun isSupported(codec: String, width: Int, height: Int, bitrate: Int): Boolean =
        jsSupported(codec, width, height, bitrate).await().toBoolean()

    override suspend fun encode(
        codec: String, width: Int, height: Int, bitrate: Int,
        pngs: List<ByteArray>, ptsMs: List<Long>, durationsMs: List<Int>, keyEvery: Int,
    ): BrowserEncoded {
        val jsPngs = newArray().also { array -> pngs.forEach { pushAny(array, toJsBytes(it)) } }
        val jsPts = newArray().also { array -> ptsMs.forEach { pushNumber(array, it.toDouble()) } }
        val jsDurations = newArray().also { array -> durationsMs.forEach { pushNumber(array, it.toDouble()) } }
        val result = checkNotNull(jsEncode(codec, width, height, bitrate, jsPngs, jsPts, jsDurations, keyEvery).await())
        val chunks = checkNotNull(prop(result, "chunks"))
        return BrowserEncoded(
            description = prop(result, "description")?.let(::fromJsBytes),
            chunks = List(lengthOf(chunks)) { i ->
                val chunk = itemAt(chunks, i)
                BrowserChunk(fromJsBytes(checkNotNull(prop(chunk, "data"))), numberProp(chunk, "ts").toLong(), boolProp(chunk, "key"))
            },
        )
    }
}
