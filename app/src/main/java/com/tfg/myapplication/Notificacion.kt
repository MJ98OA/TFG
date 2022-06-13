package com.tfg.myapplication

import com.google.gson.Gson


data class Notificacion(var timeStamp:Long, var cHANNEL_ID:String, var notification_ID:Int, var informacion:String) {

    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}
