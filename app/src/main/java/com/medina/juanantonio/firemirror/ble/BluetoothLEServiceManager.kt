package com.medina.juanantonio.firemirror.ble

import android.content.Context
import android.content.Intent
import android.os.Build

class BluetoothLEServiceManager(
    private val context: Context,
    private val bluetoothManager: IBluetoothLEManager
) {

    fun startBluetoothLEService() {
        if (bluetoothManager.scanning) return
        val bluetoothLEServiceIntent = Intent(context, BluetoothLEService::class.java)
        bluetoothLEServiceIntent.putExtra(
            BluetoothLEService.SERVICE_ACTION,
            BluetoothLEService.START_SERVICE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(bluetoothLEServiceIntent)
        } else {
            context.startService(bluetoothLEServiceIntent)
        }
    }

    fun stopBluetoothLEService() {
        if (!bluetoothManager.scanning) return
        val bluetoothLEServiceIntent = Intent(context, BluetoothLEService::class.java)
        bluetoothLEServiceIntent.putExtra(
            BluetoothLEService.SERVICE_ACTION,
            BluetoothLEService.STOP_SERVICE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(bluetoothLEServiceIntent)
        } else {
            context.startService(bluetoothLEServiceIntent)
        }
    }
}
