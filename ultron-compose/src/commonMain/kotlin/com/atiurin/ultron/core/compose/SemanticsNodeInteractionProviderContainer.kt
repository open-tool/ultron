package com.atiurin.ultron.core.compose

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider

object SemanticsNodeInteractionProviderContainer {
    private lateinit var provider: SemanticsNodeInteractionsProvider

    fun init(provider: SemanticsNodeInteractionsProvider) {
        this.provider = provider
    }

    fun getProvider(): SemanticsNodeInteractionsProvider = this.provider

    fun <T> withSemanticsProvider(block: (SemanticsNodeInteractionsProvider) -> T) = block(provider)
}