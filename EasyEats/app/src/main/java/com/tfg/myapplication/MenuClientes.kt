package com.tfg.myapplication

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tfg.myapplication.databinding.MenuclientesBinding

class MenuClientes : AppCompatActivity() {

    companion object {
        private const val REQUEST_PERMISSION_REQUEST_CODE = 2020
        private lateinit var binding: MenuclientesBinding
        lateinit var database: DatabaseReference
        private val TAG:String="MyService"
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

        binding.btnDescuento.setOnClickListener {
            if(binding.restauranteSeleccionado.text.isNotEmpty() && binding.restauranteSeleccionado.text!="null"){
                database.child("Restaurantes").child(prepararCorreo()).child("listaDescuentos").child(usuario.toString()).setValue(usuario.toString())
            }else{
                Toast.makeText(this, "Ubiquese primero en un restaurante por favor, le aparecera en el menu cuando este en uno", Toast.LENGTH_SHORT).show()

            }
        }

        binding.btnPuntos.setOnClickListener {
            if(binding.restauranteSeleccionado.text.isNotEmpty() && binding.restauranteSeleccionado.text!="null"){

                if(binding.puntosUsuario.text.toString().toInt()>20){
                    var puntosActuales= binding.puntosUsuario.text.toString().toInt() - 20
                    Log.d(TAG,usuario.toString())
                    database.child("Restaurantes").child(prepararCorreo()).child("listaDescuentos").child(usuario.toString()).setValue(usuario.toString())
                    database.child("Usuarios").child(usuario.toString()).child("puntos").setValue(puntosActuales)
                }else{
                    Toast.makeText(this, "Necesiatas mas puntos minimo 20", Toast.LENGTH_SHORT).show()

                }
            }else{
                Toast.makeText(this, "Ubiquese primero en un restaurante por favor, le aparecera en el menu cuando este en uno", Toast.LENGTH_SHORT).show()

            }

        }


        binding.nombreUsuario.text= usuario?.let { it.substring(0,it.indexOf("@")) }.toString()

        //guardado de datos usuario en sharepreferences
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("Usuario",correo)
        prefs.putString("Contrasenia",contrasenia)
        prefs.apply()

        binding.onOffLocalizacion.setOnCheckedChangeListener { buttonView, isChecked ->
            if(buttonView.isChecked){
                if (ContextCompat.checkSelfPermission(
                        applicationContext,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this@MenuClientes,
                                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_PERMISSION_REQUEST_CODE)
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
                binding.restauranteSeleccionado.text=dataSnapshot.child("Usuarios").child(usuario).child("restauranteSeleccionado").value.toString()
                binding.nombreUsuario.text=dataSnapshot.child("Usuarios").child(usuario).key.toString()
                binding.descuento.text=dataSnapshot.child("Usuarios").child(usuario).child("descuento").value.toString()
            }


            override fun onCancelled(databaseError: DatabaseError) {

                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(postListener)
        database.addListenerForSingleValueEvent(postListener)

    }

    fun prepararCorreo():String{


        var cadena=binding.restauranteSeleccionado.text.toString()
        var resultado=""

        cadena.forEach {
            if(!it.isWhitespace()){
                resultado+=it
            }
        }
        Log.d(TAG,resultado)
        return "$resultado@gmailcom"

    }



}


