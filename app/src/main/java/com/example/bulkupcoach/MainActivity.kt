//MainActivity.kt
package com.example.bulkupcoach

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.AuthCategory
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Initialize Amplify
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(applicationContext)

            // Check if the user is already logged in
            checkLoggedInStatus()
        } catch (e: AmplifyException) {
            // Handle exception if Amplify fails to initialize
            Log.e("MainActivity", "Could not initialize Amplify", e)
            e.printStackTrace()
        }
    }

    private fun checkLoggedInStatus() {
        isLoggedIn { loggedIn ->
            runOnUiThread {
                if (loggedIn) {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                } else {
                    setContentView(R.layout.activity_main)
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun isLoggedIn(callback: (Boolean) -> Unit) {
        try {
            val auth = Amplify.Auth
            auth.getCurrentUser(
                { result ->
                    // Check if the result is not null to determine if the user is authenticated
                    callback(result != null)
                },
                { error ->
                    Log.e("MainActivity", "Error fetching current user", error)
                    error.printStackTrace()
                    // If an error occurs, consider the user as not logged in
                    callback(false)
                }
            )
        } catch (e: Exception) {
            Log.e("MainActivity", "Exception checking logged in status", e)
            e.printStackTrace()
            // If an exception occurs, consider the user as not logged in
            callback(false)
        }
    }

}
