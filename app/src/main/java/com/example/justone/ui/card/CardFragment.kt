package com.example.justone.ui.card

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.example.justone.R
import com.example.justone.database.AppDatabase
import com.example.justone.database.Card
import com.example.justone.databinding.FragmentCardBinding
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson


class CardFragment : Fragment() {

    private var _binding: FragmentCardBinding? = null
    private var cardIndex: Int = 0;
    private  lateinit var appDb : AppDatabase
    private val gson = Gson()
    private var randomList: MutableList<Int> = mutableListOf(0,0)
    private var wordList: MutableList<String> = mutableListOf("0")
    private var chosenCard:  MutableList<Int> = mutableListOf(5,5,5,5,5,5,5,5,5,5,5,5,5,5)
    private var resultList: MutableList<Boolean> = mutableListOf()
    private var cardButtons = arrayOf<MaterialButton>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val cardViewModel =
            ViewModelProvider(this).get(CardViewModel::class.java)

        _binding = FragmentCardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textCard
        val currentCardLabel: TextView = binding.labelCurrentCard
        appDb = AppDatabase.getDatabase(requireContext())

        cardViewModel.text.observe(viewLifecycleOwner) {
            //textView.text = it
        }
        cardViewModel.currentCardText.observe(viewLifecycleOwner) {
            currentCardLabel.text = it.toString()
        }
        cardViewModel.wordList.observe(viewLifecycleOwner){
            //binding.card2Button.text = it.toString()
        }

        //CORRECT AND WRONG BUTTON
        binding.btnCorrect.setOnClickListener {
            setCardResult(true, cardViewModel)
        }
        binding.btnWrong.setOnClickListener {
            setCardResult(false, cardViewModel)
        }

        binding.btnShuffleChristmasCards.setOnClickListener {
            getChristmasCards(cardViewModel)
            binding.btnShuffleChristmasCards.visibility = View.INVISIBLE
            binding.btnShuffleAllCard.visibility = View.INVISIBLE
            binding.btnCancelShuffle.visibility = View.INVISIBLE
            binding.darkenedBackground.visibility = View.INVISIBLE

        }

        binding.btnSettings.setOnClickListener {
            binding.btnShuffleChristmasCards.visibility = View.VISIBLE
            binding.btnShuffleAllCard.visibility = View.VISIBLE
            binding.btnCancelShuffle.visibility = View.VISIBLE
            binding.darkenedBackground.visibility = View.VISIBLE
        }

        // getting all Buttons and add a listener to them
        cardButtons = arrayOf<MaterialButton>(
                                                binding.card1Button,
                                                binding.card2Button,
                                                binding.card3Button,
                                                binding.card4Button,
                                                binding.card5Button)

        for((index,cardButton) in cardButtons.withIndex())
        {
            cardButton.setOnClickListener {
                cardViewModel.updateChosenCards(chosenCard)
                setCardColor(cardViewModel.currentCardText.value!!,index, cardViewModel, cardButton)
            }
        }

        binding.btnArrowBack.setOnClickListener {
            if(cardViewModel.currentCardText.value!! > 1)
            {
                cardViewModel.updateCardIndexNegative()
                animationSlideInText(binding.labelCurrentCard,true)
                if(cardViewModel.currentCardText.value!!-1 in resultList.indices)
                {
                    binding.btnArrowNext.visibility = View.VISIBLE
                }
            }
            chosenCard = cardViewModel.chosenCardIndex.value as MutableList<Int>
            setCardText(cardViewModel.currentCardText.value!!)
            setCardColors(cardViewModel.currentCardText.value!!)
            showCardResult(cardViewModel.currentCardText.value!!,cardViewModel)
        }

        binding.btnArrowNext.setOnClickListener{
            if(cardViewModel.currentCardText.value!! < 13)
            {
                cardViewModel.updateCardIndexPositive()
                animationSlideInText(binding.labelCurrentCard)
                if(cardViewModel.currentCardText.value!!-1 !in resultList.indices)
                {
                    binding.btnArrowNext.visibility = View.INVISIBLE
                }
            }
            chosenCard = cardViewModel.chosenCardIndex.value as MutableList<Int>
            setCardText(cardViewModel.currentCardText.value!!)
            setCardColors(cardViewModel.currentCardText.value!!)
            showCardResult(cardViewModel.currentCardText.value!!,cardViewModel)
        }

