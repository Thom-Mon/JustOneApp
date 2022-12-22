package com.example.justone.ui.card

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CardViewModel : ViewModel() {

    var currentCardText   =  MutableLiveData<Int>()
    var wordList          =  MutableLiveData<List<String>>() //= mutableListOf("1")
    var chosenCardIndex   =  MutableLiveData<List<Int>>()

    init
    {
        currentCardText.value = 4;
        wordList.value = ArrayList()
        chosenCardIndex.value = arrayListOf(5,5,5,5,5,5,5,5,5,5,5,5,5,5)
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

    fun addToWordList(word: MutableList<String>)
    {
        wordList.value = word
    }

    fun updateChosenCards(indexList: MutableList<Int>)
    {
        chosenCardIndex.value = indexList
    }

    // next step is to fill the list with values for each card on startup or on a
    // mix button, each card should be possible to be reshuffled (maybe with limit 3 or
    // something like that. But for testing we need full support.
    // At button push all 13 cards should be set within the app!!!
    // -> consider accessing them within the fragment matching the index of currentCard!


}