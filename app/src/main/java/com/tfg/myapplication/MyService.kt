package com.tfg.myapplication

import android.annotation.SuppressLint
import android.app.Service
import android.content.ContentValues
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
import com.google.firebase.database.*

class MyService : Service() {

    lateinit var database:DatabaseReference
    private val TAG:String="MyService"
    lateinit var listaCoordenadas:MutableList<DatosRestaurante>


    companion object{
        val handler=Handler()

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            it.getStringExtra("usuario")
            Log.d(TAG,it.getStringExtra("usuario").toString())
        }

        Log.d(TAG,"Start")

        coordenadasRestaurantes()




        handler.apply {
            val runnable = object:Runnable{
                override fun run() {
                    getCurrentLocation()
                    postDelayed(this,1000)
                }
            }
            postDelayed(runnable,1000)
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

                        comprarCoordenadas(latitude,longitud)
                    }
                }

            }, Looper.getMainLooper())




    }


    fun comprarCoordenadas(latitude:Double,longitud:Double){


        var targetlocation1 = Location(LocationManager.GPS_PROVIDER)
        targetlocation1.latitude = 16.4220005
        targetlocation1.longitude = -21.0839996

        var targetlocation2 = Location(LocationManager.GPS_PROVIDER)
        targetlocation2.latitude = latitude
        targetlocation2.longitude = longitud

        Log.d(TAG,""+latitude)
        Log.d(TAG,""+longitud)

        targetlocation1.distanceTo(targetlocation2)

        Log.d(TAG,targetlocation1.distanceTo(targetlocation2).toString())


    }

    fun coordenadasRestaurantes(){


            database = FirebaseDatabase.getInstance().reference

            database.get().addOnSuccessListener {



            }

            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {


                    for(i in dataSnapshot.children){

                        print(i.child("idN").toString())
                        listaCoordenadas.add(DatosRestaurante(i.child("Coordenadas")))
                    }




                }


                override fun onCancelled(databaseError: DatabaseError) {

                    Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
                }
            }
            database.addValueEventListener(postListener)
            database.addListenerForSingleValueEvent(postListener)



    }


}