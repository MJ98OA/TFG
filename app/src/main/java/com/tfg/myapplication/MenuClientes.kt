package com.tfg.myapplication

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tfg.myapplication.databinding.MenuclientesBinding

class MenuClientes : AppCompatActivity() {

    companion object {
        private val REQUEST_PERMISSION_REQUEST_CODE = 2020
        private lateinit var binding: MenuclientesBinding
        private lateinit var database: DatabaseReference
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = MenuclientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //obtencion de los datos del usuario
        val bundle: Bundle? = intent.extras
        var usuario: String? = bundle?.getString("Usuario")
        var correo: String? = bundle?.getString("Correo")
        var contrasenia:String? = bundle?.getString("Contrasenia")

        obtenerDatos(usuario.toString())


        binding.nombreUsuario.text= usuario?.let { it.substring(0,it.indexOf("@")) }.toString()

        //guardado de datos usuario en sharepreferences
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("Usuario",correo)
        prefs.putString("Contrasenia",contrasenia)
        prefs.apply()

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

                val intent:Intent=Intent(this,MyService::class.java).apply {
                    putExtra("usuario",usuario.toString())
                }
                startService(intent)

            }else if(buttonView.isChecked){
                Intent(this,MyService::class.java).also {
                    stopService(it)
                }
            }


        }

        binding.btnLocalizacionSegundoPlano.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this@MenuClientes,
                    arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    ,REQUEST_PERMISSION_REQUEST_CODE)

            }
        }

        binding.btnLogOut.setOnClickListener{
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }





    }

    fun obtenerDatos(usuario:String){
        database = FirebaseDatabase.getInstance().reference
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                binding.puntosUsuario.text=dataSnapshot.child("Usuarios").child(usuario).child("puntos").value.toString()
                binding.restauranteSeleccionado.text=dataSnapshot.child("Usuarios").child(usuario).child("restuaranteElegido").value.toString()
                binding.nombreUsuario.text=dataSnapshot.child("Usuarios").child(usuario).key.toString()
            }


            override fun onCancelled(databaseError: DatabaseError) {

                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(postListener)
        database.addListenerForSingleValueEvent(postListener)

    }



}


