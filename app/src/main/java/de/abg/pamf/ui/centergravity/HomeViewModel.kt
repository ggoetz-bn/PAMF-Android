package de.abg.pamf.ui.centergravity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is center gravity Fragment"
    }
    val text: LiveData<String> = _text
}