package com.tfg.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.tfg.myapplication.databinding.MenuclientesBinding
import com.google.firebase.database.DatabaseReference

class MenuClientes : AppCompatActivity() {


    private lateinit var binding: MenuclientesBinding
    private lateinit var database: DatabaseReference

    companion object {
        private val REQUEST_PERMISSION_REQUEST_CODE = 2020


    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = MenuclientesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle: Bundle? = intent.extras
        var usuario: String? = bundle?.getString("Usuario")
        binding.usuario.text = usuario



        binding.onOffLocalizacion.setOnCheckedChangeListener { buttonView, isChecked ->

            if(buttonView.isChecked){

                if (ContextCompat.checkSelfPermission(
                        applicationContext,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this@MenuClientes,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        ,REQUEST_PERMISSION_REQUEST_CODE)

                }else {
                    getCurrentLocation()
                }

                Intent(this,MyService::class.java).also {
                    startService(it)
                }



            }else
                Intent(this,MyService::class.java).also {
                    stopService(it)
                }

        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_REQUEST_CODE && grantResults.size > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation()
            }else{
                Toast.makeText(this@MenuClientes,"Permission Denied!",Toast.LENGTH_SHORT).show()
            }
        }
    }



    @SuppressLint("MissingPermission")

    private fun getCurrentLocation() {

        var locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY




        LocationServices.getFusedLocationProviderClient(this@MenuClientes)
            .requestLocationUpdates(locationRequest,object :LocationCallback(){
                override fun onLocationResult(locationResult: LocationResult?) {
                    super.onLocationResult(locationResult)
                    LocationServices.getFusedLocationProviderClient(this@MenuClientes)
                        .removeLocationUpdates(this)
                    if (locationResult != null && locationResult.locations.size > 0){
                        var locIndex = locationResult.locations.size-1

                        var latitude = locationResult.locations.get(locIndex).latitude

                        binding.coordenadas.text=""+latitude

                    }
                }

            }, Looper.getMainLooper())

    }
}


