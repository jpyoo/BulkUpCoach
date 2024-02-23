package com.example.bulkupcoach

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.bulkupcoach.ui.theme.BulkUpCoachTheme
//ComponentActivity
class SecondActivity : AppCompatActivity() {

    private lateinit var textUserName : TextView
    private lateinit var textEmail : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        textUserName = findViewById(R.id.sv_username)
        textEmail = findViewById(R.id.sv_email)

        val userName = intent.getStringExtra("username")
        val password = intent.getStringExtra("password")

        textUserName.text = "Username: "+userName
        textEmail.text = "Password: "+password
        /*setContent {
            BulkUpCoachTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }*/
    }
}
/*
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BulkUpCoachTheme {
        Greeting("Android")
    }
}*/