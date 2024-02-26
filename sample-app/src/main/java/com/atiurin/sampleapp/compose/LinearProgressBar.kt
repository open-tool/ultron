package com.atiurin.sampleapp.compose

import androidx.compose.foundation.progressSemantics
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.testTag
import com.atiurin.sampleapp.activity.ComposeElementsActivity

@Composable
fun LinearProgressBar(statusState: MutableState<String>) {
    val progressState = remember {
        mutableStateOf(0f)
    }
    Button(onClick = { progressState.value += 0.1f }) {
        Text("Increase progress ${progressState.value}")
    }
    LinearProgressIndicator(progress = progressState.value, modifier = Modifier
        .semantics {
            testTag = ComposeElementsActivity.progressBar
            setProgress { value ->
                progressState.value = value
                statusState.value = "set progress $value"
                true
            }
        }
        .progressSemantics(
            progressState.value, 0f..progressState.value, 100
        )
    )
}
