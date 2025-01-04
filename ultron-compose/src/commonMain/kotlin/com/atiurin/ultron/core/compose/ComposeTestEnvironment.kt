package com.atiurin.ultron.core.compose

import androidx.compose.ui.test.MainTestClock
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.unit.Density

interface ComposeTestEnvironment {
    val provider: SemanticsNodeInteractionsProvider
    /**
     * Current device screen's density.
     */
    val density: Density

    /**
     * Clock that drives frames and recompositions in compose tests.
     */
    val mainClock: MainTestClock
}

data class UltronComposeTestEnvironment(
    override val provider: SemanticsNodeInteractionsProvider,
    override val density: Density,
    override val mainClock: MainTestClock
): ComposeTestEnvironment