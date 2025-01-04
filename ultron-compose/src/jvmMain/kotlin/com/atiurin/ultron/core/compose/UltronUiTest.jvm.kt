package com.atiurin.ultron.core.compose

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SkikoComposeUiTest
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ExperimentalTestApi::class)
fun runDesktopUltronUiTest(
    width: Int = 1024,
    height: Int = 768,
    effectContext: CoroutineContext = EmptyCoroutineContext,
    block: SkikoComposeUiTest.() -> Unit
) {
    SkikoComposeUiTest(width, height, effectContext).runTest {
        ComposeTestContainer.init(
            UltronComposeTestEnvironment(
                provider = this,
                mainClock = this.mainClock,
                density = this.density
            )
        )
        block()
    }
}
