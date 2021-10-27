package com.medina.juanantonio.firemirror.data.models

import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import com.github.kittinunf.result.Result
import kotlinx.coroutines.CompletableDeferred

data class TriggerRequest(
    val url: String,
    val requestType: RequestType,
    val headers: List<KeyValue>,
    val params: List<KeyValue>
) {

    companion object {

        fun default(): TriggerRequest {
            return TriggerRequest(
                url = "",
                requestType = RequestType.GET,
                headers = listOf(),
                params = listOf()
            )
        }
    }

    suspend fun run(): String {
        if (url.isBlank()) return ""

        val resultData = CompletableDeferred<String>()
        val paramsPairs = params.map { it.toPair() }
        val request = when (requestType) {
            RequestType.GET -> url.httpGet(paramsPairs)
            RequestType.POST -> url.httpPost(paramsPairs)
            RequestType.PUT -> url.httpPut(paramsPairs)
            RequestType.DELETE -> url.httpDelete(paramsPairs)
        }

        val headerMap = headers.map { it.toPair() }.toMap()
        request.header(headerMap)
            .responseString { _, _, result ->
                when (result) {
                    is Result.Success -> {
                        resultData.complete(result.value)
                    }
                    else -> return@responseString
                }
            }
            .join()

        return resultData.await()
    }

    enum class RequestType {
        GET,
        POST,
        PUT,
        DELETE
    }

    class KeyValue(
        private var key: String,
        private var value: String
    ) {

        fun toPair(): Pair<String, String> {
            return Pair(key, value)
        }
    }
}