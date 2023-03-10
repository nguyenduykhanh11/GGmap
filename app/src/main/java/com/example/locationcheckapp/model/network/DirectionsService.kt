package com.example.locationcheckapp.model.network

import com.example.locationcheckapp.model.Entity.MapApi.Map
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsService {
    @GET("/maps/api/directions/json")
    fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String,
        @Query("key") apiKey: String
    ): retrofit2.Call<Map>
}