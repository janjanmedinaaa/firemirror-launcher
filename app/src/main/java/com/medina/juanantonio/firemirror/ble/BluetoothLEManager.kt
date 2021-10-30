package com.medina.juanantonio.firemirror.ble

import android.bluetooth.*
import android.bluetooth.BluetoothAdapter.STATE_ON
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import com.medina.juanantonio.firemirror.data.managers.IDatabaseManager
import com.medina.juanantonio.firemirror.data.models.BlueButtDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

class BluetoothLEManager(
    private val context: Context,
    private val databaseManager: IDatabaseManager
) : IBluetoothLEManager, BluetoothGattCallback() {

    companion object {
        const val BASEUS_SERVICE_UUID = "edfec62e-9910-0bac-5241-d8bda6932a2f"
        const val BASEUS_WRITE_CHARACTERISTIC_UUID = "2d86686a-53dc-25b3-0c4a-f0e10c8aec21"
        const val BASEUS_NOTIFY_CHARACTERISTIC_UUID = "15005991-b131-3396-014c-664c9867b917"

        private const val OP_CODE_BUTTON_CLICK = (0xAA08).toByte()

        const val TAG = "BluetoothLEManager"
    }

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
    override lateinit var leScanCallBack: BluetoothLeScanCallBack

    private val scanSettings =
        ScanSettings
            .Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
    private val baseusScanFilter =
        ScanFilter
            .Builder()
            .setServiceUuid(ParcelUuid.fromString(BASEUS_SERVICE_UUID))
            .build()

    internal val bleConnectionHashMap = HashMap<String, BleConnection>()
    private var bluetoothLEManagerScope = CoroutineScope(Default)

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            blueButtDeviceHashMap.let {
                val macAddress = result?.device?.address ?: return@let
                Log.d(TAG, "Scanned $macAddress")

                if (it.containsKey(macAddress)) return@let
                if (result.device == null) return@let

                bluetoothLEManagerScope.launch {
                    try {
                        val blueButtDevice =
                            databaseManager.getDevice(macAddress) ?: BlueButtDevice(
                                name = result.device.name,
                                alias = "",
                                macAddress = macAddress
                            )
                        blueButtDeviceHashMap[macAddress] = blueButtDevice
                        createBleConnection(macAddress)

                        refreshDeviceList()
                    } catch (e: Exception) {
                        Log.d(TAG, "$e")
                    }
                }
            }
        }
    }

    override var scanning = false
    override val blueButtDeviceHashMap = HashMap<String, BlueButtDevice>()

    override fun isBluetoothAvailable(): Boolean = bluetoothAdapter != null

    override fun isBluetoothEnabled(): Boolean = bluetoothAdapter?.isEnabled == true

    override fun startScan(_leScanCallBack: BluetoothLeScanCallBack) {
        scanning = true

        if (!::leScanCallBack.isInitialized) leScanCallBack = _leScanCallBack
        refreshDeviceList()
        bluetoothLeScanner?.startScan(
            listOf(baseusScanFilter),
            scanSettings,
            scanCallback
        )

        Log.d(TAG, "BLE Scanner started Scanning - $bluetoothLeScanner")
    }

    override fun stopScan() {
        scanning = false
        if (BluetoothAdapter.getDefaultAdapter().state == STATE_ON) {
            bluetoothLeScanner?.stopScan(scanCallback)
            Log.d(TAG, "BLE Scanner stopped Scanning - $bluetoothLeScanner")
        }
    }

    override fun createBleConnection(address: String) {
        Log.d(TAG, "$address createBleConnection")
        if (bleConnectionHashMap.containsKey(address)) return

        val device = bluetoothAdapter.getRemoteDevice(address)
        val bleConnection = BleConnection(
            context,
            device,
            BluetoothDeviceGattCallback()
        )
        bleConnectionHashMap[address] = bleConnection

        val blueButtDevice = blueButtDeviceHashMap[address] ?: return
        if (blueButtDevice.isPreviouslyConnected)
            bluetoothLEManagerScope.launch {
                connectDevice(address)
            }
    }

    override suspend fun connectDevice(address: String) {
        Log.d(TAG, "$address connectDevice")

        bleConnectionHashMap[address] ?: createBleConnection(address)
        val device = bleConnectionHashMap[address]
        device?.connect()

        blueButtDeviceHashMap[address]?.isDeviceLoading = true
        refreshDeviceList()

        val blueButtDevice = blueButtDeviceHashMap[address] ?: return
        databaseManager.addBlueButtDevice(blueButtDevice)
        databaseManager.updateLastConnectionStatus(address, true)
    }

    override fun bondDevice(address: String) {
        val device = bleConnectionHashMap[address] ?: return
        device.pair()
    }

    override fun unBondDevice(address: String) {
        val device = bleConnectionHashMap[address] ?: return
        device.unpair()
    }

    override suspend fun disconnectDevice(address: String) {
        Log.d(TAG, "$address disconnectDevice")

        val device = bleConnectionHashMap[address] ?: return
        device.disconnect()

        blueButtDeviceHashMap[address]?.isDeviceLoading = true
        refreshDeviceList()

        databaseManager.updateLastConnectionStatus(address, false)
    }

    override fun writeToDevice(address: String, blueButtCommand: BlueButtCommand) {
        val device = bleConnectionHashMap[address] ?: return
        val byteArray =
            when (blueButtCommand) {
                BlueButtCommand.NOTIFY_BUTTON -> byteArrayOf((0xBA).toByte(), 0x03, 0x01)
            }

        device.sendWriteCommand(byteArray)
    }

    override fun refreshDeviceList() {
        Log.d(TAG, "refreshDeviceList")
        leScanCallBack.onScanResult(blueButtDeviceHashMap)
    }

    inner class BluetoothDeviceGattCallback : BluetoothGattCallback() {
        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            Log.d(TAG, "Character Write: $status")
        }

        override fun onConnectionStateChange(
            gatt: BluetoothGatt?,
            status: Int,
            newState: Int
        ) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    gatt?.device?.address?.let {
                        blueButtDeviceHashMap[it]?.apply {
                            isConnected = true
                            isDeviceLoading = false
                        }

                        Log.d(TAG, "$it STATE_CONNECTED")
                    }
                    gatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    gatt?.close()
                    gatt?.device?.address?.let {
                        blueButtDeviceHashMap[it]?.apply {
                            isConnected = false
                            isDeviceLoading = false
                        }
                        bleConnectionHashMap.remove(it)

                        Log.d(TAG, "$it STATE_DISCONNECTED")
                    }
                }
            }
            refreshDeviceList()
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            gatt?.getService(UUID.fromString(BASEUS_SERVICE_UUID))
                ?.getCharacteristic(UUID.fromString(BASEUS_NOTIFY_CHARACTERISTIC_UUID))
                ?.run characteristic@{
                    val address = gatt.device?.address ?: return
                    val bleConnection = bleConnectionHashMap[address]
                    bleConnection?.setupGattCharacteristic(this@characteristic)
                }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            when (characteristic?.value?.get(1)) {
                OP_CODE_BUTTON_CLICK -> {
                    val device = blueButtDeviceHashMap[gatt?.device?.address] ?: return
                    leScanCallBack.onTriggerAction(device)
                }
            }
        }
    }
}

interface IBluetoothLEManager {
    var scanning: Boolean
    val blueButtDeviceHashMap: HashMap<String, BlueButtDevice>
    var leScanCallBack: BluetoothLeScanCallBack

    fun isBluetoothAvailable(): Boolean
    fun isBluetoothEnabled(): Boolean

    fun startScan(_leScanCallBack: BluetoothLeScanCallBack)
    fun stopScan()
    fun createBleConnection(address: String)
    suspend fun connectDevice(address: String)
    suspend fun disconnectDevice(address: String)
    fun bondDevice(address: String)
    fun unBondDevice(address: String)
    fun writeToDevice(address: String, blueButtCommand: BlueButtCommand)

    fun refreshDeviceList()
}

interface BluetoothLeScanCallBack {
    fun onScanResult(bluetoothDeviceList: HashMap<String, BlueButtDevice>)
    fun onTriggerAction(blueButtDevice: BlueButtDevice)
}

enum class BlueButtCommand {
    NOTIFY_BUTTON
}
