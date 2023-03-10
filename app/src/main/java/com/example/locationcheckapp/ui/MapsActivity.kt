package com.example.locationcheckapp.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.locationcheckapp.MapService
import com.example.locationcheckapp.R
import com.example.locationcheckapp.databinding.ActivityMapsBinding
import com.example.locationcheckapp.model.Entity.room.Location
import com.example.locationcheckapp.viewModel.LocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.text.SimpleDateFormat
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap
    private lateinit var viewModel: LocationViewModel
    private var firstMap = true
    private var latitude: Double? = null
    private var longidute: Double? = null

    private var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.apply {
                latitude = getDoubleExtra(SEND_LATITUDE_TO_ACTIVITY, 0.001)
                longidute = getDoubleExtra(SEND_LONGITUDE_TO_ACTIVITY, 0.001)
                if (latitude != 0.001 && longidute != 0.001) {
                    saveLocation()
                    viewModel.readAllData.observe(this@MapsActivity) {
                        mMap.clear()
                        it.forEach { loacation ->
                            val location = LatLng(loacation.latitude!!, loacation.longitude!!)
                            if (firstMap) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13f))
                                firstMap = false
                            }
                            val markerOptions = MarkerOptions().apply {
                                position(location)
                                title(getString(R.string.my_name))
                                icon(avatarLocation())
                            }
                            mMap.addMarker(markerOptions)
                        }
                    }
                    Toast.makeText(applicationContext,
                        "$latitude \n $longidute",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun avatarLocation(): BitmapDescriptor {
        val avatarBitmap = BitmapFactory.decodeResource(resources, R.drawable.img)
        val size = 50
        val scaledBitmap = Bitmap.createScaledBitmap(avatarBitmap, size, size, false)
        val output =
            Bitmap.createBitmap(scaledBitmap.width, scaledBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, scaledBitmap.width, scaledBitmap.height)
        val rectF = RectF(rect)
        val radius = (scaledBitmap.width / 2).toFloat()
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(rectF.centerX(), rectF.centerY(), radius, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(scaledBitmap, rect, rect, paint)
        return BitmapDescriptorFactory.fromBitmap(output)
    }

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1
        const val KEY_ACTION_TO_ACTIVITY = "KEY_ACTION_TO_ACTIVITY"
        const val SEND_LATITUDE_TO_ACTIVITY = "SEND_LATITUDE_TO_ACTIVITY"
        const val SEND_LONGITUDE_TO_ACTIVITY = "SEND_LONGITUDE_TO_ACTIVITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[LocationViewModel::class.java]
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, IntentFilter(KEY_ACTION_TO_ACTIVITY))
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@MapsActivity)

    }


    private fun setRequestPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION)
        } else {
            mMap.isMyLocationEnabled = true
//            mMap.isTrafficEnabled =true
            startService(Intent(this, MapService::class.java))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setRequestPermission()
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val uiSettings = googleMap.uiSettings
        uiSettings.isMyLocationButtonEnabled = true
        setRequestPermission()
    }

    private fun saveLocation() {
        viewModel.insert(Location(null, latitude, longidute, timestamp()))
    }

    private fun timestamp(): Long {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val currentTimestamp = dateFormat.format(currentDate)
        return dateFormat.parse(currentTimestamp)?.time ?: 0L
    }

    override fun onDestroy() {
        stopService(Intent(this, MapService::class.java))
        super.onDestroy()
    }


//    private fun drawRoute(polyline: String) {
//        val decodedPolyline = PolylineEncoding.decode(polyline)
//        val points = ArrayList<LatLng>()
//        for (s in decodedPolyline) {
//            points.add(LatLng(s.lat, s.lng))
//        }
//        val polylineOptions = PolylineOptions()
//            .addAll(points)
//            .color(Color.RED)
//            .width(5f)
//        mMap.addPolyline(polylineOptions)
//
//        val builder = LatLngBounds.Builder()
//        for (point in points) {
//            builder.include(point)
//        }
//        val bounds = builder.build()
////        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
//    }


//    private fun getUrl() {
//        try {
//            val originLatLng = "37.7849569, -122.4068855" // Tọa độ điểm bắt đầu
//            val destinationLatLng = "37.8199286, -122.4782551" // Tọa độ điểm đích
//            val retrofit = Retrofit.Builder()
//                .baseUrl("https://maps.googleapis.com")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//
//            val service = retrofit.create(DirectionsService::class.java)
//            val call = service.getDirections("$latitude, $longidute", "37.8199286, -122.4782551", "driving", "AIzaSyCrUvM7rMfMn-qHd46tsA4Q31MTIqrQeyI")
//            call.enqueue(object : Callback<Map> {
//                override fun onResponse(
//                    call: Call<Map>,
//                    response: Response<Map>
//                ) {
//                    if (response.isSuccessful) {
//                        Log.d("this", "Da den day${response.body()}")
//                        val directionResponse = response.body()
//                        val routes = directionResponse?.routes ?: emptyList()
//                        val route = routes.firstOrNull()
//
//                        if (route != null) {
//                            val legs = route.legs
//                            val distance = legs.sumBy { it.distance.value }
//                            val duration = legs.sumBy { it.duration.value }
//                            val startAddress = legs.firstOrNull()?.startAddress ?: ""
//                            val endAddress = legs.lastOrNull()?.startAddress ?: ""
//                            val polyline = route.overviewPolyline.points
//                            drawRoute(polyline)
//                            Log.d("this", "Distance: $distance meters")
//                            Log.d("this", "Duration: $duration seconds")
//                            Log.d("this", "Start Address: $startAddress")
//                            Log.d("this", "End Address: $endAddress")
//                        } else {
//                            Log.e("this", "No routes found")
//                        }
//                    } else {
//                        Log.e("this", "Request failed")
//                    }
//                }
//
//                override fun onFailure(call: Call<Map>, t: Throwable) {
//                    Log.e("this", "Request failed", t)
//                }
//            })
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

//    }
}