import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.platform.testTag
import com.atiurin.ultron.core.compose.runDesktopUltronUiTest
import com.atiurin.ultron.extensions.assertTextEquals
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.page.Page
import org.junit.Test

class DesktopSampleTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun myTest() = runDesktopUltronUiTest {
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

object SamplePage : Page<SamplePage>() {
    private val text = hasTestTag("text")
    private val button = hasTestTag("button")

    fun someStep(){
        text.assertTextEquals("Hello")
        button.click()
        text.assertTextEquals("Compose")
    }
}