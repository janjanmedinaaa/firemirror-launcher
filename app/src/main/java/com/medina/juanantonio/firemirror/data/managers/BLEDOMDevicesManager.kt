package com.medina.juanantonio.firemirror.data.managers

import com.medina.juanantonio.firemirror.data.database.FireMirrorDb
import com.medina.juanantonio.firemirror.data.models.BLEDOMDevice

class BLEDOMDevicesManager(fireMirrorDb: FireMirrorDb) : IBLEDOMDevicesManager {
    private val bleDOMDeviceDao = fireMirrorDb.bleDOMDeviceDao()

    override suspend fun addDevice(bleDOMDevice: BLEDOMDevice) {
        if (exists(bleDOMDevice.macAddress)) return
        bleDOMDeviceDao.insert(bleDOMDevice)
    }

    override suspend fun getAllDevices(): List<BLEDOMDevice> {
        return bleDOMDeviceDao.getAllDevices()
    }

    override suspend fun getDevice(macAddress: String): BLEDOMDevice? {
        return bleDOMDeviceDao.getDevice(macAddress)
    }

    override suspend fun exists(macAddress: String): Boolean {
        val device = getDevice(macAddress)
        return device != null
    }

    override suspend fun updateLastConnectionStatus(
        macAddress: String,
        isPreviouslyConnected: Boolean
    ) {
        bleDOMDeviceDao.updateLastConnectionStatus(macAddress, isPreviouslyConnected)
    }

    override suspend fun deleteDevice(macAddress: String) {
        return bleDOMDeviceDao.deleteDevice(macAddress)
    }
}

interface IBLEDOMDevicesManager {
    suspend fun addDevice(bleDOMDevice: BLEDOMDevice)
    suspend fun getAllDevices(): List<BLEDOMDevice>
    suspend fun getDevice(macAddress: String): BLEDOMDevice?
    suspend fun exists(macAddress: String): Boolean
    suspend fun updateLastConnectionStatus(macAddress: String, isPreviouslyConnected: Boolean)
    suspend fun deleteDevice(macAddress: String)
}