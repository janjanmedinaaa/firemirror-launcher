package com.medina.juanantonio.firemirror.data.models

import com.google.gson.Gson

data class CurrentWeather(
    val coord: Coordinates,
    val main: Main
) {

    class Main(
        val temp: String,
        val feels_like: String,
        val temp_min: String,
        val temp_max: String,
        val pressure: String,
        val humidity: String
    )

    class Coordinates(
        val lon: String,
        val lat: String
    )
}

fun String.toCurrentWeather(): CurrentWeather {
    return Gson().fromJson(this, CurrentWeather::class.java)
}