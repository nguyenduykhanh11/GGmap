package com.example.locationcheckapp.model.Entity.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.locationcheckapp.model.local.LOCATION_TABLE

@Entity(tableName = LOCATION_TABLE)
data class Location(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "latitude") val latitude: Double?,
    @ColumnInfo(name = "longitude") val longitude: Double?,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis(),
)
