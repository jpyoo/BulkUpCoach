package com.example.bulkupcoach.ui.protein

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bulkupcoach.databinding.FragmentProteinBinding
import androidx.lifecycle.ViewModelProvider
import com.example.bulkupcoach.api.ProteinApiService

class ProteinFragment : Fragment() {

    private lateinit var viewModel: ProteinViewModel
    private lateinit var binding: FragmentProteinBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProteinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiKey = "CAKsIPzc5q86zPsuAqZUhA==zKjvLZZmaEBj15Aj"
        val apiService = ProteinApiService(apiKey)
        viewModel = ViewModelProvider(this, ProteinViewModelFactory(apiService)).get(ProteinViewModel::class.java)

        binding.btnGetProtein.setOnClickListener {
            val query = binding.etFoodQuery.text.toString()
            viewModel.fetchNutritionInfo(query)
        }

        viewModel.proteinInfo.observe(viewLifecycleOwner) { proteinInfo ->
            // Update UI with proteinInfo
            val infoText = proteinInfo.joinToString("\n") { info ->
                "Name: ${info.name}, servingSize: ${info.servingSize}, Protein: ${info.protein}"
            }
            binding.tvProteinInfo.text = infoText
            Log.d("ProteinFragment", "Response: $infoText")
        }
    }
}


