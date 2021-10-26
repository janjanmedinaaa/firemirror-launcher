package com.medina.juanantonio.firemirror.data.managers

import android.net.Uri
import android.util.Log
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.medina.juanantonio.firemirror.common.extensions.toLyrics
import com.medina.juanantonio.firemirror.data.models.Lyrics
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object LyricsManager : ILyricsManager {
    private const val BASE_URL = "https://api.lyrics.ovh/v1"
    private const val TAG = "LyricsManager"

    override suspend fun getSongLyrics(artist: String, title: String, index: Int): Lyrics {
        val multipleArtists = artist.split(",").map { it.trim() }
        val encodedArtist = Uri.encode(multipleArtists[index])
        val encodedTitle = Uri.encode(title)
        val lyricsAPI = "$BASE_URL/$encodedArtist/$encodedTitle"
        val resultData = CompletableDeferred<Lyrics>()
        val request = lyricsAPI.httpGet()

        Log.d(TAG, "$title, $artist $lyricsAPI")

        request.responseString { _, _, result ->
            when (result) {
                is Result.Success -> {
                    resultData.complete(result.value.toLyrics())
                }
                is Result.Failure -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (index + 1 > multipleArtists.size - 1) {
                            resultData.complete(
                                Lyrics(null, "No lyrics found")
                            )
                        } else {
                            resultData.complete(
                                getSongLyrics(artist, title, index + 1)
                            )
                        }
                    }
                }
            }
        }.join()

        return resultData.await()
    }

    override fun formatLyrics(lyrics: String): List<String> {
        val regexSingerPart = """\[(.*?)]""".toRegex()
        return lyrics
            .split("\n")
            .filter { it.isNotEmpty() }
            .filterNot { regexSingerPart.matches(it) }
            .filterNot { it.startsWith("Paroles") }
            .map { it.removeSurrounding("\\r") }
    }
}

interface ILyricsManager {
    suspend fun getSongLyrics(artist: String, title: String, index: Int = 0): Lyrics
    fun formatLyrics(lyrics: String): List<String>
}