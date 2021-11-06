package com.medina.juanantonio.firemirror.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
class BLEDOMDevice(
    name: String,
    alias: String,
) : BleDevice(name, alias) {

    @PrimaryKey
    override var macAddress: String = ""

    @Ignore
    override var isConnected: Boolean = false

    @Ignore
    override var isDeviceLoading: Boolean = false

    @Ignore
    override var isPaired: Boolean = false

    var ledData: LEDData = LEDData()
}