package com.tfg.myapplication

import com.google.gson.Gson

data class Usuario(var nombre: String, var puntos: Int) {

    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

}