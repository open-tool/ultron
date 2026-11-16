package com.atiurin.ultron.allure.video

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Promise

internal actual fun webCodecsBridge(): WebCodecsBridge? =
    if (js("typeof VideoEncoder !== 'undefined' && typeof createImageBitmap !== 'undefined'") as Boolean) JsWebCodecsBridge else null

private suspend fun <T> Promise<T>.await(): T = suspendCoroutine { continuation ->
    then({ value -> continuation.resume(value) }, { error -> continuation.resumeWithException(error) })
}

private fun toBytes(view: dynamic): ByteArray =
    js("new Int8Array(view.buffer, view.byteOffset, view.length)").unsafeCast<ByteArray>()

private object JsWebCodecsBridge : WebCodecsBridge {
    private val supportedFn: dynamic = js(WEBCODECS_SUPPORTED_JS)
    private val encodeFn: dynamic = js(WEBCODECS_ENCODE_JS)

    override suspend fun isSupported(codec: String, width: Int, height: Int, bitrate: Int): Boolean =
        (supportedFn(codec, width, height, bitrate).unsafeCast<Promise<Boolean>>()).await()

    override suspend fun encode(
        codec: String, width: Int, height: Int, bitrate: Int,
        pngs: List<ByteArray>, ptsMs: List<Long>, durationsMs: List<Int>, keyEvery: Int,
    ): BrowserEncoded {
        val result = encodeFn(
            codec, width, height, bitrate, pngs.toTypedArray(),
            ptsMs.map { it.toDouble() }.toTypedArray(), durationsMs.toTypedArray(), keyEvery,
        ).unsafeCast<Promise<dynamic>>().await()
        val description: dynamic = result.description
        val chunks = result.chunks.unsafeCast<Array<dynamic>>().map { chunk ->
            BrowserChunk(toBytes(chunk.data), (chunk.ts as Double).toLong(), chunk.key as Boolean)
        }
        return BrowserEncoded(if (description == null) null else toBytes(description), chunks)
    }
}
