package com.tfg.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tfg.myapplication.databinding.MenurestaurantesBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MenuRestaurantes : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var binding: MenurestaurantesBinding
    private lateinit var firebaseRealTimeData: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        firebaseAnalytics = Firebase.analytics
        firebaseRealTimeData = Firebase.database
        super.onCreate(savedInstanceState)
        binding = MenurestaurantesBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }




}

