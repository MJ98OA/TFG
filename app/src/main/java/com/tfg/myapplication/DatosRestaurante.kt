package com.tfg.myapplication

import com.google.gson.Gson

data class DatosRestaurante(var estrellas:String, var latitud:Double,var longitud:Double) {

    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

}