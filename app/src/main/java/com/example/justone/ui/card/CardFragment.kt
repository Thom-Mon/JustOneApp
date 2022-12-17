package com.example.justone.ui.card

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import com.example.justone.R
import com.example.justone.databinding.FragmentCardBinding
import com.example.justone.databinding.FragmentGalleryBinding
import com.example.justone.ui.gallery.GalleryViewModel

class CardFragment : Fragment() {

    private var _binding: FragmentCardBinding? = null
    private var cardIndex: Int = 0;

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
        }

        binding.btnArrowNext.setOnClickListener{
            if(cardViewModel.currentCardText.value!! < 13)
            {
                cardViewModel.updateCardIndexPositive()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}