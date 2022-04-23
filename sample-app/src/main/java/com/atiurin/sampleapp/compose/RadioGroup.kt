package com.atiurin.sampleapp.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import com.atiurin.sampleapp.activity.ComposeElementsActivity

@Composable
fun RadioGroup(){
    var selected by remember { mutableStateOf("Male") }
    Row (verticalAlignment = Alignment.CenterVertically){
        val male = "Male"
        val female = "Female"
        RadioButton(selected = selected == male, onClick = { selected = male }, modifier = Modifier
            .selectableGroup()
            .semantics { testTag = ComposeElementsActivity.radioButtonMaleTestTag })
        Text(
            text = male,
            modifier = Modifier
                .clickable(onClick = { selected = male })
                .padding(start = 4.dp)
        )
        Spacer(modifier = Modifier.size(4.dp))

        RadioButton(selected = selected == female, onClick = { selected = female }, modifier = Modifier
            .selectableGroup()
            .semantics { testTag = ComposeElementsActivity.radioButtonFemaleTestTag })
        Text(
            text = female,
            modifier = Modifier
                .clickable(onClick = { selected = female })
                .padding(start = 4.dp)
        )
    }
}
