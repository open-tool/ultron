package com.atiurin.ultron.core.compose

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.atiurin.ultron.annotations.InternalUltronApi
import com.atiurin.ultron.core.test.UltronTestAsync
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
): TestResult = runUltronUiTestNamed(null, effectContext, runTestContext, testTimeout, block)

/**
 * Same as [runUltronUiTest], with an explicit [testName] handed to registered [UltronTestRunHook]s
 * (for example the Allure reporter). Use it where the runtime cannot discover the test name itself.
 */
@OptIn(ExperimentalTestApi::class)
fun runUltronUiTest(
    testName: String,
    effectContext: CoroutineContext = EmptyCoroutineContext,
    runTestContext: CoroutineContext = EmptyCoroutineContext,
    testTimeout: Duration = 60.seconds,
    block: suspend ComposeUiTest.() -> Unit
): TestResult = runUltronUiTestNamed(testName, effectContext, runTestContext, testTimeout, block)

/**
 * Inside a block of `UltronTest.test { }` the result is also handed to that test (see `UltronTestAsync`): on js/wasmJs,
 * where it is a promise whose body runs only after this call has returned, the test waits for it before its next block.
 */
@OptIn(ExperimentalTestApi::class, InternalUltronApi::class)
private fun runUltronUiTestNamed(
    testName: String?,
    effectContext: CoroutineContext,
    runTestContext: CoroutineContext,
    testTimeout: Duration,
    block: suspend ComposeUiTest.() -> Unit
): TestResult =
    runComposeUiTest(
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
        UltronTestRunHooks.around(testName) { this.block() }
    }.also { UltronTestAsync.await(it) }
