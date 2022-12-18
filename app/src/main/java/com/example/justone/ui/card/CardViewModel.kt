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

    fun getCurrentCardIndex()
    {
        currentCardText.value = currentCardText.value
    }

    // next step is to fill the list with values for each card on startup or on a
    // mix button, each card should be possible to be reshuffled (maybe with limit 3 or
    // something like that. But for testing we need full support.
    // At button push all 13 cards should be set within the app!!!
    // -> consider accessing them within the fragment matching the index of currentCard!


}