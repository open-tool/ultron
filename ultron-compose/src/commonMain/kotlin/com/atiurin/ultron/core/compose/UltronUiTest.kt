package com.atiurin.ultron.core.compose

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.test.TestResult

@OptIn(ExperimentalTestApi::class)
fun runUltronUiTest(
    effectContext: CoroutineContext = EmptyCoroutineContext,
    runTestContext: CoroutineContext = EmptyCoroutineContext,
    testTimeout: Duration = 60.seconds,
    block: suspend ComposeUiTest.() -> Unit
): TestResult {
    return runComposeUiTest(
        effectContext = effectContext,
        runTestContext = runTestContext,
        testTimeout = testTimeout,
    ) {
        ComposeTestContainer.init(
            UltronComposeTestEnvironment(
                provider = this,
                mainClock = this.mainClock,
                density = this.density
            )
        )
        this.block()
    }
}
