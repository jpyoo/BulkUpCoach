package com.example.bulkupcoach.api

import android.util.Log
import com.example.bulkupcoach.model.ProteinInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException

class ProteinApiService(private val apiKey: String) {
    private val client = OkHttpClient()

    suspend fun fetchNutritionInfo(query: String): List<ProteinInfo> {
        val url = "https://api.api-ninjas.com/v1/nutrition?query=$query"

        val request = Request.Builder()
            .url(url)
            .header("X-Api-Key", apiKey)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseData = response.body?.string()
                if (responseData != null) {
                    Log.d("fetchNutritionInfo", "Response: $responseData") // Logging the API response
                    parseResponse(responseData)
                } else {
                    emptyList()
                }
            } catch (e: IOException) {
                Log.e("fetchNutritionInfo", "Error fetching nutrition info: ${e.message}", e) // Logging errors
                // Handle network errors
                emptyList()
            }
        }
    }

    private fun parseResponse(responseData: String): List<ProteinInfo> {
        val proteinInfoList = mutableListOf<ProteinInfo>()
        val jsonArray = JSONArray(responseData)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val name = jsonObject.getString("name")
            val calories = jsonObject.getDouble("calories")
            val protein = jsonObject.getDouble("protein_g")
            val servingSize = jsonObject.getDouble("serving_size_g")
            val proteinInfo = ProteinInfo(name, calories, servingSize, protein)
            proteinInfoList.add(proteinInfo)
        }
        return proteinInfoList
    }
}