package com.atiurin.ultron.utils

import com.atiurin.ultron.annotations.InternalUltronApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.pthread_self

@OptIn(ExperimentalForeignApi::class)
@InternalUltronApi
actual fun currentExecutionThread(): Any? = pthread_self()
