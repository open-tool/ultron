package com.atiurin.sampleapp.compose.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import com.atiurin.sampleapp.compose.app.AppScreen

object NavigationTestTags {
    const val DatePicker = "DatePicker"
}
@Composable
fun NavigationScreen(
    onNavButtonClicked: (AppScreen) -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        NavButton("DatePicker", NavigationTestTags.DatePicker){
            onNavButtonClicked(AppScreen.DataPicker)
        }
    }
}

@Composable
fun NavButton(name: String, testId: String, onClick: () -> Unit){
    Button(modifier = Modifier.semantics {
        testTag = testId
    },
        onClick = onClick,
        content = { Text(text = name) },
    )
}