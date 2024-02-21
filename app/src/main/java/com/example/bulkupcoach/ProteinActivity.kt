package com.example.bulkupcoach

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bulkupcoach.databinding.ActivityProteinBinding
import com.example.bulkupcoach.ui.protein.ProteinFragment

// CAKsIPzc5q86zPsuAqZUhA==zKjvLZZmaEBj15Aj

class ProteinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_protein)

        supportFragmentManager.beginTransaction().replace(R.id.container, ProteinFragment()).commit()
    }
}