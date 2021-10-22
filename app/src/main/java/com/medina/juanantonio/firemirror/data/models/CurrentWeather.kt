package com.medina.juanantonio.firemirror.data.models

data class CurrentWeather(
    val weather: List<WeatherItem>,
    val main: Main,
    val name: String
) {

    class WeatherItem(
        val id: Int,
        val icon: String
    )

    class Main(
        val temp: String,
        val feels_like: String
    )
}