        binding.btnShuffleAllCard.setOnClickListener {
            shuffleAllCards(cardViewModel)
            binding.btnShuffleChristmasCards.visibility = View.INVISIBLE
            binding.btnShuffleAllCard.visibility = View.INVISIBLE
            binding.btnCancelShuffle.visibility = View.INVISIBLE
            binding.darkenedBackground.visibility = View.INVISIBLE

        }

        binding.btnCancelShuffle.setOnClickListener {
            binding.btnShuffleChristmasCards.visibility = View.INVISIBLE
            binding.btnShuffleAllCard.visibility = View.INVISIBLE
            binding.btnCancelShuffle.visibility = View.INVISIBLE
            binding.darkenedBackground.visibility = View.INVISIBLE
        }
        // set the card words on start as shuffled
        if(cardViewModel.wordList.value?.size!! > 1)
        {
            // if there is data within the viewModel-wordList -> save it to fragment wordList
            wordList = cardViewModel.wordList.value as MutableList<String>
            chosenCard = cardViewModel.chosenCardIndex.value as MutableList<Int>
            resultList = cardViewModel.cardResult.value as MutableList<Boolean>
            //val currentCardIndex = Integer.parseInt(binding.labelCurrentCard.text as String)
            setCardText(cardViewModel.currentCardText.value!!)
            setCardColors(cardViewModel.currentCardText.value!!)
            showCardResult(cardViewModel.currentCardText.value!!, cardViewModel)
            Log.i("chosenSize After", chosenCard.size.toString())
        }
        else
        {
            binding.card3Button.text = "----------" //DEBUG
            Log.i("chosenSize Before", chosenCard.size.toString())
        }

