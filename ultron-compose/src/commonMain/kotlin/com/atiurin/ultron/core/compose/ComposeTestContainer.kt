package com.atiurin.ultron.core.compose

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider

object ComposeTestContainer {
    private lateinit var testEnvironment: ComposeTestEnvironment

    fun init(testEnvironment: ComposeTestEnvironment) {
        this.testEnvironment = testEnvironment
    }

    fun getProvider(): SemanticsNodeInteractionsProvider = this.testEnvironment.provider

    fun <T> withComposeTestEnvironment(block: (ComposeTestEnvironment) -> T) = block(testEnvironment)
}
