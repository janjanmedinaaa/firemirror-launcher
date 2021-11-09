package com.medina.juanantonio.firemirror.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.medina.juanantonio.firemirror.data.models.BLEDOMDevice
import com.medina.juanantonio.firemirror.data.models.LEDData

@Dao
interface BLEDOMDeviceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bleDOMDevice: BLEDOMDevice)

    @Query("SELECT * FROM bleDOMDevice")
    suspend fun getAllDevices(): List<BLEDOMDevice>

    @Query("SELECT * FROM bleDOMDevice WHERE macAddress = :macAddress LIMIT 1")
    suspend fun getDevice(macAddress: String): BLEDOMDevice?

    @Query("UPDATE bleDOMDevice SET alias = :alias WHERE macAddress = :macAddress")
    suspend fun updateDeviceDetails(macAddress: String, alias: String)

    @Query("UPDATE bleDOMDevice SET ledData = :ledData WHERE macAddress = :macAddress")
    suspend fun updateDeviceLEDData(macAddress: String, ledData: LEDData)

    @Query("UPDATE bleDOMDevice SET isPreviouslyConnected = :isPreviouslyConnected WHERE macAddress = :macAddress")
    suspend fun updateLastConnectionStatus(macAddress: String, isPreviouslyConnected: Boolean)

    @Query("DELETE FROM bleDOMDevice WHERE macAddress = :macAddress")
    suspend fun deleteDevice(macAddress: String)
}