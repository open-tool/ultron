package com.atiurin.sampleapp.data.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.atiurin.sampleapp.data.entities.Contact

class DataViewModel : ViewModel(){
    val data: MutableLiveData<String> by lazy {
        MutableLiveData()
    }
}