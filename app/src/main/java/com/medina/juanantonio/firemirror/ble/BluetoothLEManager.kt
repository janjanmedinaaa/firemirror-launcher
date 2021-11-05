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
import com.medina.juanantonio.firemirror.ble.models.BLEDOMBleConnection
import com.medina.juanantonio.firemirror.ble.models.BaseusBleConnection
import com.medina.juanantonio.firemirror.ble.models.BleConnection
import com.medina.juanantonio.firemirror.data.commander.BaseusCommander
import com.medina.juanantonio.firemirror.data.managers.IBLEDOMDevicesManager
import com.medina.juanantonio.firemirror.data.managers.IBlueButtDevicesManager
import com.medina.juanantonio.firemirror.data.models.BLEDOMDevice
import com.medina.juanantonio.firemirror.data.models.BleDevice
import com.medina.juanantonio.firemirror.data.models.BlueButtDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

class BluetoothLEManager(
    private val context: Context,
    private val blueButtDevicesManager: IBlueButtDevicesManager,
    private val bleDOMDevicesManager: IBLEDOMDevicesManager
) : IBluetoothLEManager, BluetoothGattCallback() {

    companion object {
        const val BASEUS_SERVICE_UUID = "edfec62e-9910-0bac-5241-d8bda6932a2f"
        const val BASEUS_WRITE_CHARACTERISTIC_UUID = "2d86686a-53dc-25b3-0c4a-f0e10c8aec21"
        const val BASEUS_NOTIFY_CHARACTERISTIC_UUID = "15005991-b131-3396-014c-664c9867b917"

        const val BLEDOM_SERVICE_UUID = "0000fff0-0000-1000-8000-00805f9b34fb"
        const val BLEDOM_WRITE_CHARACTERISTIC_UUID = "0000fff3-0000-1000-8000-00805f9b34fb"

        const val TAG = "BluetoothLEManager"
    }

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
    override lateinit var leScanCallBack: BluetoothLeScanCallBack

    private val scanSettings =
        ScanSettings
            .Builder()
            .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
            .build()

    private val baseusScanFilter =
        ScanFilter
            .Builder()
            .setServiceUuid(ParcelUuid.fromString(BASEUS_SERVICE_UUID))
            .build()

    // The 3 spaces at the end are required based on the actual device's name
    private val bleDomScanFilter =
        ScanFilter
            .Builder()
            .setDeviceName("ELK-BLEDOM   ")
            .build()

    internal val bleConnectionHashMap = HashMap<String, BleConnection>()
    private var bluetoothLEManagerScope = CoroutineScope(Default)

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            bleDeviceHashMap.let {
                val macAddress = result?.device?.address ?: return@let
                Log.d(TAG, "Scanned $macAddress")

                if (it.containsKey(macAddress)) {
                    createBleConnection(macAddress)
                    return@let
                }
                if (result.device == null) return@let

                bluetoothLEManagerScope.launch {
                    try {
                        val bleDevice = if (result.device.isBLEDOMDevice()) {
                            bleDOMDevicesManager.getDevice(macAddress) ?: BLEDOMDevice(
                                name = result.device.name ?: macAddress,
                                alias = ""
                            ).apply {
                                this.macAddress = macAddress
                            }
                        } else {
                            blueButtDevicesManager.getDevice(macAddress) ?: BlueButtDevice(
                                name = result.device.name ?: macAddress,
                                alias = ""
                            ).apply {
                                this.macAddress = macAddress
                            }
                        }

                        bleDeviceHashMap[macAddress] = bleDevice
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
    override val bleDeviceHashMap = HashMap<String, BleDevice>()

    override fun isBluetoothAvailable(): Boolean = bluetoothAdapter != null

    override fun isBluetoothEnabled(): Boolean = bluetoothAdapter?.isEnabled == true

    override fun startScan(_leScanCallBack: BluetoothLeScanCallBack) {
        scanning = true

        if (!::leScanCallBack.isInitialized) leScanCallBack = _leScanCallBack
        refreshDeviceList()
        bluetoothLeScanner?.startScan(
            listOf(baseusScanFilter, bleDomScanFilter),
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
        val bleConnection =
            if (device.isBLEDOMDevice()) {
                BLEDOMBleConnection(
                    context,
                    device,
                    BluetoothDeviceGattCallback()
                )
            } else {
                BaseusBleConnection(
                    context,
                    device,
                    BluetoothDeviceGattCallback()
                )
            }
        bleConnectionHashMap[address] = bleConnection

        val bleDevice = bleDeviceHashMap[address] ?: return
        if (bleDevice.isPreviouslyConnected)
            bluetoothLEManagerScope.launch {
                connectDevice(address)
            }
    }

    override suspend fun connectDevice(address: String) {
        Log.d(TAG, "$address connectDevice")

        bleConnectionHashMap[address] ?: createBleConnection(address)
        val device = bleConnectionHashMap[address]
        device?.connect()

        bleDeviceHashMap[address]?.isDeviceLoading = true
        refreshDeviceList()

        val bleDevice = bleDeviceHashMap[address] ?: return
        when (bleDevice) {
            is BlueButtDevice -> {
                blueButtDevicesManager.addDevice(bleDevice)
                blueButtDevicesManager.updateLastConnectionStatus(address, true)
            }
            is BLEDOMDevice -> {
                bleDOMDevicesManager.addDevice(bleDevice)
                bleDOMDevicesManager.updateLastConnectionStatus(address, true)
            }
        }
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

        val bleDevice = bleDeviceHashMap[address] ?: return
        when (bleDevice) {
            is BlueButtDevice -> {
                blueButtDevicesManager.updateLastConnectionStatus(address, false)
            }
            is BLEDOMDevice -> {
                bleDOMDevicesManager.updateLastConnectionStatus(address, false)
            }
        }

        val device = bleConnectionHashMap[address] ?: return
        device.disconnect()

        bleDeviceHashMap[address]?.isDeviceLoading = true
        refreshDeviceList()
    }

    override fun writeToDevice(address: String, command: ByteArray) {
        val device = bleConnectionHashMap[address] ?: return
        device.sendWriteCommand(command)
    }

    override fun refreshDeviceList(bleDevice: BleDevice?) {
        Log.d(TAG, "refreshDeviceList")
        bleDevice?.run {
            bleDeviceHashMap[macAddress]?.apply {
                alias = this@run.alias

                if (this is BlueButtDevice
                    && this@run is BlueButtDevice
                ) {
                    triggerRequestOff = this@run.triggerRequestOff
                    triggerRequestOn = this@run.triggerRequestOn
                }
            }
        }

        leScanCallBack.onScanResult(bleDeviceHashMap)
    }

    private fun BluetoothDevice.isBLEDOMDevice(): Boolean =
        name.startsWith("ELK-BLEDOM")

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
                        bleDeviceHashMap[it]?.apply {
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
                        bleDeviceHashMap[it]?.apply {
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
            gatt?.services?.forEach { service ->
                service.characteristics?.forEach { characteristic ->
                    Log.d(TAG, "${gatt.device?.name} ${service.uuid} ${characteristic.uuid}")
                }
            }

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
                BaseusCommander.NotificationCommand.OP_CODE_BUTTON_CLICK -> {
                    val device = bleDeviceHashMap[gatt?.device?.address] ?: return
                    leScanCallBack.onTriggerAction(device)
                }
            }
        }
    }
}

interface IBluetoothLEManager {
    var scanning: Boolean
    val bleDeviceHashMap: HashMap<String, BleDevice>
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
    fun writeToDevice(address: String, command: ByteArray)

    fun refreshDeviceList(bleDevice: BleDevice? = null)
}

interface BluetoothLeScanCallBack {
    fun onScanResult(bluetoothDeviceList: HashMap<String, BleDevice>)
    fun onTriggerAction(bleDevice: BleDevice)
}
