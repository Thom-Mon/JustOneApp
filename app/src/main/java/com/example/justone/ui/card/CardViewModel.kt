package com.example.justone.ui.card

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CardViewModel : ViewModel() {

    var currentCardText = MutableLiveData<Int>()

    init
    {
        currentCardText.value = 4;
    }
    private val _text = MutableLiveData<String>().apply {
        value = "This is Card Fragment"
    }


    val text: LiveData<String> = _text


    fun updateCardIndexPositive()
    {
        currentCardText.value = (currentCardText.value)?.plus(1)
    }

    fun updateCardIndexNegative()
    {
        currentCardText.value = (currentCardText.value)?.minus(1)
    }



}