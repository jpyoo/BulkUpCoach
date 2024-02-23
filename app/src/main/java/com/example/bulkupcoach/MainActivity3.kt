package com.example.bulkupcoach

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import androidx.cardview.widget.CardView

class MainActivity3 : AppCompatActivity() {

    private lateinit var userCard: CardView
    private lateinit var workoutCard: CardView
    private lateinit var proteinCard: CardView
    private lateinit var sleepCard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        userCard = findViewById(R.id.userCard)
        workoutCard = findViewById(R.id.workoutCard)
        proteinCard = findViewById(R.id.proteinCard)
        sleepCard = findViewById(R.id.sleepCard)

        userCard.setOnClickListener {
            val intent = Intent(this@MainActivity3, UserActivity::class.java)
            startActivity(intent)
        }

        workoutCard.setOnClickListener {
            val intent2 = Intent(this@MainActivity3, WorkoutActivity::class.java)
            startActivity(intent2)
        }

        proteinCard.setOnClickListener {
            val intent3 = Intent(this@MainActivity3, ProteinActivity::class.java)
            startActivity(intent3)
        }

        proteinCard.setOnClickListener {
            val intent4 = Intent(this@MainActivity3, SleepActivity::class.java)
            startActivity(intent4)
        }
    }
}