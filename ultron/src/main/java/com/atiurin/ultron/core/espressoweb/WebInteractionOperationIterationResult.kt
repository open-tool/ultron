package com.atiurin.ultron.core.espressoweb

import androidx.test.espresso.web.sugar.Web
import com.atiurin.ultron.core.common.OperationIterationResult

data class WebInteractionOperationIterationResult<T>(
    override val success: Boolean,
    override val exception: Throwable?,
    val webInteraction: Web.WebInteraction<T>?
) : OperationIterationResult