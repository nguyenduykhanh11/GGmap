package com.example.locationcheckapp.model.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api:DirectionsService by lazy {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DirectionsService::class.java)
    }
}