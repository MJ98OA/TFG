package com.tfg.myapplication

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference

class MyService : Service() {

    lateinit var database:DatabaseReference
    private val TAG:String="MyService"
    val handler=Handler()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"Start")

        handler.apply {
            val runnable = object:Runnable{
                override fun run() {
                    getCurrentLocation()
                    postDelayed(this,3000)
                }
            }
            postDelayed(runnable,3000)
        }
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG,"Destroy")
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    @SuppressLint("MissingPermission")

    private fun getCurrentLocation() {

        var locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        LocationServices.getFusedLocationProviderClient(this@MyService)
            .requestLocationUpdates(locationRequest,object : LocationCallback(){
                override fun onLocationResult(locationResult: LocationResult?) {
                    super.onLocationResult(locationResult)
                    LocationServices.getFusedLocationProviderClient(this@MyService)
                        .removeLocationUpdates(this)
                    if (locationResult != null && locationResult.locations.size > 0){
                        var locIndex = locationResult.locations.size-1

                        var latitude = locationResult.locations.get(locIndex).latitude
                        var longitud = locationResult.locations.get(locIndex).longitude
                        Log.d(TAG,""+latitude)
                        Log.d(TAG,""+longitud)

                    }
                }

            }, Looper.getMainLooper())

        comprarCoordenadas()


    }


    fun comprarCoordenadas(){



        var targetlocation1 = Location(LocationManager.GPS_PROVIDER)
        targetlocation1.latitude = 36.4220005
        targetlocation1.longitude = -121.0839996

        var targetlocation2 = Location(LocationManager.GPS_PROVIDER)
        targetlocation2.latitude = 37.4200005
        targetlocation2.longitude = -122.0819996

        targetlocation1.distanceTo(targetlocation2)

        Log.d(TAG,"aaa"+targetlocation1.distanceTo(targetlocation2).toString())


    }


}