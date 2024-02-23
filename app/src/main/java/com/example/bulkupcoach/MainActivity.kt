package com.example.bulkupcoach

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var SaveDataBtn : Button
    private lateinit var UsernameEdit : EditText
    private lateinit var EmailEdit : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_test)

        SaveDataBtn = findViewById(R.id.SaveDataButton)
        UsernameEdit = findViewById(R.id.username)
        EmailEdit = findViewById(R.id.email)

        SaveDataBtn.setOnClickListener{
            startActivity(Intent(this, SecondActivity::class.java)
                .putExtra("username",UsernameEdit.text.toString())
                .putExtra("email",EmailEdit.text.toString())
            )
        }

        val spinner : Spinner = findViewById(R.id.selectProtein)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, l: Long) {
                val item: String = adapterView?.getItemAtPosition (position).toString()
                Toast.makeText(this@MainActivity, "Selected Item: $item", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?){

            }
        }

        val proteinList = arrayListOf(
            "Chicken", "Beef", "Pork", "Salmon", "Turkey", "Lamb", "Egg", "Soy"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, proteinList)
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        spinner.adapter = adapter


        // Start LoginActivity when the app starts
        //val intent = Intent(this, LoginActivity::class.java)
        //startActivity(intent)
        //finish() // Finish the MainActivity if LoginActivity is started
    }
}
