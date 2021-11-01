package com.medina.juanantonio.firemirror.data.managers

import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import kotlinx.coroutines.CompletableDeferred

object SMSManager : ISMSManager {
    private const val API_KEY = ""
    private const val API_URL = "https://ws-live.txtbox.com/v1/sms/push"

    override suspend fun sendMessage(number: String, message: String): Boolean {
        val result = CompletableDeferred<Boolean>()
        val request = API_URL.httpPost()
        request.header(
            mapOf(
                "X-TXTBOX-Auth" to API_KEY,
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
    suspend fun sendMessage(number: String, message: String): Boolean
}