package com.medina.juanantonio.firemirror.ble.models

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.content.Context
import com.medina.juanantonio.firemirror.common.extensions.removeBond

open class BleConnection(
    private val context: Context,
    private val bluetoothDevice: BluetoothDevice,
    private val gattCallback: BluetoothGattCallback
) {
    protected var gatt: BluetoothGatt? = null

    fun setupGattCharacteristic(characteristic: BluetoothGattCharacteristic) {
        with(characteristic) {
            value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            descriptors.firstOrNull()?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt?.let {
                it.setCharacteristicNotification(this, true)
                it.writeDescriptor(descriptors.firstOrNull())
            }
        }
    }

    fun connect() {
        gatt = bluetoothDevice.connectGatt(context, true, gattCallback)
    }

    fun disconnect() {
        gatt?.disconnect()
    }

    fun pair() {
        bluetoothDevice.createBond()
    }

    fun unpair() {
        bluetoothDevice.removeBond()
    }

    open fun sendWriteCommand(command: ByteArray) {}
}
