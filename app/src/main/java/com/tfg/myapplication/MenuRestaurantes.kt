package com.tfg.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.tfg.myapplication.databinding.MenurestaurantesBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MenuRestaurantes : AppCompatActivity() {

    companion object{
        private lateinit var firebaseAnalytics: FirebaseAnalytics
        private lateinit var binding: MenurestaurantesBinding
        private lateinit var firebaseRealTimeData: FirebaseDatabase
        private lateinit var database: DatabaseReference
        private val TAG:String="MyService"
        private var listaUsuariosPuntos:MutableList<String> = arrayListOf()
        private var listaUsuariosDescuentos:MutableList<String> = arrayListOf()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        firebaseAnalytics = Firebase.analytics
        firebaseRealTimeData = Firebase.database
        super.onCreate(savedInstanceState)
        binding = MenurestaurantesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle: Bundle? = intent.extras
        var usuario: String? = bundle?.getString("Usuario")
        binding.actualizarLista.setOnClickListener {
            obtenerClientesPuntos(usuario.toString() )
        }

        if(binding.edtInsertarUsuarioPuntos.text.isEmpty() && binding.edtInsertarPuntos.text.isEmpty()){

        }


    }

    @SuppressLint("SetTextI18n")
    fun obtenerClientesPuntos(usuario:String){
        listaUsuariosPuntos.clear()
        listaUsuariosDescuentos.clear()

        Log.d(TAG,usuario)
        database = FirebaseDatabase.getInstance().reference

        database.child("Restaurantes").child(usuario).child("listapuntos").get().addOnSuccessListener {

            for(i in it.children){
                listaUsuariosPuntos.add(it.value.toString())
            }
            for(i in it.children){
                listaUsuariosDescuentos.add(it.value.toString())
            }

            listaUsuariosPuntos.forEach {tx->
                binding.listadoClientesPuntos.text= binding.listadoClientesPuntos.text.toString()+ tx
            }

            listaUsuariosDescuentos.forEach {txd->
                binding.listadoClientesDescuentos.text= binding.listadoClientesDescuentos.text.toString()+ txd
            }




        }

    }

    fun darPuntos(){
        if(binding.edtInsertarPuntos.text.isNotEmpty() && binding.edtInsertarUsuarioPuntos.text.isNotEmpty()){
            database.child("Usuarios").child(binding.edtInsertarUsuarioPuntos.text.toString()).child("puntos").get().toString()
        }
    }





}

