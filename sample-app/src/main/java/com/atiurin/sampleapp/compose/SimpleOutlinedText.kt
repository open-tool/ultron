package com.atiurin.sampleapp.compose

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag

@Composable
fun SimpleOutlinedText(defaultValue: String = "",  myTestTag: String = "outlinedText") {
    var text by remember { mutableStateOf(defaultValue) }
    SelectionContainer {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Label") },
            modifier = Modifier.semantics { testTag = myTestTag },

        )
    }
}