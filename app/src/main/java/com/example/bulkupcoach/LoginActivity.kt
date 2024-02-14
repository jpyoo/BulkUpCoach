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

class LoginActivity : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001 // Request code for Google Sign-In

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        Log.d(TAG, "onCreate: Setting up Google Sign-In")
        Amplify.Auth.fetchAuthSession(
            { Log.i("AmplifyQuickstart", "Auth session = $it") },
            { error -> Log.e("AmplifyQuickstart", "Failed to fetch auth session", error) }
        )
        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<View>(R.id.sign_in_button).setOnClickListener { signIn() }
    }

    private fun signIn() {
        Log.d(TAG, "signIn: Starting Google Sign-In intent")
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult: Google Sign-In result received")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                handleGoogleSignInResult(account)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
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

            // Start DashboardActivity
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        } else {
            Log.e(TAG, "handleGoogleSignInResult: Google Sign-In failed, account is null")
            // Handle sign-in failure
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
