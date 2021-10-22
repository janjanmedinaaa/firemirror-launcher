package com.medina.juanantonio.firemirror.features.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medina.juanantonio.firemirror.data.managers.IOpenWeatherManager
import com.medina.juanantonio.firemirror.data.managers.IQuotesManager
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
class HomeViewModel @Inject constructor(
    private val openWeatherManager: IOpenWeatherManager,
    private val quotesManager: IQuotesManager
) : ViewModel() {

    private var weatherTimerTask: TimerTask? = null

    val currentWeather = MutableLiveData<CurrentWeather>()
    val quote = MutableLiveData<Quote>()

    fun setupWeatherTimerTask() {
        weatherTimerTask = Timer().schedule(0, 900000) {
            viewModelScope.launch {
                getCurrentWeather()
            }

            viewModelScope.launch(Dispatchers.Main) {
                quote.value = quotesManager.getRandomQuote()
            }
        }
    }

    fun destroyTimerTask() {
        weatherTimerTask?.cancel()
        weatherTimerTask = null
    }

    private suspend fun getCurrentWeather() {
        val currentWeatherResult =
            openWeatherManager.getCurrentWeather("1105", "PH")

        withContext(Dispatchers.Main) {
            currentWeather.value = currentWeatherResult
        }
    }
}