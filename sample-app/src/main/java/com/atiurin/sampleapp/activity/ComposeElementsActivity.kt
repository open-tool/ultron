package com.atiurin.sampleapp.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.clickListenerButton
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.disabledButton
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.likesCounterButton
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.likesCounterContentDesc
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.likesCounterTextContainer
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.likesCounterTextContainerContentDesc
import com.atiurin.sampleapp.compose.CustomButton
import com.atiurin.sampleapp.compose.LinearProgressBar
import com.atiurin.sampleapp.compose.RadioGroup
import com.atiurin.sampleapp.compose.RegionsClickListener
import com.atiurin.sampleapp.compose.SimpleOutlinedText
import com.atiurin.sampleapp.compose.SwipeableNode

class ComposeElementsActivity : ComponentActivity() {
    companion object Constants {
        const val statusText = "statusText"
        const val likesCounterButton = "LikeS_CounteR"
        const val likesCounterTextContainer = "text_container"
        const val clickListenerButton = "click_listener"
        const val simpleCheckbox = "simpleCheckbox"
        const val editableText = "editableText"
        const val swipeableNode = "swipableNode"
        const val progressBar = "ProgressBar"
        const val disabledButton = "disabledButton"
        const val radioButtonMaleTestTag = "radioButtonMaleTestTag"
        const val radioButtonFemaleTestTag = "radioButtonFemaleTestTag"
        const val likesCounterContentDesc = "LikeS_CounteR_ContentDesc"
        const val likesCounterTextContainerContentDesc = "text_container_content_desc"
    }

    @ExperimentalMaterialApi
    @ExperimentalUnitApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                val status = remember { mutableStateOf("nothing") }
                Text(text = status.value, modifier = Modifier.semantics { testTag = statusText })
                ButtonWithCount()
                CheckBox(title = "Simple checkbox", testTagValue = simpleCheckbox, status)
                ClickListener(status)
                RegionsClickListener(status)
                SimpleOutlinedText(myTestTag = editableText)
                SwipeableNode(status)
                DisabledButton()
                LinearProgressBar(statusState = status)
                RadioGroup()
            }
        }
    }
}

enum class ActionsStatus {
    LongClicked, DoubleClicked, Clicked, SwipeDown, SwipeUp, SwipeRight, SwipeLeft
}

@Composable
fun CheckBox(title: String, testTagValue: String, statusState: MutableState<String>) {
    val state = remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = state.value,
            onCheckedChange = { state.value = it },
            enabled = true,
            colors = CheckboxDefaults.colors(Color.Green),
            modifier = Modifier
                .semantics {
                    testTag = testTagValue
                    stateDescription = "default"
                }
                .toggleable(state.value, true, onValueChange = { newValue -> statusState.value = "toggleable state $newValue" })
        )
        Text(text = title, modifier = Modifier.padding(16.dp))
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ButtonWithCount() {
    var count = remember {
        mutableStateOf(0)
    }
    Button(onClick = { count.value++ }, modifier = Modifier
        .semantics {
            testTag = likesCounterButton
            contentDescription = likesCounterContentDesc
        }) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = null
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text("Like count = ${count.value}",
            modifier = Modifier.semantics {
                testTag = likesCounterTextContainer
                contentDescription = likesCounterTextContainerContentDesc
            }
        )
    }
}

@Preview
@Composable
fun ButtonWithCountPreview(){
    ButtonWithCount()
}

@Composable
fun ClickListener(statusState: MutableState<String>) {
    CustomButton(
        onClickAction = { statusState.value = ActionsStatus.Clicked.name },
        onLongClick = { statusState.value = ActionsStatus.LongClicked.name },
        onDoubleClick = { statusState.value = ActionsStatus.DoubleClicked.name },
        modifier = Modifier
            .semantics {
                testTag = clickListenerButton
                contentDescription = clickListenerButton
            }
    ) {
        Text(text = "Click listener button")
    }
}

@Composable
fun DisabledButton(){
    Button(modifier = Modifier.semantics {
        testTag = disabledButton
    },
        onClick = {},
        content = { Text(text = "Disabled button")},
        enabled = false
    )
}


