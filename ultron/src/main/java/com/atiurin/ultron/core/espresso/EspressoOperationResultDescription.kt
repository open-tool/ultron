package com.atiurin.ultron.core.espresso

import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.core.common.OperationResultDescription

class EspressoOperationResultDescription(
    override val result: OperationResult,
    override val description: String
) : OperationResultDescription