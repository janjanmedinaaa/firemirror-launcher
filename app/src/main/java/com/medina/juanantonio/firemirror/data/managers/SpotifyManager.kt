package com.medina.juanantonio.firemirror.data.managers

import android.app.Activity
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.medina.juanantonio.firemirror.common.extensions.toBase64
import com.medina.juanantonio.firemirror.common.extensions.toSpotifyAccessToken
import com.medina.juanantonio.firemirror.common.extensions.toSpotifyCurrentTrack
import com.medina.juanantonio.firemirror.data.models.SpotifyAccessToken
import com.medina.juanantonio.firemirror.data.models.SpotifyCurrentTrack
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.coroutines.CompletableDeferred
import kotlin.random.Random

class SpotifyManager : ISpotifyManager {

    companion object {
        const val CLIENT_ID = "2e6ea4b25ad643018f0a2d738445216d"
        const val CLIENT_SECRET = "4d5f4eced09943a8b0556539c58f8923"

        const val REQUEST_CODE = 1337
        const val REDIRECT_URL = "com.medina.juanantonio.firemirror://callback"

        private const val CURRENT_PLAYING_TRACK_URL =
            "https://api.spotify.com/v1/me/player/currently-playing"
        private const val REQUEST_ACCESS_TOKEN_URL =
            "https://accounts.spotify.com/api/token"

        const val TAG = "SpotifyManager"
    }

    override fun authenticate(activity: Activity, externalBrowser: Boolean): String {
        val state = Random.nextInt(100000, 999999).toString()
        val builder = AuthorizationRequest.Builder(
            CLIENT_ID,
            AuthorizationResponse.Type.CODE,
            REDIRECT_URL
        ).setScopes(arrayOf("user-read-playback-state"))
            .setState(state)
        val request = builder.build()

        if (externalBrowser)
            AuthorizationClient.openLoginInBrowser(activity, request)
        else
            AuthorizationClient.openLoginActivity(activity, REQUEST_CODE, request)

        return state
    }

    override suspend fun requestAccessToken(code: String): SpotifyAccessToken? {
        val result = CompletableDeferred<SpotifyAccessToken?>()
        val request = REQUEST_ACCESS_TOKEN_URL.httpPost()
        request.header(
            mapOf(
                "Authorization" to "Basic ${("$CLIENT_ID:$CLIENT_SECRET").toBase64()}",
                "Content-Type" to "application/x-www-form-urlencoded"
            )
        )
        request.body("grant_type=authorization_code&code=$code&redirect_uri=$REDIRECT_URL")

        request.responseString { _, _, resultData ->
            when (resultData) {
                is Result.Success -> {
                    val accessToken = resultData.value.toSpotifyAccessToken()
                    result.complete(accessToken)
                }
                else -> {
                    result.complete(null)
                }
            }
        }.join()
        return result.await()
    }

    override suspend fun refreshAccessToken(refreshToken: String): SpotifyAccessToken? {
        val result = CompletableDeferred<SpotifyAccessToken?>()
        val request = REQUEST_ACCESS_TOKEN_URL.httpPost()
        request.header(
            mapOf(
                "Authorization" to "Basic ${("$CLIENT_ID:$CLIENT_SECRET").toBase64()}",
                "Content-Type" to "application/x-www-form-urlencoded"
            )
        )
        request.body("grant_type=refresh_token&refresh_token=$refreshToken")

        request.responseString { _, _, resultData ->
            when (resultData) {
                is Result.Success -> {
                    val accessToken = resultData.value.toSpotifyAccessToken()
                    result.complete(accessToken)
                }
                else -> {
                    result.complete(null)
                }
            }
        }.join()
        return result.await()
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
    fun authenticate(activity: Activity, externalBrowser: Boolean = true): String
    suspend fun requestAccessToken(code: String): SpotifyAccessToken?
    suspend fun refreshAccessToken(refreshToken: String): SpotifyAccessToken?
    suspend fun getUserCurrentTrack(token: String): Pair<Int, SpotifyCurrentTrack?>
}