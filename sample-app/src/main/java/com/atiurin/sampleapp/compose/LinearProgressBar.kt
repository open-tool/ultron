package com.atiurin.sampleapp.compose

import androidx.compose.foundation.progressSemantics
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import com.atiurin.sampleapp.activity.ComposeElementsActivity

@Composable
fun LinearProgressBar(statusState: MutableState<String>){
    val progressState = remember {
        mutableStateOf(0f)
    }
    LinearProgressIndicator(progress = progressState.value, modifier =
    Modifier
        .semantics {
            testTag = ComposeElementsActivity.progressBar
            setProgress { value ->
                progressState.value = value
                statusState.value = "set progress $value"
                true
            }
            progressBarRangeInfo = ProgressBarRangeInfo(progressState.value, 0f..progressState.value, 100)
        }
        .getProgress(progressState.value)
        .progressSemantics()
    )
}

val GetProgress = SemanticsPropertyKey<Float>("ProgressValue")
var SemanticsPropertyReceiver.getProgress by GetProgress

fun Modifier.getProgress(progress: Float): Modifier {
    return semantics { getProgress = progress }
}