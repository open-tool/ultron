
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
import com.atiurin.ultron.core.compose.runDesktopUltronUiTest
import com.atiurin.ultron.core.test.UltronTest
import com.atiurin.ultron.extensions.assertTextEquals
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.page.Page
import org.junit.Test

class DesktopSampleTest : UltronTest()  {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun myTest() = test {
        runDesktopUltronUiTest {
            setContent {
                var text by remember { mutableStateOf("Hello") }

                Text(
                    text = text,
                    modifier = Modifier.testTag("text")
                )
                Button(
                    onClick = { text = "Compose" },
                    modifier = Modifier.testTag("button")
                ) {
                    Text("Click me")
                }
            }

            SamplePage {
                someStep()
            }
        }
    }
}

object SamplePage : Page<SamplePage>() {
    private val text = hasTestTag("text")
    private val button = hasTestTag("button")

    fun someStep(){
        text.assertTextEquals("Hello")
        button.click()
        text.assertTextEquals("Compose")
    }
}