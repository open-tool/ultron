package com.atiurin.sampleapp.tests.compose

import androidx.compose.material.TriStateCheckbox
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.hasContentDescription
import com.atiurin.ultron.core.compose.createDefaultUltronComposeRule
import com.atiurin.ultron.core.compose.nodeinteraction.click
import com.atiurin.ultron.extensions.assertIsIndeterminate
import org.junit.Rule
import org.junit.Test

class CheckBoxTest {
    @get:Rule
    val composeRule = createDefaultUltronComposeRule()

    @Test
    fun assertIsIndeterminate() {
        val testTag = "checkBox"
        composeRule.setContent {
            val checkedState = remember { mutableStateOf(ToggleableState.Indeterminate) }
            TriStateCheckbox(
                state = checkedState.value,
                onClick = {
                    if (checkedState.value == ToggleableState.Indeterminate || checkedState.value == ToggleableState.Off)
                        checkedState.value = ToggleableState.On
                    else checkedState.value = ToggleableState.Off
                },
                modifier = Modifier.semantics { contentDescription = testTag }
            )
        }

        hasContentDescription("checkBox").assertIsIndeterminate()
            .click().assertIsOn()
            .click().assertIsOff()
    }
}
