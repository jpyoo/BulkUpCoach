package com.example.bulkupcoach

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.core.Amplify
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import android.content.Context

class LoginActivity : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001 // Request code for Google Sign-In

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<View>(R.id.sign_in_button).setOnClickListener { signIn() }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                handleGoogleSignInResult(account)
            } catch (e: ApiException) {
                Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            }
        }
    }

    private fun handleGoogleSignInResult(account: GoogleSignInAccount?) {
        Log.d(TAG, "handleGoogleSignInResult: Handling Google Sign-In result")
        if (account != null) {
            val idToken = account.idToken
            val id = account.id
            val email = account.email
            val displayName = account.displayName
            Log.d(TAG, "handleGoogleSignInResult: ID token retrieved: $idToken")
            Log.d(TAG, "handleGoogleSignInResult: ID retrieved: $id")
            Log.d(TAG, "handleGoogleSignInResult: email retrieved: $email")
            Log.d(TAG, "handleGoogleSignInResult: displayName retrieved: $displayName")

            // Save account information
            saveAccountName(email)

            // Start DashboardActivity
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        } else {
            Log.e(TAG, "handleGoogleSignInResult: Google Sign-In failed, account is null")
            // Handle sign-in failure
        }
    }


    private fun saveAccountName(accountName: String?) {
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("accountName", accountName)
        editor.apply()
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
