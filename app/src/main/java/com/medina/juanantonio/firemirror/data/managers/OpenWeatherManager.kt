package com.medina.juanantonio.firemirror.data.managers

import android.content.Context
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.medina.juanantonio.firemirror.R
import com.medina.juanantonio.firemirror.common.extensions.toCurrentWeather
import com.medina.juanantonio.firemirror.data.models.CurrentWeather
import kotlinx.coroutines.CompletableDeferred

class OpenWeatherManager(context: Context) : IOpenWeatherManager {

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5"
        var API_KEY = ""
    }

    init {
        API_KEY = context.getString(R.string.openWeatherKey)
    }

    override suspend fun getCurrentWeather(zipCode: String, countryCode: String): CurrentWeather {
        val currentWeatherAPI = "$BASE_URL/weather"
        val resultData = CompletableDeferred<CurrentWeather>()
        val request = currentWeatherAPI.httpGet(
            parameters = listOf(
                Pair("zip", "$zipCode,$countryCode"),
                Pair("appid", API_KEY),
                Pair("units", "metric")
            )
        )

        request.responseString { _, _, result ->
            when (result) {
                is Result.Success -> {
                    resultData.complete(result.value.toCurrentWeather())
                }
                else -> return@responseString
            }
        }.join()

        return resultData.await()
    }
}

interface IOpenWeatherManager {
    suspend fun getCurrentWeather(zipCode: String, countryCode: String): CurrentWeather
}