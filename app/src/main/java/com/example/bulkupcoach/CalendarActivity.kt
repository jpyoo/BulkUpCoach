package com.example.bulkupcoach

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.CalendarList
import com.google.api.services.calendar.model.Event
import java.io.IOException
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private lateinit var mCredential: GoogleAccountCredential
    private lateinit var listView: ListView
    private lateinit var events: MutableList<Event>
    private val SCOPES = listOf(CalendarScopes.CALENDAR_READONLY)
    private val REQUEST_PERMISSIONS = 1001
    private val TAG = "CalendarActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_calendar)

        mCredential = GoogleAccountCredential.usingOAuth2(
            applicationContext, SCOPES
        ).setBackOff(ExponentialBackOff())

        listView = findViewById(R.id.listView)
        events = mutableListOf()

        // Find and set click listener for the button
        val btnLoadEvents: Button = findViewById(R.id.btnLoadEvents)
        btnLoadEvents.setOnClickListener {
            checkPermissionsAndLoadData()
        }
    }


    private fun checkPermissionsAndLoadData() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.GET_ACCOUNTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.GET_ACCOUNTS),
                REQUEST_PERMISSIONS
            )
        } else {
            getCalendarData()
        }
    }

    private fun getCalendarData() {
        val transport = com.google.api.client.http.javanet.NetHttpTransport()
        val jsonFactory: com.google.api.client.json.JsonFactory =
            com.google.api.client.json.jackson2.JacksonFactory.getDefaultInstance()

        val service = Calendar.Builder(
            transport, jsonFactory, mCredential)
            .setApplicationName("BulkUpCoach")
            .build()

        try {
            val calendarList: CalendarList = service.calendarList().list().execute()

            for (calendar in calendarList.items) {
                Log.d(TAG, "Calendar: ${calendar.summary}")
            }

            val eventsResult = service.events().list("primary").execute()
            events.addAll(eventsResult.items)

            val eventSummaries = mutableListOf<String>()
            for (event in events) {
                eventSummaries.add(event.summary)
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, eventSummaries)
            listView.adapter = adapter
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching calendar data: ${e.message}")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCalendarData()
            } else {
                Log.e(TAG, "Permission denied.")
            }
        }
    }
}
