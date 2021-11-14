package com.medina.juanantonio.firemirror.data.managers

import android.content.Context
import com.medina.juanantonio.firemirror.R
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.Channel
import com.pusher.client.channel.PusherEvent
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import kotlinx.coroutines.CompletableDeferred

class PusherManager(context: Context) : IPusherManager {
    private val options = PusherOptions().apply {
        setCluster(context.getString(R.string.pusherCluster))
    }
    private val pusher = Pusher(
        context.getString(R.string.pusherKey),
        options
    )

    override suspend fun connect(): Boolean {
        val result = CompletableDeferred<Boolean>()
        pusher.connect(
            object : ConnectionEventListener {
                override fun onConnectionStateChange(change: ConnectionStateChange?) {
                    if (change?.currentState == ConnectionState.CONNECTED)
                        result.complete(true)
                }

                override fun onError(message: String?, code: String?, e: Exception?) {
                    result.complete(false)
                }
            }
        )

        return result.await()
    }

    override fun subscribe(channelName: String): Channel {
        return pusher.subscribe(channelName)
    }

    override fun subscribe(
        channelName: String,
        eventName: String,
        action: (PusherEvent) -> Unit
    ): Channel {
        return try {
            pusher.subscribe(channelName).apply {
                bind(eventName, action)
            }
        } catch (e: Exception) {
            pusher.getChannel(channelName).apply {
                bind(eventName, action)
            }
        }
    }

    override fun unsubscribe(channelName: String) {
        pusher.unsubscribe(channelName)
    }

    override fun disconnect() {
        pusher.disconnect()
    }
}

interface IPusherManager {
    suspend fun connect(): Boolean
    fun subscribe(channelName: String): Channel
    fun subscribe(
        channelName: String,
        eventName: String,
        action: (PusherEvent) -> Unit
    ): Channel

    fun unsubscribe(channelName: String)
    fun disconnect()
}