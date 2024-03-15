package com.example.bulkupcoach.ui.fitness

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessActivities
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.data.Session
import com.google.android.gms.fitness.request.SessionInsertRequest
import com.google.android.gms.fitness.request.SessionReadRequest
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class FitnessSessionHelper(private val context: Context) {
    private fun dumpSession(session: Session) {
        Log.i(TAG, "Session name: ${session.name}")
        Log.i(TAG, "Session identifier: ${session.identifier}")
        Log.i(TAG, "Session description: ${session.description}")
        Log.i(TAG, "Session activity: ${session.activity}")
        Log.i(TAG, "Session start time: ${session.getStartTime(TimeUnit.MILLISECONDS)}")
        Log.i(TAG, "Session end time: ${session.getEndTime(TimeUnit.MILLISECONDS)}")
    }

    // Function to subscribe to fitness data
    fun subscribeToFitnessData(dataType: DataType) {
        Fitness.getRecordingClient(context, GoogleSignIn.getAccountForExtension(context, fitnessOptions))
            .subscribe(dataType)
            .addOnSuccessListener {
                Log.i(ContentValues.TAG, "Successfully subscribed to $dataType")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "There was a problem subscribing to $dataType", e)
            }
    }

    // Function to start a fitness session
    fun startFitnessSession(sessionName: String, startTime: Long) {
        // Create a session object
        val session = Session.Builder()
            .setName(sessionName)
            .setIdentifier("UniqueIdentifierHere")
            .setDescription("Morning run")
            .setActivity(FitnessActivities.RUNNING)
            .setStartTime(startTime, TimeUnit.MILLISECONDS)
            .build()

        // Use the Sessions client to start a session
        Fitness.getSessionsClient(context, GoogleSignIn.getAccountForExtension(context, fitnessOptions))
            .startSession(session)
            .addOnSuccessListener {
                Log.i(ContentValues.TAG, "Session started successfully!")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "There was an error starting the session", e)
            }
    }

    // Function to stop a fitness session
    fun stopFitnessSession(session: Session) {
        Fitness.getSessionsClient(context, GoogleSignIn.getAccountForExtension(context, fitnessOptions))
            .stopSession(session.getIdentifier())
            .addOnSuccessListener {
                Log.i(ContentValues.TAG, "Session stopped successfully!")
                // Now unsubscribe from the fitness data
                unsubscribeFromFitnessData(DataType.TYPE_STEP_COUNT_DELTA)
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "There was an error stopping the session", e)
            }
    }

    // Function to unsubscribe from fitness data
    fun unsubscribeFromFitnessData(dataType: DataType) {
        Fitness.getRecordingClient(context, GoogleSignIn.getAccountForExtension(context, fitnessOptions))
            .unsubscribe(dataType)
            .addOnSuccessListener {
                Log.i(ContentValues.TAG, "Successfully unsubscribed from $dataType")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Failed to unsubscribe from $dataType", e)
                // Retry the unsubscribe request if needed
            }
    }

    // Function to insert a fitness session
    fun insertFitnessSession(sessionName: String, startTime: Long, endTime: Long) {
        // Create a session with metadata about the activity
        val session = Session.Builder()
            .setName(sessionName)
            .setIdentifier("UniqueIdentifierHere")
            .setDescription("Long run around Shoreline Park")
            .setActivity(FitnessActivities.RUNNING)
            .setStartTime(startTime, TimeUnit.MILLISECONDS)
            .setEndTime(endTime, TimeUnit.MILLISECONDS)
            .build()

        // Build a session insert request
        val insertRequest = SessionInsertRequest.Builder()
            .setSession(session)
            .build()

        // Insert the session
        Fitness.getSessionsClient(context, GoogleSignIn.getAccountForExtension(context, fitnessOptions))
            .insertSession(insertRequest)
            .addOnSuccessListener {
                Log.i(ContentValues.TAG, "Session insert was successful!")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "There was a problem inserting the session: ", e)
            }
    }

    // Function to read fitness data using sessions
    fun readFitnessDataUsingSessions() {
        // Use a start time of 1 week ago and an end time of now
        val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val startTime = endTime.minusWeeks(1)

        // Build a session read request
        val readRequest = SessionReadRequest.Builder()
            .setTimeInterval(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
            .read(DataType.TYPE_SPEED)
            .setSessionName("SAMPLE_SESSION_NAME")
            .build()

        // Read sessions
        Fitness.getSessionsClient(context, GoogleSignIn.getAccountForExtension(context, fitnessOptions))
            .readSession(readRequest)
            .addOnSuccessListener { response ->
                // Get a list of the sessions that match the criteria to check the result
                val sessions = response.sessions
                Log.i(ContentValues.TAG, "Number of returned sessions is: ${sessions.size}")
                for (session in sessions) {
                    // Process the session
                    dumpSession(session)

                    // Process the data sets for this session
                    val dataSets = response.getDataSet(session)
                    for (dataSet in dataSets) {
                        // Process the data set
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Failed to read session", e)
            }
    }

    fun insertWorkoutExercise(session: Session, exerciseType: Int, repetitions: Int, resistanceType: Int, resistance: Float, duration: Int) {
        val dataSource = DataSource.Builder()
            .setAppPackageName(context.packageName)
            .setDataType(DataType.TYPE_WORKOUT_EXERCISE)
            .setType(DataSource.TYPE_RAW)
            .build()

        val exerciseDataPoint = DataPoint.builder(dataSource)
            .setField(Field.FIELD_EXERCISE, exerciseType)
            .setField(Field.FIELD_REPETITIONS, repetitions)
            .setField(Field.FIELD_RESISTANCE_TYPE, resistanceType)
            .setField(Field.FIELD_RESISTANCE, resistance)
            .setField(Field.FIELD_DURATION, duration)
            .build()

        val dataSet = DataSet.builder(dataSource)
            .add(exerciseDataPoint)
            .build()

        val request = SessionInsertRequest.Builder()
            .addDataSet(dataSet)
            .setSession(session)
            .build()

        Fitness.getSessionsClient(context, GoogleSignIn.getAccountForExtension(context, fitnessOptions))
            .insertSession(request)
            .addOnSuccessListener { Log.i(TAG, "Workout exercise inserted successfully!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error inserting workout exercise", e) }
    }



}
