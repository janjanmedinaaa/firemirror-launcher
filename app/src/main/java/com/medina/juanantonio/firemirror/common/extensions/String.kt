package com.medina.juanantonio.firemirror.common.extensions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.medina.juanantonio.firemirror.data.models.CurrentWeather
import com.medina.juanantonio.firemirror.data.models.Quote

fun String.toCurrentWeather(): CurrentWeather {
    return Gson().fromJson(this, CurrentWeather::class.java)
}
fun String.toQuotesList(): List<Quote> {
    val myType = object : TypeToken<List<Quote>>() {}.type
    return Gson().fromJson(this, myType)
}