package com.tfg.myapplication

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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



class MainActivity : AppCompatActivity() {

    companion object {
        var loginActivado = true
        var tClientesfRestaurante: Boolean? = null
        lateinit var usuarioNuevo: Usuario
        private val TAG: String = "MyService"
    }

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var binding: RegistroActivityBinding
    private lateinit var firebaseRealTimeData: FirebaseDatabase
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {

        database = Firebase.database.reference
        firebaseAnalytics = Firebase.analytics
        firebaseRealTimeData = Firebase.database
        super.onCreate(savedInstanceState)
        binding = RegistroActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.botonLogin.setOnClickListener {
            if (requisitosLogin()) {

                database.get().addOnSuccessListener {
                    if (it.child("Usuarios").child("puntos").exists()) {

                        val menuCliente: Intent = Intent(this, MenuClientes::class.java).apply {
                            putExtra("Usuario", binding.correoTXT.text.toString())
                        }
                        startActivity(menuCliente)
                    } else {

                        val menuRestaurante: Intent = Intent(this, MenuRestaurantes::class.java).apply {
                            putExtra("Usuario", binding.correoTXT.text)
                        }
                        startActivity(menuRestaurante)
                    }
                }
            }
        }

        binding.botonRegistroLoginTxt.setOnClickListener{
            mostrarRegistro()
        }

        binding.imgCliente.setOnClickListener{
            tClientesfRestaurante=true
            binding.imgRestaurante.setBackgroundColor(Color.TRANSPARENT)
            binding.imgCliente.setBackgroundColor(Color.parseColor("#86FAD9"))
        }

        binding.imgRestaurante.setOnClickListener {
            tClientesfRestaurante=false
            binding.imgCliente.setBackgroundColor(Color.TRANSPARENT)
            binding.imgRestaurante.setBackgroundColor(Color.parseColor("#86FAD9"))
        }

        binding.botonRegistro.setOnClickListener {
            if(requisitosLogin()){
                if(tClientesfRestaurante==null)
                    Toast.makeText(this, "Selecciona si eres cliente o restaurante primero", Toast.LENGTH_SHORT).show()
                else{
                    if(crearUsuario())
                        ocultarRegistro()
                }
            }
            tClientesfRestaurante=null
        }
    }

    fun requisitosLogin(): Boolean {
        if (binding.correoTXT.text.isEmpty() || binding.contraseniaTXT.text.isEmpty()) {
            Toast.makeText(this, "Rellena los campos primero", Toast.LENGTH_SHORT).show()
        } else {
            if (!(binding.contraseniaTXT.text.contains("(?=.*[a-zA-Z])(?=.*[0-9])".toRegex()) && binding.contraseniaTXT.text.length >= 8))
                Toast.makeText(this, "La contraseña debe de ser de mas de 8 digitos y combinación de letras y numeros", Toast.LENGTH_SHORT).show()

            if (!binding.correoTXT.text.contains("(?=.*[@])(?=.*[.])".toRegex())) Toast.makeText(this, "El correo no cumple el formato", Toast.LENGTH_SHORT).show()
        }

        return binding.contraseniaTXT.text.length >= 8 && binding.correoTXT.text.contains("(?=.*[@])(?=.*[.])".toRegex())

    }

    fun crearUsuario():Boolean {
        var creado:Boolean=true
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.correoTXT.text.toString(), binding.contraseniaTXT.text.toString()).addOnCompleteListener {
                if (it.isSuccessful) {
                    if (tClientesfRestaurante == true) {
                        var nuevoCliente = Usuario(binding.correoTXT.text.toString(), 0)
                        val clienteBaseDatos = firebaseRealTimeData.getReference("Usuarios").child((nickUsuariosincorreo(nuevoCliente.nombre)))
                        clienteBaseDatos.setValue(nuevoCliente)

                    } else {
                        var listaDescuentos: MutableList<String> = mutableListOf("")
                        var listaPuntos: MutableList<String> = mutableListOf("")
                        var nuevoRestaurante = Restaurante(binding.correoTXT.text.toString(),listaDescuentos,listaPuntos)

                        val restauranteBaseDatos = firebaseRealTimeData.getReference("Restaurantes").child((nickUsuariosincorreo(nuevoRestaurante.nombre)))
                        restauranteBaseDatos.setValue(nuevoRestaurante)
                    }

                } else{
                    Toast.makeText(this, "Usaurio ya registrado o problema con FireBase", Toast.LENGTH_SHORT).show()
                    creado=false
                }

            }
        return creado
    }

    fun mostrarRegistro(){
        binding.botonRegistro.visibility= View.VISIBLE
        binding.botonLogin.visibility=View.GONE
        binding.imgRestaurante.visibility=View.VISIBLE
        binding.imgCliente.visibility=View.VISIBLE
        binding.txtEstadoLogReg.text="Registrate"
        binding.botonRegistroLoginTxt.text=""
        binding.txtRegistroLogin.text=""
    }

    fun ocultarRegistro(){
        binding.botonRegistro.visibility= View.GONE
        binding.botonLogin.visibility=View.VISIBLE
        binding.imgRestaurante.visibility=View.GONE
        binding.imgCliente.visibility=View.GONE
        binding.txtEstadoLogReg.text="Login"
        binding.botonRegistroLoginTxt.text="Registrate"
        binding.txtRegistroLogin.text="¿No tienes cuenta?"
    }

    fun nickUsuariosincorreo(usuario:String):String{
        var nick=usuario.substring(0, binding.correoTXT.text.toString().indexOf("@"))
        var nickadaptado:String=""
        var caracteresNoadmitidos: List<Char> = listOf('.','#','$','[',']')

        nick.forEach{
           if(!caracteresNoadmitidos.contains(it))
               nickadaptado+=it
        }
        return nickadaptado
    }

}