        return root
    }

    private fun showCardResult(value: Int, cardViewModel: CardViewModel)
    {
        if(cardViewModel.currentCardText.value!!-1 !in resultList.indices)
        {
            binding.backgroundCards.setImageResource(R.drawable.cards_background)
            return
        }
        if(resultList[cardViewModel.currentCardText.value!!-1])
        {
            binding.backgroundCards.setImageResource(R.drawable.cards_background_right)
        }
        else
        {
            binding.backgroundCards.setImageResource(R.drawable.cards_background_wrong)
        }
    }

    private fun setCardResult(cardResult: Boolean, cardViewModel: CardViewModel) {
        Log.i("result", "BEF: " + resultList.size.toString())

        if(cardViewModel.currentCardText.value!!-1 in resultList.indices)
        {
            resultList[cardViewModel.currentCardText.value!!-1] = cardResult
        }
        else
        {
            resultList.add(cardResult)
            binding.btnArrowNext.visibility = View.VISIBLE
        }

        // if true then give card design a greener touch if false then more redish
        if(resultList[cardViewModel.currentCardText.value!!-1])
        {
            binding.backgroundCards.setImageResource(R.drawable.cards_background_right)
        }
        else
        {
            binding.backgroundCards.setImageResource(R.drawable.cards_background_wrong)
        }

        cardViewModel.updateCardResult(resultList)
    }


    private fun getChristmasCards(cardViewModel: CardViewModel)
    {
        wordList.clear();
        var cards: List<Card>

        val christmasThread = Thread {
            cards = appDb.cardDao().getAllByTags("Weihnachten")
            val cards_count = appDb.cardDao().getTagCount("Weihnachten")
            Log.i("COunt", cards_count.toString())

            for (i in 0..cards_count-1)
            {
                wordList.add(cards[i].word.toString())
            }
        }
        christmasThread.start()

        // TODO: Maybe there is a better way to update the cards but I did not find it yet
        // Thread.sleep will become unnecessary if the shuffle all cards is in another fragment
        Thread.sleep(500)

        shuffle(wordList)

        // current Card to set card text appropiately
        //val currentCardIndex = Integer.parseInt(binding.labelCurrentCard.text as String)

        // save fragment wordlist to viewmodel to persist data
        cardViewModel.addToWordList(wordList)
        resetGame(cardViewModel)
    }

    // helper function
    fun <T> shuffle(list: MutableList<T>) {
        list.shuffle()
    }

    private fun shuffleAllCards(cardViewModel: CardViewModel) {
        // clear wordList to fill with new Words
        wordList.clear();

        // need to get the size of the roomdb here! from thread


        // get randomList from cards avaiable and  take at least 80
        randomList = (0..1500).shuffled().take(80) as MutableList<Int>

        var cards: List<Card>

        val thread = Thread {
            cards = appDb.cardDao().findByIds(randomList)

            for (i in 1..65)
            {
                wordList.add(cards[i].word.toString())
            }
        }
        thread.start()

        // TODO: Maybe there is a better way to update the cards but I did not find it yet
        // Thread.sleep will become unnecessary if the shuffle all cards is in another fragment
        Thread.sleep(500)

        // current Card to set card text appropiately
        val currentCardIndex = Integer.parseInt(binding.labelCurrentCard.text as String)

        // set the cards according to fragment wordlist
        setCardText(currentCardIndex)
        // save fragment wordlist to viewmodel to persist data
        cardViewModel.addToWordList(wordList)
    }

    private fun shuffleSingleCard()
    {

    }

    private fun setCardText(currentCardIndex : Int)
    {
        binding.card1Button.text = wordList[currentCardIndex*5-5].toString();
        binding.card2Button.text = wordList[currentCardIndex*5-4].toString();
        binding.card3Button.text = wordList[currentCardIndex*5-3].toString();
        binding.card4Button.text = wordList[currentCardIndex*5-2].toString();
        binding.card5Button.text = wordList[currentCardIndex*5-1].toString();
    }

    private fun setCardColors(currentCardIndex: Int)
    {
        for(cardBtn in cardButtons)
        {
            cardBtn.setBackgroundColor(Color.WHITE)
            cardBtn.setTextColor(Color.BLACK)
        }

        if(chosenCard[currentCardIndex-1] == 5)
        {
            return
        }
        cardButtons[chosenCard[currentCardIndex-1]].setBackgroundColor(resources.getColor(R.color.color_selected_card))
        cardButtons[chosenCard[currentCardIndex-1]].setTextColor(Color.WHITE)

    }

    private fun setCardColor(currentCardIndex: Int,index: Int,cardViewModel: CardViewModel,cardButton: MaterialButton)
    {
        cardButton.setBackgroundColor(resources.getColor(R.color.color_selected_card))
        cardButton.setTextColor(Color.WHITE)

        for(cardBtn in cardButtons)
        {
            if(cardBtn == cardButton) {continue}
            cardBtn.setBackgroundColor(Color.WHITE)
            cardBtn.setTextColor(Color.BLACK)
        }

        chosenCard[currentCardIndex-1] = index
        cardViewModel.updateChosenCards(chosenCard)
    }

    private fun resetGame(cardViewModel: CardViewModel)
    {
        setCardText(1)
        cardViewModel.resetCardResults()
        chosenCard = cardViewModel.chosenCardIndex.value as MutableList<Int>
        resultList = cardViewModel.cardResult.value as MutableList<Boolean>

    }


    private fun animationSlideInText(textView: TextView, fromLeft: Boolean = false, duration: Long = 250)
    {
        textView.visibility = View.INVISIBLE
        binding.group.visibility = View.INVISIBLE

        val mSlide = Slide()
        mSlide.duration = duration

        if(fromLeft)
        {
            mSlide.slideEdge = Gravity.LEFT
        }
        else
        {
            mSlide.slideEdge = Gravity.RIGHT
        }

        TransitionManager.beginDelayedTransition((view as ViewGroup?)!!, mSlide)
        textView.visibility = View.VISIBLE
        binding.group.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}