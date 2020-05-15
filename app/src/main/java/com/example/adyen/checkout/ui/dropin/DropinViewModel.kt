package com.example.adyen.checkout.ui.dropin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DropinViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "TODO Remove"
    }
    val text: LiveData<String> = _text
}