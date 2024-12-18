package com.stukalov.staffairlines.pro.ui.sel_point

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SelPointViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is sel point Fragment"
    }
    val text: LiveData<String> = _text
}