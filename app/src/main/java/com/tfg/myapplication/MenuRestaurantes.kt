package com.tfg.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.tfg.myapplication.databinding.MenurestaurantesBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
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
        var puntos=0

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        firebaseAnalytics = Firebase.analytics
        firebaseRealTimeData = Firebase.database
        super.onCreate(savedInstanceState)
        binding = MenurestaurantesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle: Bundle? = intent.extras
        var usuario: String? = bundle?.getString("Usuario")

        actualizarListas(usuario.toString())

        binding.actualizarLista.setOnClickListener {
            actualizarListas(usuario.toString())
        }

        binding.btEnvioPuntos.setOnClickListener {
            if(binding.edtPuntosAdar.text.isNotEmpty() && binding.edtUsuarioSumPuntos.text.isNotEmpty()){
                darPuntos(usuario.toString())
            }

        }

        binding.btDescuentoPuntos.setOnClickListener {
            if(binding.edtInsertarDescuento.text.isNotEmpty() && binding.edtInsertarUsuarioDescuento.text.isNotEmpty()){
                darDescuento(usuario.toString())
            }

        }

        binding.btnLogOutR.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

    }



    @SuppressLint("SetTextI18n")
    fun actualizarListas(usuario:String){

        Log.d(TAG,usuario)
        database = FirebaseDatabase.getInstance().reference

        database.child("Restaurantes").child(usuario).child("listapuntos").get().addOnSuccessListener {
            binding.listadoClientesPuntos.text=""
            it.children.forEach { child->
                binding.listadoClientesPuntos.text= binding.listadoClientesPuntos.text.toString()+ child.value.toString() + "\n"
            }
        }
        database.child("Restaurantes").child(usuario).child("listaDescuentos").get().addOnSuccessListener {
            binding.listadoClientesDescuentos.text=""
            it.children.forEach { child->
                binding.listadoClientesDescuentos.text= binding.listadoClientesDescuentos.text.toString()+ child.value.toString() + "\n"
            }
        }

    }

    fun darPuntos(usuario: String) {

        database=FirebaseDatabase.getInstance().reference
        database.child("Usuarios").child(binding.edtUsuarioSumPuntos.text.toString()).child("puntos").get().addOnSuccessListener {
            if(it.exists()){
                Log.d(TAG,binding.edtPuntosAdar.text.toString())
                Log.d(TAG,it.value.toString())
                database.child("Usuarios").child(binding.edtUsuarioSumPuntos.text.toString()).child("puntos").setValue(it.value.toString().toInt()+binding.edtPuntosAdar.text.toString().toInt())
                database.child("Restaurantes").child(usuario).child("listapuntos").child(binding.edtUsuarioSumPuntos.text.toString()).removeValue()
                binding.edtPuntosAdar.text.clear()
                binding.edtUsuarioSumPuntos.text.clear()

            }
        }
        actualizarListas(usuario)

    }




    fun darDescuento(usuario: String) {

        database.child("Usuarios").child(binding.edtInsertarUsuarioDescuento.text.toString()).child("descuento").setValue( usuario.substring(0,usuario.indexOf("@")) +" "+ binding.edtInsertarDescuento.text.toString() + " €")
        database.child("Restaurantes").child(usuario).child("listaDescuentos").child(binding.edtInsertarUsuarioDescuento.text.toString()).removeValue()
        binding.edtInsertarDescuento.text.clear()
        binding.edtInsertarUsuarioDescuento.text.clear()
        actualizarListas(usuario)

    }



}

