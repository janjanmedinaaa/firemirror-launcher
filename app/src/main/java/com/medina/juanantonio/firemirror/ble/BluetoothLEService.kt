package com.medina.juanantonio.firemirror.ble

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.medina.juanantonio.firemirror.R
import com.medina.juanantonio.firemirror.data.commander.BLEDOMCommander
import com.medina.juanantonio.firemirror.data.managers.IBLEDOMDevicesManager
import com.medina.juanantonio.firemirror.data.managers.IBlueButtDevicesManager
import com.medina.juanantonio.firemirror.data.managers.IPusherManager
import com.medina.juanantonio.firemirror.data.models.BLEDOMDevice
import com.medina.juanantonio.firemirror.data.models.BleDevice
import com.medina.juanantonio.firemirror.data.models.BlueButtDevice
import com.medina.juanantonio.firemirror.features.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class BluetoothLEService : LifecycleService(), BluetoothLeScanCallBack {

    companion object {
        const val SERVICE_ACTION = "SERVICE_ACTION"
        const val START_SERVICE = "START_SERVICE"
        const val STOP_SERVICE = "STOP_SERVICE"
        const val SCANNED_DEVICES = "SCANNED_DEVICES"
        const val BLE_DEVICES = "BLE_DEVICES"

        const val TAG = "bluetoothLEService"
    }

    private val localBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(this)
    }

    @Inject
    lateinit var bluetoothLEManager: IBluetoothLEManager

    @Inject
    lateinit var blueButtDevicesManager: IBlueButtDevicesManager

    @Inject
    lateinit var bleDomDevicesManager: IBLEDOMDevicesManager

    @Inject
    lateinit var pusherManager: IPusherManager

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getStringExtra(SERVICE_ACTION) ?: "") {
            START_SERVICE -> {
                startDeviceScan()
                startForeground()
                startPusher()
                return Service.START_STICKY
            }
            STOP_SERVICE -> {
                stopDeviceScan()
                stopPusher()
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startDeviceScan() {
        bluetoothLEManager.startScan(this)
    }

    private fun stopDeviceScan() {
        bluetoothLEManager.stopScan()
    }

    private fun startPusher() {
        CoroutineScope(Dispatchers.IO).launch {
            pusherManager.connect()
            pusherManager.subscribe(
                getString(R.string.pusherChannel),
                getString(R.string.pusherEvent)
            ) { event ->
                Log.i(TAG, "Received event with data: $event")
            }
        }
    }

    private fun stopPusher() {
        pusherManager.unsubscribe(getString(R.string.pusherChannel))
        pusherManager.disconnect()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopDeviceScan()
        stopPusher()
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun startForeground() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationId = 101
        val channelId = "BlueButtNotificationChannel"
        val channelName = getString(R.string.blue_butt_scanner_channel_name)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.setShowBadge(false)
            notificationChannel.description =
                getString(R.string.blue_butt_scanner_channel_description)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.blue_butt_scanner_notification_content_title))
            .setContentText(getString(R.string.blue_butt_scanner_notification_content_text))
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
        startForeground(notificationId, notification)
    }

    override fun onScanResult(bluetoothDeviceList: HashMap<String, BleDevice>) {
        val intent = Intent(SCANNED_DEVICES)
        intent.putExtra(BLE_DEVICES, bluetoothDeviceList)
        localBroadcastManager.sendBroadcast(intent)
    }

    override fun onTriggerAction(bleDevice: BleDevice) {
        when (bleDevice) {
            is BlueButtDevice -> runBlueButtTriggerAction(bleDevice)
        }
    }

    private fun runBlueButtTriggerAction(blueButtDevice: BlueButtDevice) {
        CoroutineScope(Dispatchers.IO).launch {
            blueButtDevicesManager.updateClickCount(blueButtDevice.macAddress)

            bluetoothLEManager.bleDeviceHashMap[blueButtDevice.macAddress]?.run {
                if (this !is BlueButtDevice) return@run

                clickCount++
                bluetoothLEManager.refreshDeviceList()
            }

            bleDomDevicesManager.isLEDStatusOn = !bleDomDevicesManager.isLEDStatusOn

            bluetoothLEManager.bleDeviceHashMap
                .filter { (_, device) -> device.isConnected && device is BLEDOMDevice }
                .forEach { (macAddress, device) ->
                    if (device is BLEDOMDevice) {
                        val newStatus =
                            bleDomDevicesManager.updateDeviceLEDStatus(
                                macAddress = macAddress,
                                status = bleDomDevicesManager.isLEDStatusOn
                            )

                        bluetoothLEManager.writeToDevice(
                            macAddress,
                            BLEDOMCommander.setPower(newStatus)
                        )
                    }
                }
        }
    }
}
