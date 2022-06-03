package com.tfg.myapplication

import com.google.gson.Gson

data class DatosRestaurante(var coordenadas:String, var estrellas: String,  var nombre:String, var precioMedio:String) {

    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

}