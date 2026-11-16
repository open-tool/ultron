package com.atiurin.ultron.allure.video

// The JavaScript behind the WebCodecs bridge, one text for both dialects: `js(...)` takes it as a constant on both
// Kotlin/JS and wasmJs. Kept apart from the Kotlin so that the two dialects cannot drift. ES5 syntax on purpose:
// the compiler parses `js(...)` text with an ES5 parser, so no arrow functions, `let` or `async`; the browser that
// runs it has WebCodecs and therefore Promises.

/** `(codec, width, height, bitrate) => Promise<boolean>` */
internal const val WEBCODECS_SUPPORTED_JS =
    "(function(codec, width, height, bitrate) {" +
        "var config = { codec: codec, width: width, height: height, bitrate: bitrate, framerate: 1 };" +
        "if (codec.indexOf('avc1') === 0) { config.avc = { format: 'avc' }; config.hardwareAcceleration = 'prefer-software'; }" +
        "return VideoEncoder.isConfigSupported(config).then(function(r) { return !!(r && r.supported); });" +
        "})"

/**
 * `(codec, width, height, bitrate, pngs, pts, durations, keyEvery) => Promise<{ description, chunks: [{ data, ts, key }] }>`
 * `pngs` are typed arrays, `pts` and `durations` plain arrays of milliseconds. Frames are drawn on a canvas of the
 * even target size and the last column and row are repeated into the padding.
 */
internal const val WEBCODECS_ENCODE_JS =
    "(function(codec, width, height, bitrate, pngs, pts, durations, keyEvery) {" +
        "var chunks = []; var description = null; var failure = null;" +
        "var encoder = new VideoEncoder({" +
        "output: function(chunk, meta) {" +
        "var data = new Uint8Array(chunk.byteLength); chunk.copyTo(data);" +
        "if (description === null && meta && meta.decoderConfig && meta.decoderConfig.description) {" +
        "var d = meta.decoderConfig.description;" +
        "description = new Uint8Array(d instanceof ArrayBuffer ? d.slice(0) : d.buffer.slice(d.byteOffset, d.byteOffset + d.byteLength));" +
        "}" +
        "chunks.push({ data: data, ts: chunk.timestamp / 1000, key: chunk.type === 'key' });" +
        "}," +
        "error: function(e) { failure = e; }" +
        "});" +
        "var config = { codec: codec, width: width, height: height, bitrate: bitrate, framerate: 1 };" +
        "if (codec.indexOf('avc1') === 0) { config.avc = { format: 'avc' }; config.hardwareAcceleration = 'prefer-software'; }" +
        "encoder.configure(config);" +
        "function step(i) {" +
        "if (failure || i >= pngs.length) return Promise.resolve();" +
        "return createImageBitmap(new Blob([pngs[i]], { type: 'image/png' })).then(function(bitmap) {" +
        "var canvas = new OffscreenCanvas(width, height);" +
        "var ctx = canvas.getContext('2d');" +
        "ctx.drawImage(bitmap, 0, 0);" +
        "if (width > bitmap.width) ctx.drawImage(canvas, bitmap.width - 1, 0, 1, height, bitmap.width, 0, width - bitmap.width, height);" +
        "if (height > bitmap.height) ctx.drawImage(canvas, 0, bitmap.height - 1, width, 1, 0, bitmap.height, width, height - bitmap.height);" +
        "bitmap.close();" +
        "var frame = new VideoFrame(canvas, { timestamp: pts[i] * 1000, duration: durations[i] * 1000 });" +
        "encoder.encode(frame, { keyFrame: i % keyEvery === 0 });" +
        "frame.close();" +
        "return step(i + 1);" +
        "});" +
        "}" +
        "return step(0).then(function() { return failure ? null : encoder.flush(); }).then(function() {" +
        "encoder.close();" +
        "if (failure) throw failure;" +
        "return { description: description, chunks: chunks };" +
        "});" +
        "})"
