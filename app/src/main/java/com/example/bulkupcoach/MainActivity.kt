package com.example.bulkupcoach

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start LoginActivity when the app starts
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Finish the MainActivity if LoginActivity is started
    }
}
