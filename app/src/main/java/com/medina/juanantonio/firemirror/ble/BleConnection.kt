package com.medina.juanantonio.firemirror.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
import android.bluetooth.BluetoothGattDescriptor
import android.content.Context
import com.medina.juanantonio.firemirror.ble.BluetoothLEManager.Companion.BASEUS_SERVICE_UUID
import com.medina.juanantonio.firemirror.ble.BluetoothLEManager.Companion.BASEUS_WRITE_CHARACTERISTIC_UUID
import com.medina.juanantonio.firemirror.common.extensions.removeBond
import java.util.*

class BleConnection(
    private val context: Context,
    private val bluetoothDevice: BluetoothDevice,
    private val gattCallback: BluetoothGattCallback
) {
    private var gatt: BluetoothGatt? = null

    val isAutoConnectEnabled
        get() = bluetoothDevice.bondState == BluetoothDevice.BOND_BONDED

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

    fun sendWriteCommand(command: ByteArray) {
        gatt?.getService(UUID.fromString(BASEUS_SERVICE_UUID))
            ?.getCharacteristic(UUID.fromString(BASEUS_WRITE_CHARACTERISTIC_UUID))
            ?.run characteristic@{
                writeType = WRITE_TYPE_DEFAULT
                value = command
                gatt?.writeCharacteristic(this@characteristic)
            }
    }
}
