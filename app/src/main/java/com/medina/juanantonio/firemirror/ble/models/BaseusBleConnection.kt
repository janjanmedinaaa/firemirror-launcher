package com.medina.juanantonio.firemirror.ble.models

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
import android.content.Context
import com.medina.juanantonio.firemirror.ble.BluetoothLEManager.Companion.BASEUS_SERVICE_UUID
import com.medina.juanantonio.firemirror.ble.BluetoothLEManager.Companion.BASEUS_WRITE_CHARACTERISTIC_UUID
import java.util.*

class BaseusBleConnection(
    context: Context,
    bluetoothDevice: BluetoothDevice,
    gattCallback: BluetoothGattCallback
): BleConnection(context, bluetoothDevice, gattCallback) {

    override fun sendWriteCommand(command: ByteArray) {
        gatt?.getService(UUID.fromString(BASEUS_SERVICE_UUID))
            ?.getCharacteristic(UUID.fromString(BASEUS_WRITE_CHARACTERISTIC_UUID))
            ?.run characteristic@{
                writeType = WRITE_TYPE_DEFAULT
                value = command
                gatt?.writeCharacteristic(this@characteristic)
            }
    }
}