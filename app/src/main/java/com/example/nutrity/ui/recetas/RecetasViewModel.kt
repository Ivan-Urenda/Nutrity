package com.example.nutrity.ui.recetas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecetasViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is recetas Fragment"
    }
    val text: LiveData<String> = _text
}