package com.example.justone.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.justone.MainActivity
import com.example.justone.database.AppDatabase
import com.example.justone.database.Card
import com.example.justone.databinding.FragmentCardBinding
import com.example.justone.databinding.FragmentHomeBinding
import com.example.justone.ui.card.CardFragment
import com.example.justone.ui.card.CardViewModel
import com.google.gson.Gson

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private  lateinit var appDb : AppDatabase
    private val gson = Gson()
    private var wordList: MutableList<String> = mutableListOf("0")

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        appDb = AppDatabase.getDatabase(requireContext())

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        binding.btnUseChristmas.setOnClickListener {
            val cardViewModel = ViewModelProvider(this).get(CardViewModel::class.java)

            binding.textViewResult.text = this.toString()
            getChristmasCards(cardViewModel)
        }

        binding.btnUseNormalSet.setOnClickListener {
            val cardViewModel = ViewModelProvider(this).get(CardViewModel::class.java)
            binding.textViewResult.text = cardViewModel.wordList.value?.get(0).toString()
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getChristmasCards(cardViewModel: CardViewModel)
    {
        wordList.clear()
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
        Log.i("Count", wordList[0])

        // current Card to set card text appropiately
        //val currentCardIndex = Integer.parseInt(binding.labelCurrentCard.text as String)

        // set the cards according to fragment wordlist
        //setCardText(currentCardIndex)
        // save fragment wordlist to viewmodel to persist data
        cardViewModel.addToWordList(wordList)

    }

    // helper function
    fun <T> shuffle(list: MutableList<T>) {
        list.shuffle()
    }
}