package com.medina.juanantonio.firemirror.features.server

import android.util.Log
import com.medina.juanantonio.firemirror.ble.IBluetoothLEManager
import com.medina.juanantonio.firemirror.common.Constants
import com.medina.juanantonio.firemirror.data.managers.IBLEDOMDevicesManager
import com.medina.juanantonio.firemirror.data.managers.IBlueButtDevicesManager
import com.medina.juanantonio.firemirror.data.models.BleDevice
import com.medina.juanantonio.firemirror.data.models.BlueButtDevice
import com.medina.juanantonio.firemirror.data.models.TriggerRequest
import fi.iki.elonen.NanoHTTPD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.lang.NullPointerException

class FireMirrorServer(
    private val blueButtDevicesManager: IBlueButtDevicesManager,
    private val bleDomDevicesManager: IBLEDOMDevicesManager,
    private val bluetoothLEManager: IBluetoothLEManager,
    val port: Int
) : NanoHTTPD(port) {

    companion object {
        const val TAG = "FireMirrorServer"
    }

    override fun serve(session: IHTTPSession?): Response {
        return when (session?.method) {
            Method.GET -> {
                try {
                    runBlocking(Dispatchers.Main) {
                        val uriMacAddress = session.uri?.removePrefix("/") ?: ""
                        val updateDeviceList: Boolean
                        when (getBleDevice(uriMacAddress)) {
                            is BlueButtDevice -> {
                                updateDeviceList = session.parameters.let {
                                    it.containsKey("alias") &&
                                            it.containsKey("triggerurlon") &&
                                            it.containsKey("triggerurloff")
                                }

                                if (updateDeviceList) {
                                    blueButtDevicesManager.updateDeviceDetails(
                                        macAddress = uriMacAddress,
                                        alias = session.parameters["alias"]?.get(0) ?: "",
                                        triggerRequestOff = TriggerRequest.default(
                                            session.parameters["triggerurloff"]?.get(0) ?: ""
                                        ),
                                        triggerRequestOn = TriggerRequest.default(
                                            session.parameters["triggerurlon"]?.get(0) ?: ""
                                        )
                                    )
                                }
                            }
                            else -> {
                                updateDeviceList = session.parameters.containsKey("alias")

                                if (updateDeviceList) {
                                    bleDomDevicesManager.updateDeviceDetails(
                                        macAddress = uriMacAddress,
                                        alias = session.parameters["alias"]?.get(0) ?: ""
                                    )
                                }
                            }
                        }

                        val device = getBleDevice(uriMacAddress)
                        if (updateDeviceList) bluetoothLEManager.refreshDeviceList(device)

                        newFixedLengthResponse(
                            when (device) {
                                is BlueButtDevice -> {
                                    Constants.Server.getHtmlForm(
                                        deviceName = device.getDeviceName(),
                                        alias = device.alias,
                                        triggerUrlOff = device.triggerRequestOff.url,
                                        triggerUrlOn = device.triggerRequestOn.url
                                    )
                                }
                                else -> {
                                    Constants.Server.getHtmlForm(
                                        deviceName = device.getDeviceName(),
                                        alias = device.alias
                                    )
                                }
                            }
                        )
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "${e.message}")

                    newFixedLengthResponse(
                        "<html><body><h1>Invalid device ID provided from URL: " +
                                "${session.uri}</h1><br /><h2>${e.message}</h2></body></html>"
                    )
                }
            }
            else -> {
                newFixedLengthResponse(
                    "<html><body><h1>Fire Mirror is running.</h1></body></html>"
                )
            }
        }
    }

    override fun start() {
        try {
            super.start()
            Log.d(TAG, "FireMirrorServer started.")
        } catch (e: Exception) {
            Log.d(TAG, "${e.message}")
        }
    }

    @Throws(NullPointerException::class)
    private suspend fun getBleDevice(macAddress: String): BleDevice {
        return blueButtDevicesManager.getDevice(macAddress)
            ?: bleDomDevicesManager.getDevice(macAddress)
            ?: throw NullPointerException("$macAddress not found")
    }
}