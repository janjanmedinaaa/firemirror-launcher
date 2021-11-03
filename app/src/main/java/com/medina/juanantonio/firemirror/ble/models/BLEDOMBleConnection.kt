package com.medina.juanantonio.firemirror.ble.models

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
import android.content.Context
import com.medina.juanantonio.firemirror.ble.BluetoothLEManager.Companion.BLEDOM_SERVICE_UUID
import com.medina.juanantonio.firemirror.ble.BluetoothLEManager.Companion.BLEDOM_WRITE_CHARACTERISTIC_UUID
import java.util.*

class BLEDOMBleConnection(
    context: Context,
    bluetoothDevice: BluetoothDevice,
    gattCallback: BluetoothGattCallback
) : BleConnection(context, bluetoothDevice, gattCallback) {

    override fun sendWriteCommand(command: ByteArray) {
        gatt?.getService(UUID.fromString(BLEDOM_SERVICE_UUID))
            ?.getCharacteristic(UUID.fromString(BLEDOM_WRITE_CHARACTERISTIC_UUID))
            ?.run characteristic@{
                writeType = WRITE_TYPE_DEFAULT
                value = command
                gatt?.writeCharacteristic(this@characteristic)
            }
    }
}