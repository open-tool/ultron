package com.atiurin.sampleapp.tests.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import com.atiurin.ultron.core.compose.ComposeTestContainer
import com.atiurin.ultron.core.compose.runUltronUiTest
import com.atiurin.ultron.extensions.assertTextContains
import com.atiurin.ultron.extensions.assertTextEquals
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.extensions.isSuccess
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.test.TestResult
import org.junit.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class RunUltronUiTest {

    @Test
    fun useUnmergedTreeConfigTest() = runUltronUiTest {
        val testTag = "element"
        setContent {
            Column {
                Button(onClick = {}, modifier = Modifier.testTag(testTag)) {
                    Text("Text1")
                    Text("Text2")
                }
            }
        }
        assertTrue ("Ultron operation success should be true") {
            hasTestTag(testTag).isSuccess { assertTextContains("Text1") }
        }
    }

    @Test
    fun oldSourceStyleInitializesCommonComposeEnvironment() = runUltronUiTest {
        setCompatibilityContent()

        assertTrue(ComposeTestContainer.isInitialized, "Compose test container should be initialized")
        hasTestTag(TextTag).assertTextEquals("Hello")
        hasTestTag(ButtonTag).click()
        waitForIdle()
        hasTestTag(TextTag).assertTextEquals("Compose")
    }

    @Test
    fun currentComposeRunnerParametersWorkInAndroidSampleApp(): TestResult = runUltronUiTest(
        runTestContext = EmptyCoroutineContext,
        testTimeout = 5.seconds,
    ) {
        setCompatibilityContent()

        awaitIdle()
        assertTrue(ComposeTestContainer.isInitialized, "Compose test container should be initialized")
        hasTestTag(TextTag).assertTextEquals("Hello")
    }

    private fun ComposeUiTest.setCompatibilityContent() {
        setContent {
            var text by remember { mutableStateOf("Hello") }

            Column {
                Text(
                    text = text,
                    modifier = Modifier.testTag(TextTag),
                )
                Button(
                    onClick = { text = "Compose" },
                    modifier = Modifier.testTag(ButtonTag),
                ) {
                    Text("Switch")
                }
            }
        }
    }

    private companion object {
        const val TextTag = "compatibility-text"
        const val ButtonTag = "compatibility-button"
    }
}
