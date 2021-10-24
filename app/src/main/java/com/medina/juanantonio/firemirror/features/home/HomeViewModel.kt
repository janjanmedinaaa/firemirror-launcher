package com.medina.juanantonio.firemirror.features.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medina.juanantonio.firemirror.data.managers.OpenWeatherManager
import com.medina.juanantonio.firemirror.data.managers.QuotesManager
import com.medina.juanantonio.firemirror.data.models.CurrentWeather
import com.medina.juanantonio.firemirror.data.models.Quote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private var weatherTimerTask: TimerTask? = null

    val currentWeather = MutableLiveData<CurrentWeather>()
    val quote = MutableLiveData<Quote>()
    val spotifyAccessToken = MutableLiveData<String>()

    fun setupWeatherTimerTask() {
        weatherTimerTask = Timer().schedule(0, 900000) {
            viewModelScope.launch {
                getCurrentWeather()
            }

            viewModelScope.launch(Dispatchers.Main) {
                quote.value = QuotesManager.getRandomQuote()
            }
        }
    }

    fun destroyTimerTask() {
        weatherTimerTask?.cancel()
        weatherTimerTask = null
    }

    private suspend fun getCurrentWeather() {
        val currentWeatherResult =
            OpenWeatherManager.getCurrentWeather("1105", "PH")

        withContext(Dispatchers.Main) {
            currentWeather.value = currentWeatherResult
        }
    }
}