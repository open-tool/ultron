package com.atiurin.ultron.core.common.options

import com.atiurin.ultron.core.common.UltronOperationType

data class PerformCustomBlockOption(
    val operationType: UltronOperationType,
    val description: String = ""
)