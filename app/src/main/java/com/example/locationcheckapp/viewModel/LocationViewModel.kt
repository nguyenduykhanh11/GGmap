package com.example.locationcheckapp.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.locationcheckapp.model.Entity.MapApi.Map
import com.example.locationcheckapp.model.Entity.room.Location
import com.example.locationcheckapp.model.local.AppDatabase
import com.example.locationcheckapp.model.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val locationDao = AppDatabase.getInstance(application).locationDao()
    private var routeLiveData = MutableLiveData<Map>()
    val readAllData: LiveData<List<Location>> = locationDao.readData()

    fun insert(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            locationDao.insert(location)
        }
    }

    fun deleteAfterLast10Records() {
        viewModelScope.launch(Dispatchers.IO) {
            locationDao.deleteAfterLast10Records()
        }
    }

    fun getRoute(pastLocation: String, currentLocation: String, mode: String, key: String) {
        if (pastLocation.isEmpty() || pastLocation.isEmpty()) {
            Log.d("this", "data null*********************************************")
            return
        }
        RetrofitInstance.api.getDirections(pastLocation, currentLocation, mode, key)
            .enqueue(object : Callback<Map> {
                override fun onResponse(call: Call<Map>, response: Response<Map>) {
                    response.body()?.let { map ->
                        Log.d("this", map.toString()
                        )
                        routeLiveData.postValue(map)
                    }
                }

                override fun onFailure(call: Call<Map>, t: Throwable) {
                    Log.d("Tag", "thất bại")
                }

            })
    }

    fun observeGetRoute(): LiveData<Map> {
        return routeLiveData
    }
}