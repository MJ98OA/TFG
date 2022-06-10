package com.tfg.myapplication

import com.google.gson.Gson

data class Usuario(var nombre: String, var puntos: Int, var tipo:Boolean) {

    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

}