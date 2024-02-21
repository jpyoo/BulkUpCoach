package com.example.bulkupcoach.ui.calendar

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.databinding.DataBindingUtil
import com.example.bulkupcoach.R
import com.example.bulkupcoach.databinding.FragmentCalendarBinding
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.CalendarScopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.android.gms.common.ConnectionResult
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException

class CalendarFragment : Fragment() {

    companion object {
        private const val PREF_ACCOUNT_NAME = "accountName"
        private const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
        private const val REQUEST_ACCOUNT_PICKER = 1000
        private const val REQUEST_AUTHORIZATION = 1001
        private const val REQUEST_PERMISSION_GET_ACCOUNTS = 1003
    }

    private var mCredential: GoogleAccountCredential? = null
    private var mService: Calendar? = null
    private var mProgress: ProgressDialog? = null
    private lateinit var binding: FragmentCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCredentials()
        initView()
    }

    private fun initView() {
        mProgress = ProgressDialog(requireContext())
        mProgress!!.setMessage("Loading...")

        binding.btnLoadEvents.setOnClickListener {
            binding.btnLoadEvents.isEnabled = false
            binding.txtOut.text = ""
            getResultsFromApi()
            binding.btnLoadEvents.isEnabled = true
        }
    }

    private fun initCredentials() {
        mCredential = GoogleAccountCredential.usingOAuth2(
            requireContext(), listOf(CalendarScopes.CALENDAR)
        ).setBackOff(ExponentialBackOff())
    }

    private fun getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices()
        } else if (mCredential?.selectedAccountName == null) {
            chooseAccount()
        } else if (!isDeviceOnline()) {
            Log.e("CalendarFragment", "No network connection available.")
            binding.txtOut.text = "No network connection available."
        } else {
            makeRequestTask()
        }
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(requireContext())
        return connectionStatusCode == ConnectionResult.SUCCESS
    }

    private fun acquireGooglePlayServices() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(requireContext())
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
        }
    }

    private fun showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode: Int) {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val dialog = apiAvailability.getErrorDialog(
            requireActivity(),
            connectionStatusCode,
            REQUEST_GOOGLE_PLAY_SERVICES
        )
        dialog?.show()
    }

    private fun isDeviceOnline(): Boolean {
        val connMgr =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun chooseAccount() {
        if (EasyPermissions.hasPermissions(
                requireContext(), Manifest.permission.GET_ACCOUNTS
            )
        ) {
            val accountName = requireActivity().getPreferences(Context.MODE_PRIVATE)
                .getString(PREF_ACCOUNT_NAME, null)
            if (accountName != null) {
                mCredential!!.selectedAccountName = accountName
                getResultsFromApi()
            } else {
                startActivityForResult(
                    mCredential!!.newChooseAccountIntent(),
                    REQUEST_ACCOUNT_PICKER
                )
            }
        } else {
            EasyPermissions.requestPermissions(
                this,
                "This app needs to access your Google account (via Contacts).",
                REQUEST_PERMISSION_GET_ACCOUNTS,
                Manifest.permission.GET_ACCOUNTS
            )
        }
    }

    private fun makeRequestTask() {
        lifecycleScope.launch(Dispatchers.IO) {
            var mLastError: Exception? = null
            try {
                // Call the function to get the calendar list instead of events
                getCalendarList()
            } catch (e: Exception) {
                mLastError = e
                lifecycleScope.cancel()
            }

            launch(Dispatchers.Main) {
                mProgress!!.hide()
                if (mLastError != null) {
                    handleRequestFailure(mLastError!!)
                }
            }
        }
    }

    private fun getCalendarList() {
        val transport: HttpTransport = NetHttpTransport()
        val jsonFactory: com.google.api.client.json.JsonFactory = JacksonFactory.getDefaultInstance()

        mService = com.google.api.services.calendar.Calendar.Builder(
            transport, jsonFactory, mCredential
        ).setApplicationName("GetEventCalendar").build()

        try {
            val calendarList = mService!!.calendarList().list().execute()

            // Log the calendar details
            for (calendar in calendarList.items) {
                Log.d("CalendarFragment", "Calendar Summary: ${calendar.summary}")
            }
        } catch (e: UserRecoverableAuthIOException) {
            startActivityForResult(e.intent, REQUEST_AUTHORIZATION)
        } catch (e: com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException) {
            showGooglePlayServicesAvailabilityErrorDialog(e.connectionStatusCode)
        } catch (e: IOException) {
            Log.e("Google", "getCalendarList: ${e.message}", e)
        }
    }

    private fun handleRequestFailure(exception: Exception) {
        if (exception is UserRecoverableAuthIOException) {
            Log.e("CalendarFragment", "UserRecoverableAuthIOException occurred.")
            startActivityForResult(exception.intent, REQUEST_AUTHORIZATION)
        } else if (exception is GooglePlayServicesAvailabilityIOException) {
            Log.e("CalendarFragment", "GooglePlayServicesAvailabilityIOException occurred.")
            showGooglePlayServicesAvailabilityErrorDialog(exception.connectionStatusCode)
        } else {
            Log.e("CalendarFragment", "An error occurred: ${exception.message}")
            binding.txtOut.text = "An error occurred: ${exception.message}"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_GET_ACCOUNTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("CalendarFragment", "GET_ACCOUNTS permission granted.")
                chooseAccount()
            } else {
                Log.e("CalendarFragment", "GET_ACCOUNTS permission denied.")
                // Handle permission denied case
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_AUTHORIZATION || requestCode == REQUEST_ACCOUNT_PICKER) {
            if (resultCode == Activity.RESULT_OK && data != null && data.extras != null) {
                val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                if (accountName != null) {
                    requireActivity().getPreferences(Context.MODE_PRIVATE)
                        .edit()
                        .putString(PREF_ACCOUNT_NAME, accountName)
                        .apply()
                    mCredential?.selectedAccountName = accountName
                    getResultsFromApi()
                }
            }
        }
    }
}

