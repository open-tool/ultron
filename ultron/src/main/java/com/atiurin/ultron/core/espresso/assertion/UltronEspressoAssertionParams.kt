package com.atiurin.ultron.core.espresso.assertion

import com.atiurin.ultron.core.common.UltronOperationType

data class UltronEspressoAssertionParams(
    val operationName: String,
    val operationDescription: String,
    val operationType: UltronOperationType = EspressoAssertionType.ASSERT_MATCHES,
    val descriptionToAppend: String = "Default assert matcher description"
)
