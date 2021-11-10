package com.medina.juanantonio.firemirror.data.managers

import android.content.Context
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.medina.juanantonio.firemirror.R
import kotlinx.coroutines.CompletableDeferred

object SMSManager : ISMSManager {
    private const val API_URL = "https://ws-live.txtbox.com/v1/sms/push"

    override suspend fun sendMessage(context: Context, number: String, message: String): Boolean {
        val apiKey = context.getString(R.string.txtBoxKey)
        val result = CompletableDeferred<Boolean>()
        val request = API_URL.httpPost()
        request.header(
            mapOf(
                "X-TXTBOX-Auth" to apiKey,
                "Content-Type" to "application/x-www-form-urlencoded"
            )
        )
        request.body("number=$number&message=$message")

        request.responseString { _, _, resultData ->
            result.complete(resultData is Result.Success)
        }.join()
        return result.await()
    }
}

interface ISMSManager {
    suspend fun sendMessage(context: Context, number: String, message: String): Boolean
}