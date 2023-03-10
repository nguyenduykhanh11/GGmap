package com.example.locationcheckapp.model.Entity.MapApi


import com.google.gson.annotations.SerializedName

data class Polyline(
    @SerializedName("points")
    val points: String
)