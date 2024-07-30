package com.atiurin.sampleapp.tests.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import com.atiurin.ultron.core.compose.runUltronUiTest
import com.atiurin.ultron.extensions.assertTextContains
import com.atiurin.ultron.extensions.isSuccess
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
}