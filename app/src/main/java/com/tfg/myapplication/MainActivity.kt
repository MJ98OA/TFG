package com.tfg.myapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.tfg.myapplication.databinding.RegistroActivityBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database

import com.google.firebase.ktx.Firebase


import android.os.Looper

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object{
        var loginActivado=true
        var tClientesfRestaurante:Boolean?=null
        lateinit var usuarioNuevo:Usuario

    }

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var binding:RegistroActivityBinding
    private lateinit var firebaseRealTimeData: FirebaseDatabase
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {

        database = Firebase.database.reference
        firebaseAnalytics = Firebase.analytics
        firebaseRealTimeData = Firebase.database
        super.onCreate(savedInstanceState)
        binding= RegistroActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.botonRegistroLoginTxt.setOnClickListener {
            ActivarRegistro()
        }


        binding.botonRegistro.setOnClickListener {
            if(requisitosCorreoContrasenia()){
                    crearUsuario()
            }
        }

        binding.imgCliente.setOnClickListener{
            if(!loginActivado){
                activarImgCliente()
            }else{
                binding.imgCliente.setBackgroundColor(Color.TRANSPARENT)
            }

        }
        binding.imgRestaurante.setOnClickListener{
            if(!loginActivado){
                activarImgRestaurante()
            }else{
                binding.imgRestaurante.setBackgroundColor(Color.TRANSPARENT)
            }

        }

        binding.botonLogin.setOnClickListener {
            if(requisitosCorreoContrasenia())
                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.correoTXT.text.toString(), binding.contraseniaTXT.text.toString()
                ).addOnCompleteListener{
                    if(it.isSuccessful){
                        inicioMenuCliente()
                    }else
                        Toast.makeText(this, "Usuario no existente o contraseña/email incorrectos", Toast.LENGTH_SHORT).show()
                }
        }

    }




    fun inicioMenuCliente(){
        val menuCliente:Intent=Intent(this,MenuClientes::class.java).apply {

            usuarioNuevo = Usuario(
                binding.correoTXT.text.toString().substring(0, binding.correoTXT.text.toString().indexOf("@")), 0)
            putExtra("Usuario" , usuarioNuevo.nombre)

        }
        startActivity(menuCliente)
    }


    fun activarImgCliente (){
        binding.imgCliente.setBackgroundColor(Color.parseColor("#86FAD9"))
        binding.imgRestaurante.setBackgroundColor(Color.TRANSPARENT)
        tClientesfRestaurante=true
        Toast.makeText(this, tClientesfRestaurante.toString(), Toast.LENGTH_SHORT).show()
    }
    fun activarImgRestaurante(){
        binding.imgRestaurante.setBackgroundColor(Color.parseColor("#86FAD9"))
        binding.imgCliente.setBackgroundColor(Color.TRANSPARENT)
        tClientesfRestaurante=false
        Toast.makeText(this, tClientesfRestaurante.toString(), Toast.LENGTH_SHORT).show()
    }

    fun ActivarRegistro(){
        if(loginActivado){
            binding.txtRegistroLogin.text="Accede ya"
            binding.botonRegistroLoginTxt.text="Login"
            binding.botonRegistro.visibility=View.VISIBLE
            binding.botonLogin.visibility=View.GONE
            loginActivado=false
            binding.imgRestaurante.visibility=View.VISIBLE
            binding.imgCliente.visibility=View.VISIBLE
            binding.txtEstadoLogReg.text="Registro"
        }

        else{
            binding.txtEstadoLogReg.text="Login"
            tClientesfRestaurante=null
            binding.txtRegistroLogin.text="¿No tienes cuenta?"
            binding.botonRegistroLoginTxt.text="Registrate"
            binding.botonRegistro.visibility=View.GONE
            binding.botonLogin.visibility=View.VISIBLE
            loginActivado=true
            binding.imgCliente.setBackgroundColor(Color.TRANSPARENT)
            binding.imgRestaurante.setBackgroundColor(Color.TRANSPARENT)
            binding.imgRestaurante.visibility=View.GONE
            binding.imgCliente.visibility=View.GONE
        }
    }

    fun crearUsuario() {
        if (tClientesfRestaurante == null) {
            Toast.makeText(this, "Selecciona si eres cliente o restaurante primero", Toast.LENGTH_SHORT).show()
        } else {
            usuarioNuevo = Usuario(
                binding.correoTXT.text.toString().substring(0, binding.correoTXT.text.toString().indexOf("@")), 0
            )

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                binding.correoTXT.text.toString(), binding.contraseniaTXT.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful) {

                    val usuariocreado = firebaseRealTimeData.getReference(usuarioNuevo.nombre)
                    usuariocreado.setValue(Usuario(usuarioNuevo.nombre, 0))

                } else
                    Toast.makeText(this, "Usaurio ya registrado", Toast.LENGTH_SHORT).show()
            }

        }


    }
    fun requisitosCorreoContrasenia(): Boolean {

        if(binding.correoTXT.text.isEmpty() || binding.contraseniaTXT.text.isEmpty()) {
            Toast.makeText(this, "Rellena los campos primero", Toast.LENGTH_SHORT).show()
        }else {
            if (!(binding.contraseniaTXT.text.contains("(?=.*[a-zA-Z])(?=.*[0-9])".toRegex()) && binding.contraseniaTXT.text.length>=8))
                Toast.makeText(
                    this,
                    "La contraseña debe de ser de mas de 8 digitos y combinación de letras y numeros",
                    Toast.LENGTH_SHORT
                ).show()

            if (!binding.correoTXT.text.contains("(?=.*[@])(?=.*[.])".toRegex()))
                Toast.makeText(this, "El correo no cumple el formato", Toast.LENGTH_SHORT).show()
        }

        return  binding.contraseniaTXT.text.length >= 8 && binding.correoTXT.text.contains("(?=.*[@])(?=.*[.])".toRegex())

    }

}
