package com.brend.serialsnow.services

import com.brend.serialsnow.models.SerialInfo
import com.brend.serialsnow.models.SerialInfoResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiService {

    companion object Factory {
        private const val BASE_URI = "https://serials-now.ru/"

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URI)
                    .build()

            return retrofit.create(ApiService::class.java)
        }

    }

    @GET("json/Main-2.json")
    fun listSerials(): Call<ArrayList<ArrayList<SerialInfo>>>

    @FormUrlEncoded
    @POST("API/Online.php")
    fun getSerialInfo(@Field("ID") id: String): Call<SerialInfoResponse>

}