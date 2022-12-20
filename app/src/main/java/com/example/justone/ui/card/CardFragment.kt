package com.example.justone.ui.card

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
import com.example.justone.database.AppDatabase
import com.example.justone.database.Card
import com.example.justone.databinding.FragmentCardBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CardFragment : Fragment() {

    private var _binding: FragmentCardBinding? = null
    private var cardIndex: Int = 0;
    private  lateinit var appDb : AppDatabase
    private val gson = Gson()
    private var randomList: MutableList<Int> = mutableListOf(0,0)
    private var wordList: MutableList<String> = mutableListOf("0")

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

        val textView: TextView = binding.textCard
        val currentCardLabel: TextView = binding.labelCurrentCard
        appDb = AppDatabase.getDatabase(requireContext())

        cardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        cardViewModel.currentCardText.observe(viewLifecycleOwner) {
            currentCardLabel.text = it.toString()
        }
        cardViewModel.wordList.observe(viewLifecycleOwner){
            //binding.card2Button.text = it.toString()
        }

        // Button Listener
        binding.btnDeleteDb.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO){
                appDb.cardDao().deleteAll();
            }
        }

        binding.btnLoadDb.setOnClickListener{
            // example for loading from asset-folder as json-string to DB
            var loadedData = ArrayList<Card>()
            val file = context?.assets?.open("words.txt")?.bufferedReader()
            val fileContents = file?.readText()
            val arrayListTutorialType = object : TypeToken<List<Card>>() {}.type
            loadedData = gson.fromJson(fileContents, arrayListTutorialType)

            GlobalScope.launch(Dispatchers.IO){
                // write contents from JSON-String to DB
                appDb.cardDao().insertAll(loadedData)
                Log.e("LastEntry",appDb.cardDao().getSequenceNumber("card_table").toString())
            }
        }

        binding.btnArrowBack.setOnClickListener {
            if(cardViewModel.currentCardText.value!! > 1)
            {
                cardViewModel.updateCardIndexNegative()
                animationSlideInText(binding.labelCurrentCard,true)
            }
            setCardText(cardViewModel.currentCardText.value!!)
        }

        binding.btnArrowNext.setOnClickListener{
            if(cardViewModel.currentCardText.value!! < 13)
            {
                cardViewModel.updateCardIndexPositive()
                animationSlideInText(binding.labelCurrentCard)
            }
            setCardText(cardViewModel.currentCardText.value!!)

        }

        binding.btnShuffleAllCard.setOnClickListener {
            shuffleAllCards(cardViewModel)
        }

        // set the card words on start as shuffled
        if(cardViewModel.wordList.value?.size!! > 1)
        {
            // if there is data within the viewModel-wordList -> save it to fragment wordList
            wordList = cardViewModel.wordList.value as MutableList<String>
            //val currentCardIndex = Integer.parseInt(binding.labelCurrentCard.text as String)
            setCardText(cardViewModel.currentCardText.value!!)
        }
        else
        {
            binding.card3Button.text = "----------" //DEBUG
        }

        return root
    }

    private fun shuffleAllCards(cardViewModel: CardViewModel) {
        // clear wordList to fill with new Words
        wordList.clear();
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