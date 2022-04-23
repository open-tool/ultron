package com.atiurin.sampleapp.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import com.atiurin.sampleapp.activity.ActionsStatus
import com.atiurin.sampleapp.activity.ComposeElementsActivity

@Composable
fun SwipeableNode(statusState: MutableState<String>) {
    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consumeAllChanges()

                    val (x, y) = dragAmount
                    when {
                        x > 0 -> {
                            statusState.value = ActionsStatus.SwipeRight.name
                        }
                        x < 0 -> {
                            statusState.value = ActionsStatus.SwipeLeft.name
                        }
                    }
                    when {
                        y > 0 -> {
                            statusState.value = ActionsStatus.SwipeDown.name
                        }
                        y < 0 -> {
                            statusState.value = ActionsStatus.SwipeUp.name
                        }
                    }
                }
            }
            .semantics { testTag = ComposeElementsActivity.swipeableNode }
            .width(100.dp)
            .height(100.dp)
            .background(Color.Blue)
    )
}