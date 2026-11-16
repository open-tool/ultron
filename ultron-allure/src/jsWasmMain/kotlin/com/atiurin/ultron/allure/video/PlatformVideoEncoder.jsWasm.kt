package com.atiurin.ultron.allure.video

internal actual fun platformVideoEncoder(): VideoEncoder? = webCodecsBridge()?.let(::WebCodecsVideoEncoder)
