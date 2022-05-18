package com.appNutrity.nutrity.ui.calorias

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CaloriasViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is calorias Fragment"
    }
    val text: LiveData<String> = _text
}