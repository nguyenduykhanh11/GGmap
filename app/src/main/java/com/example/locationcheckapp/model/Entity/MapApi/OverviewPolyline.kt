package com.example.locationcheckapp.model.Entity.MapApi


import com.google.gson.annotations.SerializedName

data class OverviewPolyline(
    @SerializedName("points")
    val points: String
)