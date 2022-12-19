package com.example.justone.ui.card

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
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

            var card: Card
            GlobalScope.launch(Dispatchers.IO){
                //card = appDb.cardDao().findById(1)
                //binding.card1Button.text = card.word.toString();

                // write contents from JSON-String to DB
                appDb.cardDao().insertAll(loadedData)
                Log.e("LastEntry",appDb.cardDao().getSequenceNumber("card_table").toString())
                //card = appDb.cardDao().findById(2);
                //binding.textCard.text = card.id.toString()
            }
        }

        binding.btnArrowBack.setOnClickListener {
            if(cardViewModel.currentCardText.value!! > 1)
            {
                cardViewModel.updateCardIndexNegative()
                animationSlideInText(binding.labelCurrentCard,true)
            }
            // current Card TODO: als FUnctuon unbedingt auslagern und optiimieren

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
            shuffleAllCards()
        }

        // set the card words on start as shuffled
        if(randomList[0] == 0)
        {
            binding.card1Button.text = "Karten mischen!"
        }
        else
        {
            setCardText(cardViewModel.currentCardText.value!!)
        }

        return root
    }

    private fun shuffleAllCards() {
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
        setCardText(currentCardIndex)
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
    //fun <T: Any> getData(clazz: KClass<T>)

    private fun animationSlideInText(textView: TextView, fromLeft: Boolean = false, duration: Long = 500)
    {
        textView.visibility = View.INVISIBLE
        binding.card1Button.visibility = View.INVISIBLE
        binding.card2Button.visibility = View.INVISIBLE
        binding.card3Button.visibility = View.INVISIBLE
        binding.card4Button.visibility = View.INVISIBLE
        binding.card5Button.visibility = View.INVISIBLE

        val mSlide = Slide()
        mSlide.duration = duration

        if(fromLeft)
        {
            mSlide.slideEdge = Gravity.START
        }
        else
        {
            mSlide.slideEdge = Gravity.END
        }

        TransitionManager.beginDelayedTransition((view as ViewGroup?)!!, mSlide)
        textView.visibility = View.VISIBLE
        binding.card1Button.visibility = View.VISIBLE
        binding.card3Button.visibility = View.VISIBLE
        binding.card2Button.visibility = View.VISIBLE
        binding.card4Button.visibility = View.VISIBLE
        binding.card5Button.visibility = View.VISIBLE

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}