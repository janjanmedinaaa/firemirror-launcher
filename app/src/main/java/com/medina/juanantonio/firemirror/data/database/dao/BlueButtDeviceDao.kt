package com.medina.juanantonio.firemirror.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.medina.juanantonio.firemirror.data.models.BlueButtDevice
import com.medina.juanantonio.firemirror.data.models.TriggerRequest

@Dao
interface BlueButtDeviceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(blueButtDevice: BlueButtDevice)

    @Query("SELECT * FROM blueButtDevice")
    suspend fun getAllDevices(): List<BlueButtDevice>

    @Query("SELECT * FROM blueButtDevice WHERE macAddress = :macAddress LIMIT 1")
    suspend fun getDevice(macAddress: String): BlueButtDevice?

    @Query(
        "UPDATE blueButtDevice SET alias = :alias, triggerRequestOff = " +
            ":triggerRequestOff, triggerRequestOn = :triggerRequestOn " +
            "WHERE macAddress = :macAddress"
    )
    suspend fun updateDeviceDetails(
        macAddress: String,
        alias: String,
        triggerRequestOff: TriggerRequest?,
        triggerRequestOn: TriggerRequest?
    )

    @Query("UPDATE blueButtDevice SET clickCount = clickCount + 1 WHERE macAddress = :macAddress")
    suspend fun updateClickCount(macAddress: String)

    @Query("UPDATE blueButtDevice SET switchMode = :switchModeStatus WHERE macAddress = :macAddress")
    suspend fun activateSwitchMode(macAddress: String, switchModeStatus: Boolean?)

    @Query("UPDATE blueButtDevice SET switchMode = NOT switchMode WHERE macAddress = :macAddress")
    suspend fun updateSwitchModeStatus(macAddress: String)

    @Query("UPDATE blueButtDevice SET isPreviouslyConnected = :isPreviouslyConnected WHERE macAddress = :macAddress")
    suspend fun updateLastConnectionStatus(macAddress: String, isPreviouslyConnected: Boolean)

    @Query("DELETE FROM blueButtDevice WHERE macAddress = :macAddress")
    suspend fun deleteDevice(macAddress: String)
}