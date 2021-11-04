package com.medina.juanantonio.firemirror.data.managers

import com.medina.juanantonio.firemirror.data.database.FireMirrorDb
import com.medina.juanantonio.firemirror.data.models.BlueButtDevice
import com.medina.juanantonio.firemirror.data.models.TriggerRequest

class DatabaseManager(fireMirrorDb: FireMirrorDb) : IDatabaseManager {
    private val blueButtDeviceDao = fireMirrorDb.blueButtDeviceDao()

    override suspend fun addBlueButtDevice(blueButtDevice: BlueButtDevice) {
        blueButtDeviceDao.insert(blueButtDevice)
    }

    override suspend fun getAllDevices(): List<BlueButtDevice> {
        return blueButtDeviceDao.getAllDevices()
    }

    override suspend fun getDevice(id: Int): BlueButtDevice? {
        return blueButtDeviceDao.getDevice(id)
    }

    override suspend fun getDevice(macAddress: String): BlueButtDevice? {
        return blueButtDeviceDao.getDevice(macAddress)
    }

    override suspend fun updateDeviceDetails(
        id: Int,
        alias: String,
        triggerRequestOff: TriggerRequest?,
        triggerRequestOn: TriggerRequest?
    ) {
        blueButtDeviceDao.updateDeviceDetails(
            id,
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

interface IDatabaseManager {
    suspend fun addBlueButtDevice(blueButtDevice: BlueButtDevice)
    suspend fun getAllDevices(): List<BlueButtDevice>
    suspend fun getDevice(id: Int): BlueButtDevice?
    suspend fun getDevice(macAddress: String): BlueButtDevice?
    suspend fun updateDeviceDetails(
        id: Int,
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