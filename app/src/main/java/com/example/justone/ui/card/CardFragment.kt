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
import java.io.Reader


class CardFragment : Fragment() {

    private var _binding: FragmentCardBinding? = null
    private var cardIndex: Int = 0;
    private  lateinit var appDb : AppDatabase
    private val gson = Gson()

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
        binding.btnArrowBack.setOnClickListener {
            if(cardViewModel.currentCardText.value!! > 1)
            {
                cardViewModel.updateCardIndexNegative()
            }

            var loadedData = ArrayList<Card>()

            val file = context?.assets?.open("words.txt")?.bufferedReader()
            val fileContents = file?.readText()



            Log.e("Load",fileContents.toString())
            val arrayListTutorialType = object : TypeToken<List<Card>>() {}.type
            loadedData = gson.fromJson(fileContents, arrayListTutorialType)



            var card: Card
            GlobalScope.launch(Dispatchers.IO){
                //appDb.entryDao().insert(entry)
                card = appDb.cardDao().findById(1)
                binding.card1Button.text = card.word.toString();
                appDb.cardDao().insertAll(loadedData)
            }

        }

        binding.btnArrowNext.setOnClickListener{
            if(cardViewModel.currentCardText.value!! < 13)
            {
                cardViewModel.updateCardIndexPositive()
            }

            // DEBUG / TESTING
            /*val card = Card(null,"Testwort","Tagexample",4)

            GlobalScope.launch(Dispatchers.IO){
                //appDb.entryDao().insert(entry)
                appDb.cardDao().insert(card)
            }*/

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}