package com.atiurin.ultron.log

actual fun getFileLogger(): UltronFileLogger = UltronFileLoggerImpl()