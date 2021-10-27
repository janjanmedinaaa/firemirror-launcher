package com.medina.juanantonio.firemirror.ble

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.medina.juanantonio.firemirror.R
import com.medina.juanantonio.firemirror.data.managers.IDatabaseManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.medina.juanantonio.firemirror.data.models.BlueButtDevice
import com.medina.juanantonio.firemirror.features.MainActivity
import java.util.*
import kotlin.collections.HashMap

@AndroidEntryPoint
class BluetoothLEService : LifecycleService(), BluetoothLeScanCallBack {

    companion object {
        const val SERVICE_ACTION = "SERVICE_ACTION"
        const val START_SERVICE = "START_SERVICE"
        const val STOP_SERVICE = "STOP_SERVICE"
        const val SCANNED_DEVICES = "SCANNED_DEVICES"
        const val BLUE_BUTT_DEVICES = "BLUE_BUTT_DEVICES"
    }

    private val localBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(this)
    }

    @Inject
    lateinit var bluetoothLEManager: IBluetoothLEManager

    @Inject
    lateinit var databaseManager: IDatabaseManager

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getStringExtra(SERVICE_ACTION) ?: "") {
            START_SERVICE -> {
                startDeviceScan()
                startForeground()
                return Service.START_STICKY
            }
            STOP_SERVICE -> {
                stopDeviceScan()
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

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopDeviceScan()
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

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun showActionNotification(blueButtDevice: BlueButtDevice) {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = "BlueButtNotificationChannel"
        val channelName = getString(R.string.blue_butt_trigger_channel_name)
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
                getString(R.string.blue_butt_trigger_channel_description)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.blue_butt_trigger_notification_content_title))
            .setContentText(
                getString(
                    R.string.blue_butt_trigger_notification_content_text,
                    blueButtDevice.getDeviceName()
                )
            )
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(Date().time.toInt(), notification)
    }

    override fun onScanResult(bluetoothDeviceList: HashMap<String, BlueButtDevice>) {
        val intent = Intent(SCANNED_DEVICES)
        intent.putExtra(BLUE_BUTT_DEVICES, bluetoothDeviceList)
        localBroadcastManager.sendBroadcast(intent)
    }

    override fun onTriggerAction(blueButtDevice: BlueButtDevice) {
        CoroutineScope(Dispatchers.IO).launch {
            showActionNotification(blueButtDevice)
            databaseManager.updateClickCount(blueButtDevice.macAddress)
            blueButtDevice.runRequest()

            bluetoothLEManager.blueButtDeviceHashMap[blueButtDevice.macAddress]?.run {
                clickCount++
                switchMode?.let { status ->
                    switchMode = !status
                    databaseManager.updateSwitchModeStatus(macAddress)
                }
                bluetoothLEManager.refreshDeviceList()
            }
        }
    }
}
