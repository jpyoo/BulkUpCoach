package com.example.bulkupcoach.ui.protein

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bulkupcoach.api.ProteinApiService
import com.example.bulkupcoach.model.ProteinInfo
import kotlinx.coroutines.launch

class ProteinViewModel(private val proteinApiService: ProteinApiService) : ViewModel() {

    private val _proteinInfo = MutableLiveData<List<ProteinInfo>>()
    val proteinInfo: LiveData<List<ProteinInfo>>
        get() = _proteinInfo

    fun fetchNutritionInfo(query: String) {
        viewModelScope.launch {
            val result = proteinApiService.fetchNutritionInfo(query)
            _proteinInfo.value = result
        }
    }
}

class ProteinViewModelFactory(private val proteinApiService: ProteinApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProteinViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProteinViewModel(proteinApiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}




