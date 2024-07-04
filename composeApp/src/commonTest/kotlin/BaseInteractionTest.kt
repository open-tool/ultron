import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import com.atiurin.ultron.core.common.options.TextContainsOption
import com.atiurin.ultron.core.compose.config.UltronComposeConfig
import com.atiurin.ultron.core.compose.nodeinteraction.click
import com.atiurin.ultron.core.compose.runUltronUiTest
import com.atiurin.ultron.extensions.assertIsDisplayed
import com.atiurin.ultron.extensions.assertTextContains
import com.atiurin.ultron.extensions.isSuccess
import com.atiurin.ultron.extensions.withAssertion
import com.atiurin.ultron.extensions.withUseUnmergedTree
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class BaseInteractionTest {
    @Test
    fun test() = runUltronUiTest {
        setContent {
            App()
        }
        hasText("Click me!").withAssertion() {
            hasTestTag("greeting")
                .assertIsDisplayed()
                .assertTextContains("Compose: Hello,", option = TextContainsOption(substring = true))
        }.click()
    }

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
        UltronComposeConfig.params.useUnmergedTree = true
        assertFalse("Ultron operation success should be false") {
            hasTestTag(testTag).isSuccess { assertTextContains("Text1") }
        }
        assertTrue ("Ultron operation success should be true") {
            hasTestTag(testTag).withUseUnmergedTree(false).isSuccess { assertTextContains("Text1") }
        }
    }

    @AfterTest
    fun disableUseUnmergedTree(){
        UltronComposeConfig.params.useUnmergedTree = false
    }
}