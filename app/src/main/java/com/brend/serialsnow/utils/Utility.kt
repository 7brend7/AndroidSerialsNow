package com.brend.serialsnow.utils

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import com.brend.serialsnow.R


class Utility {

    companion object Factory {
        fun calculateNoOfColumns(context: Context): Int {
            val displayMetrics = context.resources.displayMetrics
            val dpWidth = displayMetrics.widthPixels / displayMetrics.density
            return (dpWidth / 160).toInt()
        }

        fun connectionCheck(context: Context, clb: () -> Unit) {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm.activeNetworkInfo?.isConnected == true) {
                clb()
            }
            else {
                showConnectionError(context)
            }
        }

        private fun showConnectionError(ct: Context) {
            AlertDialog.Builder(ct)
                    .setTitle(ct.getString(R.string.no_internet_title))
                    .setMessage(ct.getString(R.string.no_internet_message))
                    .setPositiveButton("Ok", { dialog, i -> dialog.cancel() }).show()
        }
    }
}