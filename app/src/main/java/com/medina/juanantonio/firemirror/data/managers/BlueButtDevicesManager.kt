package com.medina.juanantonio.firemirror.data.managers

import com.medina.juanantonio.firemirror.data.database.FireMirrorDb
import com.medina.juanantonio.firemirror.data.models.BlueButtDevice
import com.medina.juanantonio.firemirror.data.models.TriggerRequest

class BlueButtDevicesManager(fireMirrorDb: FireMirrorDb) : IBlueButtDevicesManager {
    private val blueButtDeviceDao = fireMirrorDb.blueButtDeviceDao()

    override suspend fun addDevice(blueButtDevice: BlueButtDevice) {
        if (exists(blueButtDevice.macAddress)) return
        blueButtDeviceDao.insert(blueButtDevice)
    }

    override suspend fun getAllDevices(): List<BlueButtDevice> {
        return blueButtDeviceDao.getAllDevices()
    }

    override suspend fun getDevice(macAddress: String): BlueButtDevice? {
        return blueButtDeviceDao.getDevice(macAddress)
    }

    override suspend fun exists(macAddress: String): Boolean {
        val device = getDevice(macAddress)
        return device != null
    }

    override suspend fun updateDeviceDetails(
        macAddress: String,
        alias: String,
        triggerRequestOff: TriggerRequest?,
        triggerRequestOn: TriggerRequest?
    ) {
        blueButtDeviceDao.updateDeviceDetails(
            macAddress,
            alias,
            triggerRequestOff,
            triggerRequestOn
        )
    }

    override suspend fun updateClickCount(macAddress: String) {
        blueButtDeviceDao.updateClickCount(macAddress)
    }

    override suspend fun activateSwitchMode(macAddress: String, switchModeStatus: Boolean?) {
        blueButtDeviceDao.activateSwitchMode(macAddress, switchModeStatus)
    }

    override suspend fun updateSwitchModeStatus(macAddress: String) {
        blueButtDeviceDao.updateSwitchModeStatus(macAddress)
    }

    override suspend fun updateLastConnectionStatus(
        macAddress: String,
        isPreviouslyConnected: Boolean
    ) {
        blueButtDeviceDao.updateLastConnectionStatus(macAddress, isPreviouslyConnected)
    }

    override suspend fun deleteDevice(macAddress: String) {
        return blueButtDeviceDao.deleteDevice(macAddress)
    }
}

interface IBlueButtDevicesManager {
    suspend fun addDevice(blueButtDevice: BlueButtDevice)
    suspend fun getAllDevices(): List<BlueButtDevice>
    suspend fun getDevice(macAddress: String): BlueButtDevice?
    suspend fun exists(macAddress: String): Boolean
    suspend fun updateDeviceDetails(
        macAddress: String,
        alias: String,
        triggerRequestOff: TriggerRequest? = null,
        triggerRequestOn: TriggerRequest? = null
    )

    suspend fun updateClickCount(macAddress: String)
    suspend fun activateSwitchMode(macAddress: String, switchModeStatus: Boolean?)
    suspend fun updateSwitchModeStatus(macAddress: String)
    suspend fun updateLastConnectionStatus(macAddress: String, isPreviouslyConnected: Boolean)
    suspend fun deleteDevice(macAddress: String)
}