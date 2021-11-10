package com.medina.juanantonio.firemirror.common.extensions

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.medina.juanantonio.firemirror.data.models.CurrentWeather
import com.medina.juanantonio.firemirror.data.models.Lyrics
import com.medina.juanantonio.firemirror.data.models.Quote
import com.medina.juanantonio.firemirror.data.models.SpotifyAccessToken
import com.medina.juanantonio.firemirror.data.models.SpotifyCurrentTrack
import java.util.*

fun String.toCurrentWeather(): CurrentWeather {
    return Gson().fromJson(this, CurrentWeather::class.java)
}

fun String.toQuotesList(): List<Quote> {
    val myType = object : TypeToken<List<Quote>>() {}.type
    return Gson().fromJson(this, myType)
}

fun String.toSpotifyCurrentTrack(): SpotifyCurrentTrack {
    return Gson().fromJson(this, SpotifyCurrentTrack::class.java)
}

fun String.toSpotifyAccessToken(): SpotifyAccessToken {
    return Gson().fromJson(this, SpotifyAccessToken::class.java)
}

fun String.toLyrics(): Lyrics {
    return Gson().fromJson(this, Lyrics::class.java)
}

fun String.toBase64(): String {
    return Base64.encodeToString(toByteArray(), Base64.NO_WRAP)
}

fun String.toCapitalCase() = lowercase().replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}

fun String.snakeToCapitalize(): String =
    split("_").joinToString(separator = " ") { it.toCapitalCase() }