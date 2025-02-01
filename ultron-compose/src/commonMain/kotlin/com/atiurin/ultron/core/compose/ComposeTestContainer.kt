package com.atiurin.ultron.core.compose

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.atiurin.ultron.exceptions.UltronException

object ComposeTestContainer {
    private lateinit var testEnvironment: ComposeTestEnvironment

    fun init(testEnvironment: ComposeTestEnvironment) {
        this.testEnvironment = testEnvironment
    }

    val isInitialized : Boolean
        get() = ::testEnvironment.isInitialized

    fun getProvider(): SemanticsNodeInteractionsProvider = this.testEnvironment.provider

    fun <T> withComposeTestEnvironment(block: (ComposeTestEnvironment) -> T): T {
        if (!isInitialized) throw UltronException("ComposeTestContainer isn't initialized!")
        return block(testEnvironment)
    }
}
