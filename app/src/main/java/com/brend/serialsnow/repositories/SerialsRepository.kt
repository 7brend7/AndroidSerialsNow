package com.brend.serialsnow.repositories

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.brend.serialsnow.R
import com.brend.serialsnow.models.Category
import com.brend.serialsnow.models.SerialInfo
import com.brend.serialsnow.services.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SerialsRepository(var context: Context) {

    companion object {
        const val SERIALS_REPOSITORY_TAG = "SerialsRepository"
    }


    fun getSerials(onSerialsReadyCallback: OnSerialsReadyCallback) {
        val apiService = ApiService.create()

        apiService.listSerials().enqueue(object : Callback<ArrayList<ArrayList<SerialInfo>>> {
            override fun onFailure(call: Call<ArrayList<ArrayList<SerialInfo>>>?, t: Throwable?) {
                Log.e(SERIALS_REPOSITORY_TAG, t?.message)
                Toast.makeText(context, context.resources.getText(R.string.serials_repository_api_error), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<ArrayList<ArrayList<SerialInfo>>>?, response: Response<ArrayList<ArrayList<SerialInfo>>>?) {
                val categories = arrayListOf<Category>()

                if (response?.isSuccessful == true && response.body()?.isNotEmpty() == true) {

                    response.body()?.let {

                        val tabs = context.resources.getStringArray(R.array.tab_titles)

                        it.mapIndexedTo(categories) { i, serials -> Category(serials, tabs.getOrElse(i) { " " }) }

                    }
                }

                onSerialsReadyCallback.onDataReady(categories)
            }


        })
    }
}

interface OnSerialsReadyCallback {
    fun onDataReady(data: ArrayList<Category>)
}