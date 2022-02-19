package com.medina.juanantonio.firemirror.data.models

open class BleDevice(
    val name: String,
    var alias: String,
) {

    open var macAddress: String = ""

    open var isPreviouslyConnected: Boolean = false
    open var isConnected: Boolean = false
    open var isDeviceLoading: Boolean = false
    open var isPaired: Boolean = false
    open var lastSeen = System.currentTimeMillis()

    open val notAvailable: Boolean
        get() {
            val currentTime = System.currentTimeMillis()
            val lastSeenDifference = currentTime - lastSeen

            return lastSeenDifference >= 4000 && !isConnected && !isDeviceLoading
        }

    fun getDeviceName() =
        if (alias.isNotEmpty()) alias else name
}