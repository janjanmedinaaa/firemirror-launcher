package com.medina.juanantonio.firemirror.data.managers

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.medina.juanantonio.firemirror.common.extensions.toQuotesList
import com.medina.juanantonio.firemirror.data.models.Quote
import kotlinx.coroutines.CompletableDeferred
import kotlin.random.Random

class QuotesManager : IQuotesManager {

    companion object {
        const val BASE_URL = "https://type.fit/api/quotes"
    }

    var quotesList = listOf<Quote>()

    override suspend fun getQuoteList(): List<Quote> {
        val resultData = CompletableDeferred<List<Quote>>()
        val request = BASE_URL.httpGet()

        request.responseString { _, _, result ->
            when (result) {
                is Result.Success -> {
                    quotesList = result.value.toQuotesList()
                    resultData.complete(quotesList)
                }
                else -> return@responseString
            }
        }.join()

        return resultData.await()
    }

    override suspend fun getRandomQuote(): Quote {
        if (quotesList.isEmpty()) getQuoteList()
        val randomIndex = Random.nextInt(0, quotesList.size - 1)
        return quotesList[randomIndex]
    }
}

interface IQuotesManager {
    suspend fun getQuoteList(): List<Quote>
    suspend fun getRandomQuote(): Quote
}