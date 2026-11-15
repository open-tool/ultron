package com.atiurin.ultron.utils

import com.atiurin.ultron.annotations.InternalUltronApi

expect fun sleep(timeMs: Long)

/**
 * The thread the caller runs on, compared by equality; `null` where one test or block cannot be told apart from
 * another one by its thread (js/wasmJs: one thread, tests interleave through their promises).
 */
@InternalUltronApi
expect fun currentExecutionThread(): Any?
