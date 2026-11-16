package com.atiurin.ultron.core.compose

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SkikoComposeUiTest
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.test.runSkikoComposeUiTest
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlinx.coroutines.test.TestResult

@OptIn(ExperimentalTestApi::class)
fun runDesktopUltronUiTest(
    width: Int = 1024,
    height: Int = 768,
    effectContext: CoroutineContext = EmptyCoroutineContext,
    runTestContext: CoroutineContext = EmptyCoroutineContext,
    testTimeout: Duration = Duration.INFINITE,
    block: suspend SkikoComposeUiTest.() -> Unit
): TestResult = runDesktopUltronUiTestNamed(null, width, height, effectContext, runTestContext, testTimeout, block)

/** Same as [runDesktopUltronUiTest], with an explicit [testName] handed to registered [UltronTestRunHook]s. */
@OptIn(ExperimentalTestApi::class)
fun runDesktopUltronUiTest(
    testName: String,
    width: Int = 1024,
    height: Int = 768,
    effectContext: CoroutineContext = EmptyCoroutineContext,
    runTestContext: CoroutineContext = EmptyCoroutineContext,
    testTimeout: Duration = Duration.INFINITE,
    block: suspend SkikoComposeUiTest.() -> Unit
): TestResult = runDesktopUltronUiTestNamed(testName, width, height, effectContext, runTestContext, testTimeout, block)

@OptIn(ExperimentalTestApi::class)
private fun runDesktopUltronUiTestNamed(
    testName: String?,
    width: Int,
    height: Int,
    effectContext: CoroutineContext,
    runTestContext: CoroutineContext,
    testTimeout: Duration,
    block: suspend SkikoComposeUiTest.() -> Unit
): TestResult =
    runSkikoComposeUiTest(
        size = Size(width.toFloat(), height.toFloat()),
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
    }
