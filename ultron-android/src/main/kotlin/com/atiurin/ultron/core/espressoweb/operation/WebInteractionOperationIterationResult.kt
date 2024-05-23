package com.atiurin.ultron.core.espressoweb.operation

import androidx.test.espresso.web.sugar.Web
import com.atiurin.ultron.core.common.OperationIterationResult

internal data class WebInteractionOperationIterationResult<T>(
    override val success: Boolean,
    override val exception: Throwable?,
    val webInteraction: Web.WebInteraction<T>?
) : OperationIterationResult