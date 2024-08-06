package com.atiurin.ultron.core.compose.config

import com.atiurin.ultron.log.ULogger
import com.atiurin.ultron.log.UltronLogcatLogger

actual fun getPlatformLoggers(): List<ULogger> {
    return listOf(UltronLogcatLogger())
}