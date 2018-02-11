package com.brend.serialsnow.repositories

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.brend.serialsnow.R
import com.brend.serialsnow.models.SerialInfoResponse
import com.brend.serialsnow.services.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SerialInfoRepository(var context: Context) {

    companion object {
        const val SERIAL_INFO_REPOSITORY_TAG = "SERIAL_INFO_REPOSITORY_TAG"
    }

    fun getSerialInfo(id: String, onSerialInfoReadyCallback : OnSerialInfoReadyCallback) {
        val apiService = ApiService.create()

        apiService.getSerialInfo(id).enqueue(object : Callback<SerialInfoResponse> {
            override fun onFailure(call: Call<SerialInfoResponse>?, t: Throwable?) {
                Log.e(SERIAL_INFO_REPOSITORY_TAG, t?.message)
                Toast.makeText(context, context.resources.getText(R.string.serials_repository_api_error), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<SerialInfoResponse>?, response: Response<SerialInfoResponse>?) {
                response?.body()?.let {
                    if(it.serialInfo == null) {
                        onFailure(call, Throwable("Can't fetch data"))
                    }
                    else {
                        onSerialInfoReadyCallback.onDataReady(it)
                    }
                }
            }

        })
    }
}

interface OnSerialInfoReadyCallback {
    fun onDataReady(data : SerialInfoResponse)
}