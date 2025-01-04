package com.atiurin.ultron.core.compose

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ExperimentalTestApi::class)
fun runUltronUiTest(
    effectContext: CoroutineContext = EmptyCoroutineContext,
    block: ComposeUiTest.() -> Unit
) {
    runComposeUiTest(effectContext){
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