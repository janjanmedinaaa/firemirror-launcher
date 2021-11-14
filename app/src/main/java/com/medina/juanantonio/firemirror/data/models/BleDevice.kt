package com.medina.juanantonio.firemirror.data.models

import java.util.*

open class BleDevice(
    val name: String,
    var alias: String,
) {

    open var macAddress: String = ""

    open var isPreviouslyConnected: Boolean = false
    open var isConnected: Boolean = false
    open var isDeviceLoading: Boolean = false
    open var isPaired: Boolean = false
    open var lastSeen = Date().time

    open val notAvailable: Boolean
        get() {
            val currentTime = Date().time
            val lastSeenDifference = currentTime - lastSeen

            return lastSeenDifference >= 2000 && !isConnected && !isDeviceLoading
        }

    fun getDeviceName() =
        if (alias.isNotEmpty()) alias else name
}