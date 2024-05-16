package com.lis355.retand.app.network

import android.os.StrictMode
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Network {
    companion object {
        init {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        fun <T : Any> createAPI(baseURL: String, service: Class<T>): T {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(service)
        }
    }
}