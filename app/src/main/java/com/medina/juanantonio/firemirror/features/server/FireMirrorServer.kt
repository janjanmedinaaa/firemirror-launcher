package com.medina.juanantonio.firemirror.features.server

import android.content.Context
import android.util.Log
import com.medina.juanantonio.firemirror.R
import com.medina.juanantonio.firemirror.common.Constants
import fi.iki.elonen.NanoHTTPD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FireMirrorServer(
    private val context: Context,
    port: Int
) : NanoHTTPD(port) {

    companion object {
        const val TAG = "FireMirrorServer"
    }

    override fun serve(session: IHTTPSession?): Response {
        return when {
            session?.method == Method.GET
                    && session.uri == "/" -> {

                CoroutineScope(Dispatchers.Main).launch {
                    session.parameters.forEach { (t, u) ->
                        u.forEach {
                            Log.d("DEVELOP", "$t $it")
                        }
                    }
                }

                newFixedLengthResponse(
                    Constants.Server.getHtmlForm(
                        deviceName = "Device Name",
                        alias = "Alias",
                        triggerUrlOff = "Test URL Off",
                        triggerUrlOn = "Test URL On"
                    )
                )
            }
            else -> {
                newFixedLengthResponse(
                    "<html><body><h1>Fire Mirror is running.</h1></body></html>"
                )
            }
        }
    }

    override fun start() {
        try {
            super.start()
            Log.d(TAG, "FireMirrorServer started.")
        } catch (e: Exception) {
            Log.d(TAG, "${e.message}")
        }
    }
}