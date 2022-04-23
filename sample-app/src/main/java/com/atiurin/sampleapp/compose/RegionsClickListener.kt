package com.atiurin.sampleapp.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import com.atiurin.sampleapp.compose.RegionsClickListenerTestTags.regionsClickedText
import com.atiurin.sampleapp.compose.RegionsClickListenerTestTags.regionsNode

object RegionsClickListenerTestTags {
    const val regionsNode = "regionsClickListener"
    const val regionsClickedText = "regionsClickedText"
}
@Composable
fun RegionsClickListener(clickState: MutableState<String>) {

    Column(modifier = Modifier.semantics { testTag = regionsNode }) {
        Row (Modifier.defaultMinSize(minHeight = 1.dp).padding(vertical = 0.dp)){//top
            Button(modifier = Modifier.padding(0.dp),onClick = { clickState.value = RegionName.TopLeft.name }) {
                Text(text = "TL")
            }
            Button(modifier = Modifier.padding(0.dp),onClick = { clickState.value = RegionName.TopCenter.name }) {
                Text(text = "TC")
            }
            Button(modifier = Modifier.padding(0.dp),onClick = { clickState.value = RegionName.TopRight.name }) {
                Text(text = "TR")
            }
        }
        Row (Modifier.defaultMinSize(minHeight = 1.dp).padding(vertical = 0.dp)){//Center
            Button(onClick = { clickState.value = RegionName.CenterLeft.name }) {
                Text(text = "CL")
            }
            Button(onClick = { clickState.value = RegionName.Center.name }) {
                Text(text = "CC")
            }
            Button(onClick = { clickState.value = RegionName.CenterRight.name }) {
                Text(text = "CR")
            }
        }
        Row (Modifier.padding(vertical = 0.dp)){//Bottom
            Button(modifier = Modifier.padding(0.dp), onClick = { clickState.value = RegionName.BottomLeft.name }) {
                Text(text = "BL")
            }
            Button(onClick = { clickState.value = RegionName.BottomCenter.name }) {
                Text(text = "BC")
            }
            Button(onClick = { clickState.value = RegionName.BottomRight.name }) {
                Text(text = "BR")
            }
        }
    }

}

enum class RegionName {
    TopLeft, TopCenter, TopRight, CenterLeft, Center, CenterRight, BottomLeft, BottomCenter, BottomRight
}