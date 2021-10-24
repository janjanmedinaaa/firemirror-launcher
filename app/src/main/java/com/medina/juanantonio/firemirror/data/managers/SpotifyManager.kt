package com.medina.juanantonio.firemirror.data.managers

import android.app.Activity
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.medina.juanantonio.firemirror.common.extensions.toSpotifyCurrentTrack
import com.medina.juanantonio.firemirror.data.models.SpotifyCurrentTrack
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.coroutines.CompletableDeferred

class SpotifyManager: ISpotifyManager {

    companion object {
        const val CLIENT_ID = "81aa36c56a724f2480057cfd35861ff8"
        const val REQUEST_CODE = 1337
        const val REDIRECT_URL = "com.medina.juanantonio.firemirror://callback"
        private const val CURRENT_PLAYING_TRACK_URL =
            "https://api.spotify.com/v1/me/player/currently-playing"
    }

    override fun authenticate(activity: Activity) {
        val builder = AuthorizationRequest.Builder(
            CLIENT_ID,
            AuthorizationResponse.Type.TOKEN,
            REDIRECT_URL
        ).setScopes(arrayOf("user-read-playback-state"))
        val request = builder.build()
        AuthorizationClient.openLoginActivity(activity, REQUEST_CODE, request)
    }

    override suspend fun getUserCurrentTrack(token: String): Pair<Int, SpotifyCurrentTrack?> {
        val result = CompletableDeferred<Pair<Int, SpotifyCurrentTrack?>>()
        val request = CURRENT_PLAYING_TRACK_URL.httpGet()
        request.header(mapOf("Authorization" to "Bearer $token"))

        request.responseString { _, response, resultData ->
            when (resultData) {
                is Result.Success -> {
                    if (resultData.value.isNotEmpty()) {
                        val currentTrack = resultData.value.toSpotifyCurrentTrack()
                        result.complete(Pair(response.statusCode, currentTrack))
                    } else {
                        result.complete(Pair(response.statusCode, null))
                    }
                }
                else -> {
                    result.complete(Pair(response.statusCode, null))
                }
            }
        }.join()
        return result.await()
    }
}

interface ISpotifyManager {
    fun authenticate(activity: Activity)
    suspend fun getUserCurrentTrack(token: String): Pair<Int, SpotifyCurrentTrack?>
}