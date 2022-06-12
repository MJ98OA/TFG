package com.tfg.myapplication

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
        var listaTimestamp:MutableList<Notificacion> = arrayListOf()
        lateinit var usuario:String
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        coordenadasRestaurantes()


        intent?.let {
            usuario = it.getStringExtra("usuario").toString()
        }

        Log.d(TAG,"Start")
        Log.d(TAG, usuario)




        handler.apply {
            val runnable = object:Runnable{
                override fun run() {
                    getCurrentLocation()
                    postDelayed(this,10000)
                }
            }
            postDelayed(runnable,10000)
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




        listaCoordenadasRestaurantes.forEachIndexed { index, datosRestaurante ->

            var coordenadasRestaurante = Location(LocationManager.GPS_PROVIDER)
            coordenadasRestaurante.latitude = datosRestaurante.latitud
            coordenadasRestaurante.longitude = datosRestaurante.longitud

            if(coordenadasMias.distanceTo(coordenadasRestaurante).toDouble()>3500.3398){

                Log.d(TAG,"Notificacion")
                createChannelNotifications(listaTimestamp[index].cHANNEL_ID, listaTimestamp[index].informacion)
                sendNotificaction(listaTimestamp[index].cHANNEL_ID,listaTimestamp[index].cHANNEL_ID,listaTimestamp[index].informacion,listaTimestamp[index].notification_ID,latitude, longitud, coordenadasRestaurante.latitude, coordenadasRestaurante.longitude)
            }

            Log.d(TAG,coordenadasMias.distanceTo(coordenadasRestaurante).toString())

        }






    }

    fun coordenadasRestaurantes(){
        var contador:Int=0
        database = FirebaseDatabase.getInstance().reference
        database.get().addOnSuccessListener {

            for(i in it.children){

                if(i.child("Nombre").exists()){

                    listaCoordenadasRestaurantes.add(DatosRestaurante(i.child("Estrellas").value.toString(),i.child("Latitud").value.toString().toDouble(),i.child("Longitud").value.toString().toDouble(),i.child("Nombre").value.toString(),i.child("Precio Medio").value.toString()))
                    Log.d(TAG,i.child("Nombre").value.toString())
                    listaTimestamp.add(Notificacion(0,i.child("Nombre").value.toString(),contador,(i.child("Estrellas").value.toString()+"\n" + i.child("Precio Medio").value)))
                    contador++
                }


            }

        }
    }

    fun createChannelNotifications(cHANNEL_ID: String,informacion: String){
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){

                val name=cHANNEL_ID
                val importance=NotificationManager.IMPORTANCE_DEFAULT
                val channel=NotificationChannel(cHANNEL_ID,name,importance).apply {
                    description=informacion
                }
                val notificationManager:NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)

        }
    }

    fun sendNotificaction(cHANNEL_ID: String, nombreRestaurante: String, informacion: String, notifiaction_ID: Int, latitudemia: Double, longitudmia: Double, latituderestaurante: Double, longituderestaurante: Double){

        val gmmIntentUri = Uri.parse("http://maps.google.com/maps?f=d&hl=en&saddr="+latitudemia+","+longitudmia+"&daddr="+latituderestaurante+","+longituderestaurante)
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        mapIntent.setPackage("com.google.android.apps.maps")

        val pendingIntent:PendingIntent=PendingIntent.getActivity(this,0,mapIntent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)


        val builder = NotificationCompat.Builder(this,cHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(nombreRestaurante)
            .setContentText(informacion)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            notify(notifiaction_ID,builder.build())
        }



    }

}