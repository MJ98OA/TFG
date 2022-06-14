package com.tfg.myapplication

import android.annotation.SuppressLint
import android.content.Context
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

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        firebaseAnalytics = Firebase.analytics
        firebaseRealTimeData = Firebase.database
        super.onCreate(savedInstanceState)
        binding = MenurestaurantesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle: Bundle? = intent.extras
        var usuario: String? = bundle?.getString("Usuario")

        obtenerListas(usuario.toString())

        binding.actualizarLista.setOnClickListener {
            obtenerListas(usuario.toString())
        }

        binding.btEnvioPuntos.setOnClickListener {
            darPuntos(usuario.toString())
            binding.edtPuntosaDar.text.clear()
            binding.edtUsuarioSumPuntos.text.clear()
        }

        binding.btDescuentoPuntos.setOnClickListener {
            darDescuento(usuario.toString())
            binding.edtInsertarDescuento.text.clear()
            binding.edtInsertarUsuarioDescuento.text.clear()
        }

        binding.btnLogOutR.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

    }



    @SuppressLint("SetTextI18n")
    fun obtenerListas(usuario:String){

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
        if(binding.edtUsuarioSumPuntos.text.isNotEmpty() && binding.edtPuntosaDar.text.isNotEmpty()){
            var puntosActuales=0
            database.child("Usuarios").child(binding.edtUsuarioSumPuntos.text.toString()).child("puntos").get().addOnSuccessListener {
                puntosActuales=it.value.toString().toInt()
            }
            database.child("Usuarios").child(binding.edtUsuarioSumPuntos.text.toString()).child("puntos").setValue(
                (binding.edtPuntosaDar.text.toString().toInt()+puntosActuales).toString())
        }
        database.child("Restaurantes").child(usuario).child("listapuntos").child(binding.edtUsuarioSumPuntos.text.toString()).removeValue()
    }

    fun darDescuento(usuario: String) {
        if(binding.edtInsertarUsuarioDescuento.text.isNotEmpty() && binding.edtInsertarDescuento.text.isNotEmpty()){

            database.child("Usuarios").child(binding.edtInsertarUsuarioDescuento.text.toString()).child("descuento").setValue( usuario.substring(0,usuario.indexOf("@")) +" "+ binding.edtInsertarDescuento.text.toString() + " €")

        }
        database.child("Restaurantes").child(usuario).child("listaDescuentos").child(binding.edtUsuarioSumPuntos.text.toString()).removeValue()
    }



}

