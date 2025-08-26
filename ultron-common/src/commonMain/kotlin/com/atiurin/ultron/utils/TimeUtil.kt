package com.atiurin.ultron.utils

import kotlin.time.ExperimentalTime
import kotlin.time.Clock

@OptIn(ExperimentalTime::class)
fun now() = Clock.System.now()
@OptIn(ExperimentalTime::class)
fun nowMs() = now().toEpochMilliseconds()

@OptIn(ExperimentalTime::class)
fun measureTimeMillis(function: () -> Any): Long {
    val start = now()
    function()
    return now().minus(start).inWholeMilliseconds
}