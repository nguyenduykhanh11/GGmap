package com.example.locationcheckapp.model.Entity.MapApi


import com.google.gson.annotations.SerializedName

data class Map(
    @SerializedName("routes")
    val routes: List<Route>,
    @SerializedName("status")
    val status: String
)