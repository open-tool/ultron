import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import com.atiurin.ultron.core.compose.ComposeTestContainer
import com.atiurin.ultron.core.compose.runDesktopUltronUiTest
import com.atiurin.ultron.core.compose.runUltronUiTest
import com.atiurin.ultron.extensions.assertTextEquals
import com.atiurin.ultron.extensions.click
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.test.TestResult

@OptIn(ExperimentalTestApi::class)
class UltronComposeTestApiCompatibilityTest {
    @Test
    fun oldStyleRunUltronUiTest_stillInitializesUltronEnvironment() = runUltronUiTest {
        setCompatibilityContent()

        assertTrue(ComposeTestContainer.isInitialized)
        hasTestTag(TextTag).assertTextEquals("Hello")
        hasTestTag(ButtonTag).click()
        hasTestTag(TextTag).assertTextEquals("Compose")
    }

    @Test
    fun oldStyleRunDesktopUltronUiTest_stillInitializesUltronEnvironment() = runDesktopUltronUiTest {
        setCompatibilityContent()

        assertTrue(ComposeTestContainer.isInitialized)
        hasTestTag(TextTag).assertTextEquals("Hello")
        hasTestTag(ButtonTag).click()
        hasTestTag(TextTag).assertTextEquals("Compose")
    }

    @Test
    fun wrapperBlockSupportsSuspendComposeTestApi(): TestResult = runUltronUiTest {
        setCompatibilityContent()

        awaitIdle()
        hasTestTag(TextTag).assertTextEquals("Hello")
    }

    @Test
    fun wrapperAcceptsCurrentComposeRunnerParameters(): TestResult = runUltronUiTest(
        runTestContext = EmptyCoroutineContext,
        testTimeout = 5.seconds,
    ) {
        setCompatibilityContent()

        awaitIdle()
        hasTestTag(TextTag).assertTextEquals("Hello")
    }

    @Test
    fun desktopWrapperAcceptsCurrentComposeRunnerParameters(): TestResult = runDesktopUltronUiTest(
        runTestContext = EmptyCoroutineContext,
        testTimeout = 5.seconds,
    ) {
        setCompatibilityContent()

        awaitIdle()
        hasTestTag(TextTag).assertTextEquals("Hello")
    }

    private fun androidx.compose.ui.test.ComposeUiTest.setCompatibilityContent() {
        setContent {
            var text by remember { mutableStateOf("Hello") }

            Text(
                text = text,
                modifier = Modifier.testTag(TextTag),
            )
            Button(
                onClick = { text = "Compose" },
                modifier = Modifier.testTag(ButtonTag),
            ) {
                Text("Click me")
            }
        }
    }

    private companion object {
        const val TextTag = "compatibility-text"
        const val ButtonTag = "compatibility-button"
    }
}
