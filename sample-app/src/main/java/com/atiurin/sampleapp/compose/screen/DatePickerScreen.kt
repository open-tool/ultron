package com.atiurin.sampleapp.compose.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.atiurin.sampleapp.compose.DatePickerDocked

@Composable
fun DatePickerScreen() {
    Column {
        DatePickerDocked()

//        val modalDate = remember { mutableStateOf("No modal date selected") }
//        val showModal = remember { mutableStateOf(false) }
//        Text(modalDate.value)
//        if (showModal.value){
//            DatePickerModal({ date ->
//                date?.let { modalDate.value = convertMillisToDate(date) }
//                showModal.value = false
//            }) {
//                showModal.value = false
//            }
//        }
    }

}

@Composable
fun ShowModalButton(){

}