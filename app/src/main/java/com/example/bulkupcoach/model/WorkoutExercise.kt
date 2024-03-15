package com.example.bulkupcoach.model

data class WorkoutExercise(
    val exerciseType: String,
    val repetitions: Int,
    val resistanceType: String,
    val resistance: Float,
    val duration: Long
)
