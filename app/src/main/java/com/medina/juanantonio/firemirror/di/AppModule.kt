package com.medina.juanantonio.firemirror.di

import android.content.Context
import androidx.room.Room
import com.medina.juanantonio.firemirror.ble.BluetoothLEManager
import com.medina.juanantonio.firemirror.ble.BluetoothLEServiceManager
import com.medina.juanantonio.firemirror.ble.IBluetoothLEManager
import com.medina.juanantonio.firemirror.data.database.FireMirrorDb
import com.medina.juanantonio.firemirror.data.managers.*
import com.medina.juanantonio.firemirror.features.server.FireMirrorServer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideAppManager(
        @ApplicationContext context: Context
    ): IAppManager {
        return AppManager(context)
    }

    @Provides
    @Singleton
    fun provideDataStoreManager(
        @ApplicationContext context: Context
    ): IDataStoreManager {
        return DataStoreManager(context)
    }

    @Provides
    @Singleton
    fun provideOpenWeatherManager(
        @ApplicationContext context: Context
    ): IOpenWeatherManager {
        return OpenWeatherManager(context)
    }

    @Provides
    @Singleton
    fun provideSpotifyManager(
        @ApplicationContext context: Context
    ): ISpotifyManager {
        return SpotifyManager(context)
    }

    @Provides
    @Singleton
    fun provideFireMirrorDb(@ApplicationContext context: Context): FireMirrorDb {
        return Room.databaseBuilder(context, FireMirrorDb::class.java, "firemirror.db")
            .fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideBlueButtDevicesManager(
        fireMirrorDb: FireMirrorDb
    ): IBlueButtDevicesManager {
        return BlueButtDevicesManager(fireMirrorDb)
    }

    @Provides
    @Singleton
    fun provideBLEDOMDevicesManager(
        fireMirrorDb: FireMirrorDb
    ): IBLEDOMDevicesManager {
        return BLEDOMDevicesManager(fireMirrorDb)
    }

    @Provides
    @Singleton
    fun provideBluetoothLeManager(
        @ApplicationContext context: Context,
        blueButtDevicesManager: IBlueButtDevicesManager,
        bleDOMDevicesManager: IBLEDOMDevicesManager
    ): IBluetoothLEManager {
        return BluetoothLEManager(context, blueButtDevicesManager, bleDOMDevicesManager)
    }

    @Provides
    @Singleton
    fun provideServiceManager(
        @ApplicationContext context: Context,
        bluetoothLEManager: IBluetoothLEManager
    ): BluetoothLEServiceManager {
        return BluetoothLEServiceManager(context, bluetoothLEManager)
    }

    @Provides
    @Singleton
    fun provideFireMirrorServer(
        blueButtDevicesManager: IBlueButtDevicesManager,
        bleDOMDevicesManager: IBLEDOMDevicesManager,
        bluetoothLEManager: IBluetoothLEManager
    ): FireMirrorServer {
        return FireMirrorServer(
            blueButtDevicesManager,
            bleDOMDevicesManager,
            bluetoothLEManager,
            8080
        )
    }
}