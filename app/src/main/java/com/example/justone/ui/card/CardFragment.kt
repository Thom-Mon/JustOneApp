package com.example.justone.ui.card

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.justone.database.AppDatabase
import com.example.justone.database.Card
import com.example.justone.databinding.FragmentCardBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Reader


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
            }
            // current Card TODO: als FUnctuon unbedingt auslagern und optiimieren

            setCardText(cardViewModel.currentCardText.value!!)

            /*binding.card1Button.text = randomList[currentCardIndex*5-5].toString();
            binding.card2Button.text = randomList[currentCardIndex*5-4].toString();
            binding.card3Button.text = randomList[currentCardIndex*5-3].toString();
            binding.card4Button.text = randomList[currentCardIndex*5-2].toString();
            binding.card5Button.text = randomList[currentCardIndex*5-1].toString();*/
        }

        binding.btnArrowNext.setOnClickListener{
            if(cardViewModel.currentCardText.value!! < 13)
            {
                cardViewModel.updateCardIndexPositive()
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

    private fun shuffleAllCards()
    {
        randomList = (0..1500).shuffled().take(65) as MutableList<Int>
        //wordList = create a list of the words from the randomList

        var card: Card

            GlobalScope.launch(){
                for (number in randomList)
                {
                    card = appDb.cardDao().findById(number)
                }
            }
        //TODO: get from DB the word according to the random int, do not delete DB while test
        // current Card
        val currentCardIndex = Integer.parseInt(binding.labelCurrentCard.text as String)

        setCardText(currentCardIndex)
    }

    private fun shuffleSingleCard()
    {

    }

    private fun setCardText(currentCardIndex : Int)
    {
        binding.card1Button.text = randomList[currentCardIndex*5-5].toString();
        binding.card2Button.text = randomList[currentCardIndex*5-4].toString();
        binding.card3Button.text = randomList[currentCardIndex*5-3].toString();
        binding.card4Button.text = randomList[currentCardIndex*5-2].toString();
        binding.card5Button.text = randomList[currentCardIndex*5-1].toString();
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}