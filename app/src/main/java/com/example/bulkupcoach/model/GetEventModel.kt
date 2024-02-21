package com.example.bulkupcoach.model

import com.google.api.client.util.DateTime

data class GetEventModel(
    var id: Int = 0,
    var summary: String? = "",
    var startDate: DateTime? = null
)
