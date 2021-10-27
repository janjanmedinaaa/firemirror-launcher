package com.medina.juanantonio.firemirror.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.medina.juanantonio.firemirror.BuildConfig
import com.medina.juanantonio.firemirror.data.database.dao.BlueButtDeviceDao
import com.medina.juanantonio.firemirror.data.database.dao.FireMirrorTypeConverters
import com.medina.juanantonio.firemirror.data.models.BlueButtDevice

@Database(
    entities = [
        BlueButtDevice::class
    ],
    version = BuildConfig.VERSION_CODE,
    exportSchema = false
)
@TypeConverters(FireMirrorTypeConverters::class)
abstract class FireMirrorDb : RoomDatabase() {
    abstract fun blueButtDeviceDao(): BlueButtDeviceDao
}