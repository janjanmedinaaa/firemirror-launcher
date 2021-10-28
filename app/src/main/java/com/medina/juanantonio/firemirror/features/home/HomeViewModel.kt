package com.medina.juanantonio.firemirror.features.home

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medina.juanantonio.firemirror.common.Constants.PreferencesKey.SPOTIFY_ACCESS_TOKEN
import com.medina.juanantonio.firemirror.common.Constants.PreferencesKey.SPOTIFY_REFRESH_TOKEN
import com.medina.juanantonio.firemirror.data.managers.*
import com.medina.juanantonio.firemirror.data.models.CurrentWeather
import com.medina.juanantonio.firemirror.data.models.Quote
import com.medina.juanantonio.firemirror.data.models.SpotifyCurrentTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule

@HiltViewModel
class HomeViewModel @Inject constructor(
    private var spotifyManager: ISpotifyManager,
    private var dataStoreManager: IDataStoreManager
) : ViewModel() {

    private var weatherTimerTask: TimerTask? = null
    private var spotifyJob: Job? = null
    private var lyricsJob: Job? = null

    val currentWeather = MutableLiveData<CurrentWeather>()
    val quote = MutableLiveData<Quote>()
    val spotifyCode = MutableLiveData<String>()
    val spotifyAccessToken = MutableLiveData<String>()
    val spotifyRefreshToken = MutableLiveData<String>()
    val songLyrics = MutableLiveData(arrayListOf<String>())

    var isSpotifyRequestPending = false
    var currentLyricsTitle = ""

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

    fun getSongLyrics(currentTrack: SpotifyCurrentTrack) {
        if (lyricsJob?.isActive == true) return
        if (currentTrack.songName == currentLyricsTitle) return
        currentLyricsTitle = currentTrack.songName
        lyricsJob = viewModelScope.launch(Dispatchers.IO) {
            val lyrics = LyricsManager.getSongLyrics(
                artist = currentTrack.artist,
                title = currentTrack.songName
            )

            withContext(Dispatchers.Main) {
                songLyrics.value = LyricsManager.formatLyrics(lyrics.lyrics ?: "")
            }
        }
    }

    fun requestUserCurrentTrack(
        activity: Activity,
        authenticate: Boolean,
        onCurrentTrack: (SpotifyCurrentTrack?) -> Unit
    ) {
        if (spotifyJob?.isActive == true) return
        spotifyJob = viewModelScope.launch(Dispatchers.IO) {
            val token = dataStoreManager.getString(SPOTIFY_ACCESS_TOKEN)
            if (token.isEmpty()) {
                if (!authenticate) return@launch
                spotifyManager.authenticate(activity)
                isSpotifyRequestPending = true
            } else {
                val (requestCode, currentTrack) =
                    spotifyManager.getUserCurrentTrack(token)

                if (requestCode == 401) {
                    isSpotifyRequestPending = true
                    refreshAccessToken(dataStoreManager.getString(SPOTIFY_REFRESH_TOKEN))
                } else {
                    Timer().schedule(500) {
                        requestUserCurrentTrack(activity, authenticate, onCurrentTrack)
                    }
                }

                onCurrentTrack(currentTrack)
                Log.d(SpotifyManager.TAG, "$requestCode, ${currentTrack?.songName}")
            }
        }
    }

    suspend fun requestAccessToken(code: String) {
        val result = spotifyManager.requestAccessToken(code)

        withContext(Dispatchers.Main) {
            spotifyAccessToken.value = result?.access_token
            result?.refresh_token?.let {
                spotifyRefreshToken.value = it
            }
        }
    }

    private suspend fun refreshAccessToken(refreshToken: String) {
        val result = spotifyManager.refreshAccessToken(refreshToken)

        withContext(Dispatchers.Main) {
            spotifyAccessToken.value = result?.access_token
        }
    }

    private suspend fun getCurrentWeather() {
        val currentWeatherResult =
            OpenWeatherManager.getCurrentWeather("1105", "PH")

        withContext(Dispatchers.Main) {
            currentWeather.value = currentWeatherResult
        }
    }
}