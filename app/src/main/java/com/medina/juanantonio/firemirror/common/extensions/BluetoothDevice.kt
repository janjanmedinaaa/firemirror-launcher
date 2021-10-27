package com.medina.juanantonio.firemirror.common.extensions

import android.bluetooth.BluetoothDevice
import android.util.Log
import com.medina.juanantonio.firemirror.ble.BluetoothLEManager

fun BluetoothDevice.removeBond() {
    try {
        javaClass.getMethod("removeBond").invoke(this)
    } catch (e: Exception) {
        Log.e(BluetoothLEManager.TAG, "Removing bond has been failed. ${e.message}")
    }
}