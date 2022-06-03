package com.tfg.myapplication

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.*
import com.tfg.myapplication.databinding.MenuclientesBinding

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


        obtenerpuntos(usuario.toString())



        binding.onOffLocalizacion.setOnCheckedChangeListener { buttonView, isChecked ->

            if(buttonView.isChecked){

                if (ContextCompat.checkSelfPermission(
                        applicationContext,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this@MenuClientes,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        ,REQUEST_PERMISSION_REQUEST_CODE)
                }
                binding.btnLocalizacionSegundoPlano.visibility= View.VISIBLE


                binding.btnLocalizacionSegundoPlano.setOnClickListener {
                    if (ContextCompat.checkSelfPermission(
                            applicationContext,android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(this@MenuClientes,
                            arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            ,REQUEST_PERMISSION_REQUEST_CODE)

                    }
                }

                val intent:Intent=Intent(this,MyService::class.java).apply {
                    putExtra("usuario",usuario.toString())
                }
                startService(intent)



            }else
                Intent(this,MyService::class.java).also {
                    stopService(it)
                }

        }





    }

    fun obtenerpuntos(usuario:String){
        database = FirebaseDatabase.getInstance().reference
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                binding.usuario.text=dataSnapshot.child("Usuarios").child(usuario).child("puntos").value.toString()

            }


            override fun onCancelled(databaseError: DatabaseError) {

                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(postListener)
        database.addListenerForSingleValueEvent(postListener)

    }


}


