package com.medina.juanantonio.firemirror.data.database.dao

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.medina.juanantonio.firemirror.data.models.TriggerRequest

class FireMirrorTypeConverters {

    @TypeConverter
    fun triggerRequestToString(triggerRequest: TriggerRequest): String {
        val gson = Gson()
        val type = object : TypeToken<TriggerRequest>() {}.type
        return gson.toJson(triggerRequest, type)
    }

    @TypeConverter
    fun stringToTriggerRequest(string: String): TriggerRequest {
        val gson = Gson()
        return gson.fromJson(string, TriggerRequest::class.java)
    }
}