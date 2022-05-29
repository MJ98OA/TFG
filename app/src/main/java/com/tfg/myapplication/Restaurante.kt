package com.tfg.myapplication

import com.google.gson.Gson

data class Restaurante(var nombre: String, var telefono: Int) {

    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

}