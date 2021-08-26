package com.poema.theorganizerapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast

object Utility {
    fun Context.isInternetAvailable(): Boolean {

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        if (netInfo != null && netInfo.isConnected) {
            return true
        } else {
            showErrorToast("Internet not available. Restricted to cached videos. Please check your connection!")
            return false
        }
    }

    private fun Context.showErrorToast(message: String?) {

        Toast.makeText(
            applicationContext, message,
            Toast.LENGTH_LONG
        ).show()

    }
}