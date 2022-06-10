package com.tfg.myapplication

import android.content.Context
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
        private var tipoUsuariotClientfRestaurante:Boolean?=null
        private var valor:Boolean?=null
        private const val TAG = "MyActivity"
    }

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var binding: RegistroActivityBinding
    private lateinit var firebaseRealTimeData: FirebaseDatabase
    private lateinit var database: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {


        firebaseAnalytics = Firebase.analytics
        firebaseRealTimeData = Firebase.database
        database = FirebaseDatabase.getInstance().reference
        super.onCreate(savedInstanceState)
        binding = RegistroActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sesionActiva()

        binding.botonLogin.setOnClickListener {
            if (loginrelleno()) {
                Toast.makeText(this, "Rellena los campos primero", Toast.LENGTH_SHORT).show()
            } else if (!requisitosUsuarioContrasenia()) {
                Toast.makeText(this, "Requisitos: contraseña de 8 digitos al menos 1 letra y numero\n direccion de corro valida", Toast.LENGTH_SHORT).show()
            } else {
                comprobarTipoUsuario()
                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.correoTXT.text.toString(),binding.contraseniaTXT.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful){
                        if(valor==true){
                            val menuCliente:Intent=Intent(this,MenuClientes::class.java).apply {
                                putExtra("Usuario" , nickUsuarioAdaptado(binding.contraseniaTXT.text.toString()))
                                putExtra("Correo",binding.correoTXT.text.toString())
                                putExtra("Contrasenia",binding.contraseniaTXT.text.toString())
                            }
                            startActivity(menuCliente)
                        }else if(valor==false){
                            val menuRestaurante:Intent=Intent(this,MenuRestaurantes::class.java).apply {
                                putExtra("Usuario" , binding.correoTXT.text)
                                putExtra("Correo",binding.correoTXT.text.toString())
                                putExtra("Contrasenia",binding.contraseniaTXT.text.toString())
                            }
                            startActivity(menuRestaurante)
                        }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Usuario no registrado", Toast.LENGTH_SHORT).show()
                }
            }

        }

        binding.botonRegistro.setOnClickListener {
            if(loginrelleno()){
                Toast.makeText(this, "Rellena los campos primero", Toast.LENGTH_SHORT).show()
            }else if(!requisitosUsuarioContrasenia()){
                Toast.makeText(this, "Requisitos: contraseña de 8 digitos al menos 1 letra y numero\n direccion de corro valida", Toast.LENGTH_SHORT).show()
            }else{
                crearUsuario()
            }
        }

        binding.botonRegistroLoginTxt.setOnClickListener {
            mostrarRegistro()
        }

        binding.imgCliente.setOnClickListener {
            binding.imgRestaurante.setBackgroundColor(Color.TRANSPARENT)
            binding.imgCliente.setBackgroundColor(Color.parseColor("#86FAD9"))
            tipoUsuariotClientfRestaurante=true
        }

        binding.imgRestaurante.setOnClickListener {
            binding.imgCliente.setBackgroundColor(Color.TRANSPARENT)
            binding.imgRestaurante.setBackgroundColor(Color.parseColor("#86FAD9"))
            tipoUsuariotClientfRestaurante=false
        }


    }

    override fun onStart() {
        super.onStart()
        binding.registroAuth.visibility=View.VISIBLE
    }

    fun sesionActiva(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val correoSesionGuardada:String? = prefs.getString("Correo",null)
        Log.d(TAG,correoSesionGuardada.toString())
        if(correoSesionGuardada!=null){
            binding.registroAuth.visibility=View.GONE
            comprobarTipoUsuarioLogeado(nickUsuarioAdaptado(correoSesionGuardada.toString()))
            if(tipoUsuariotClientfRestaurante==true){
                val menuCliente:Intent=Intent(this,MenuClientes::class.java).apply {
                    putExtra("Usuario" , nickUsuarioAdaptado(correoSesionGuardada.toString()))
                    putExtra("Correo",binding.correoTXT.text.toString())
                    putExtra("Contrasenia",binding.contraseniaTXT.text.toString())
                }
                startActivity(menuCliente)
            }else if(valor==false){
                val menuRestaurante:Intent=Intent(this,MenuRestaurantes::class.java).apply {
                    putExtra("Usuario" , nickUsuarioAdaptado(correoSesionGuardada))
                    putExtra("Correo",binding.correoTXT.text.toString())
                    putExtra("Contrasenia",binding.contraseniaTXT.text.toString())
                }
                startActivity(menuRestaurante)
            }
        }
    }

    fun loginrelleno():Boolean {
        return (binding.correoTXT.text.isEmpty() || binding.contraseniaTXT.text.isEmpty())
    }

    fun requisitosUsuarioContrasenia():Boolean{
        var contador:Int=0
        binding.correoTXT.text.toString().forEach {
            if(it == '.')
                contador+=1
        }
        return (binding.contraseniaTXT.text.contains("(?=.*[a-zA-Z])(?=.*[0-9])".toRegex()) && android.util.Patterns.EMAIL_ADDRESS.matcher(binding.correoTXT.text.toString()).matches() && contador==1)
    }

    fun comprobarTipoUsuario() {

        database.child("Usuarios").child(nickUsuarioAdaptado(binding.correoTXT.text.toString())).child("tipo").get().addOnSuccessListener {
            if(it.value==true) {
                valor = true
                binding.textoEslogan.text = valor.toString()
            }
        }

            database.child("Restaurantes").child(nickUsuarioAdaptado(binding.contraseniaTXT.text.toString())).child("tipo").get().addOnSuccessListener {
                if(it.value==false){
                    valor = false
                    binding.textoEslogan.text= valor.toString()
            }
        }


    }

    fun comprobarTipoUsuarioLogeado(usuario:String) {

        database.child("Usuarios").child(usuario).child("tipo").get().addOnSuccessListener {
            if(it.value==true) {
                valor = true
                binding.textoEslogan.text = valor.toString()
            }
        }

        database.child("Restaurantes").child(usuario).child("tipo").get().addOnSuccessListener {
            if(it.value==false){
                valor = false
                binding.textoEslogan.text= valor.toString()
            }
        }


    }





    fun crearUsuario() {

        when (tipoUsuariotClientfRestaurante) {
            true -> {
                usuarioCliente()
            }
            false -> {
                usuarioRestaurante()
            }
            else -> Toast.makeText(this,"Selecciona si eres cliente o restaurante",Toast.LENGTH_SHORT).show()
        }


    }

    fun usuarioCliente() {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.correoTXT.text.toString(), binding.contraseniaTXT.text.toString()).addOnCompleteListener {
            if (it.isSuccessful) {
                var nuevoCliente = Usuario(binding.correoTXT.text.toString(), 0, true)
                val clienteBaseDatos = firebaseRealTimeData.getReference("Usuarios").child((nickUsuarioAdaptado(binding.contraseniaTXT.text.toString())))
                clienteBaseDatos.setValue(nuevoCliente)
                ocultarRegistro()
            }
        }
    }

    fun usuarioRestaurante() {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.correoTXT.text.toString(), binding.contraseniaTXT.text.toString()).addOnCompleteListener {
            if (it.isSuccessful) {
                var listaDescuentos: MutableList<String> = mutableListOf("")
                var listaPuntos: MutableList<String> = mutableListOf("")
                var nuevoRestaurante = Restaurante(binding.correoTXT.text.toString(), listaDescuentos, listaPuntos,false)

                val restauranteBaseDatos = firebaseRealTimeData.getReference("Restaurantes").child((nickUsuarioAdaptado(binding.contraseniaTXT.text.toString())))
                restauranteBaseDatos.setValue(nuevoRestaurante)
                ocultarRegistro()
            }


        }
    }

    fun mostrarRegistro() {
        binding.botonRegistro.visibility = View.VISIBLE
        binding.botonLogin.visibility = View.GONE
        binding.imgRestaurante.visibility = View.VISIBLE
        binding.imgCliente.visibility = View.VISIBLE
        binding.txtEstadoLogReg.text = "Registrate"
        binding.botonRegistroLoginTxt.text = ""
        binding.txtRegistroLogin.text = ""
    }

    fun ocultarRegistro() {
        binding.botonRegistro.visibility = View.GONE
        binding.botonLogin.visibility = View.VISIBLE
        binding.imgRestaurante.visibility = View.GONE
        binding.imgCliente.visibility = View.GONE
        binding.txtEstadoLogReg.text = "Login"
        binding.botonRegistroLoginTxt.text = "Registrate"
        binding.txtRegistroLogin.text = "¿No tienes cuenta?"
        tipoUsuariotClientfRestaurante=null
        binding.imgCliente.setBackgroundColor(Color.TRANSPARENT)
        binding.imgRestaurante.setBackgroundColor(Color.TRANSPARENT)
    }


    fun nickUsuarioAdaptado(correoUsuario:String): String {

        var nickadaptado = ""
        val caracteresNoadmitidos: List<Char> = listOf('.', '#', '$', '[', ']')

        correoUsuario.forEach {
            if (!caracteresNoadmitidos.contains(it))
                nickadaptado += it
        }
        return nickadaptado
    }

}