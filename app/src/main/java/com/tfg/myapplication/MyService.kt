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
import com.google.firebase.database.*

class MyService : Service() {





    companion object{
        val handler=Handler()
        lateinit var database:DatabaseReference
        private val TAG:String="MyService"
        val listaCoordenadasRestaurantes : MutableList<DatosRestaurante> = arrayListOf()

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        coordenadasRestaurantes()

        intent?.let {
            it.getStringExtra("usuario")
        }

        Log.d(TAG,"Start")





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

        var coordenadasMias = Location(LocationManager.GPS_PROVIDER)
        coordenadasMias.latitude = latitude
        coordenadasMias.longitude = longitud




        listaCoordenadasRestaurantes.forEach {

            var coordenadasRestaurante = Location(LocationManager.GPS_PROVIDER)
            coordenadasRestaurante.latitude = it.latitud
            coordenadasRestaurante.longitude = it.longitud

            coordenadasMias.distanceTo(coordenadasRestaurante)

            Log.d(TAG,coordenadasMias.distanceTo(coordenadasRestaurante).toString())

        }

        Log.d(TAG,"Acabe")





    }

    fun coordenadasRestaurantes(){

        database = FirebaseDatabase.getInstance().reference
        database.get().addOnSuccessListener {

            for(i in it.children){

                if(i.child("Nombre").exists()){

                    listaCoordenadasRestaurantes.add(DatosRestaurante(i.child("Estrellas").value.toString(),i.child("Latitud").value.toString().toDouble(),i.child("Longitud").value.toString().toDouble(),i.child("Nombre").value.toString(),i.child("Precio Medio").value.toString()))

                }


            }

        }




    }


}