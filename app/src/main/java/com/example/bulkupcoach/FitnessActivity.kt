package com.example.bulkupcoach

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.SessionInsertRequest
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import java.util.concurrent.TimeUnit

class WorkoutActivity : AppCompatActivity() {

    private lateinit var exerciseSpinner: Spinner
    private lateinit var resistanceSpinner: Spinner
    private lateinit var repetitionsEditText: EditText
    private lateinit var resistanceEditText: EditText
    private lateinit var insertButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fitness)

        exerciseSpinner = findViewById(R.id.exerciseSpinner)
        resistanceSpinner = findViewById(R.id.resistanceSpinner)
        repetitionsEditText = findViewById(R.id.repetitionsEditText)
        resistanceEditText = findViewById(R.id.resistanceEditText)
        insertButton = findViewById(R.id.insertButton)
        // Set up spinners
        ArrayAdapter.createFromResource(
            this,
            R.array.exercise_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            exerciseSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.resistance_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            resistanceSpinner.adapter = adapter
        }

        // Set click listener for insert button
        insertButton.setOnClickListener {
            // Get selected exercise type and resistance type
            val exerciseType = exerciseSpinner.selectedItem.toString()
            val resistanceType = resistanceSpinner.selectedItem.toString()

            // Insert workout exercise
            insertWorkoutExercise(exerciseType, resistanceType)
        }
    }

    private fun insertWorkoutExercise(exerciseType: String, resistanceType: String) {
        // Get values from EditText fields
        val repetitions = repetitionsEditText.text.toString().toIntOrNull() ?: 0
        val resistance = resistanceEditText.text.toString().toFloatOrNull() ?: 0f

        // Example code to insert workout exercise data
        val request = SessionInsertRequest.Builder()
            .addDataSet(createWorkoutDataSet(exerciseType, resistanceType, repetitions, resistance))
            .build()

        // Fitness.getSessionsClient(context, googleSignInAccount)
        //     .insertSession(request)
        //     .addOnSuccessListener { Log.i(TAG, "Workout exercise inserted successfully!") }
        //     .addOnFailureListener { e -> Log.e(TAG, "Error inserting workout exercise", e) }
    }


    private fun createWorkoutDataSet(
        exerciseType: String,
        resistanceType: String,
        repetitions: Int,
        resistance: Float
    ): DataSet {
        // Create a DataSource for workout exercise data
        val dataSource = DataSource.Builder()
            .setAppPackageName(this.packageName)
            .setDataType(DataType.TYPE_WORKOUT_EXERCISE)
            .setType(DataSource.TYPE_RAW)
            .build()

        // Create a DataPoint for workout exercise
        val exerciseDataPoint = DataPoint.builder(dataSource)
            .setField(Field.FIELD_EXERCISE, exerciseType)
            .setField(Field.FIELD_REPETITIONS, repetitions)
            .setField(Field.FIELD_RESISTANCE_TYPE, resistanceType)
            .setField(Field.FIELD_RESISTANCE, resistance)
            .build()

        // Create a DataSet for workout exercise
        return DataSet.builder(dataSource)
            .add(exerciseDataPoint)
            .build()
    }

}


