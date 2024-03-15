package com.example.bulkupcoach.ui.fitness

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import android.content.Context
import com.google.android.gms.fitness.FitnessOptions

val fitnessOptions = FitnessOptions.builder()
    .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
    // Add more data types and access permissions as needed
    .build()

class FitnessViewModel(private val context: Context) : ViewModel() {

    private val fitnessSessionHelper = FitnessSessionHelper(context)

    // Function to subscribe to fitness data
    fun subscribeToFitnessData(dataType: DataType) {
        fitnessSessionHelper.subscribeToFitnessData(dataType)
    }

    // Function to unsubscribe from fitness data
    fun unsubscribeFromFitnessData(dataType: DataType) {
        fitnessSessionHelper.unsubscribeFromFitnessData(dataType)
    }
}

