package com.medina.juanantonio.firemirror.di

import android.content.Context
import androidx.room.Room
import com.medina.juanantonio.firemirror.ble.BluetoothLEManager
import com.medina.juanantonio.firemirror.ble.BluetoothLEServiceManager
import com.medina.juanantonio.firemirror.ble.IBluetoothLEManager
import com.medina.juanantonio.firemirror.data.database.FireMirrorDb
import com.medina.juanantonio.firemirror.data.managers.AppManager
import com.medina.juanantonio.firemirror.data.managers.BLEDOMDevicesManager
import com.medina.juanantonio.firemirror.data.managers.BlueButtDevicesManager
import com.medina.juanantonio.firemirror.data.managers.DataStoreManager
import com.medina.juanantonio.firemirror.data.managers.IAppManager
import com.medina.juanantonio.firemirror.data.managers.IBLEDOMDevicesManager
import com.medina.juanantonio.firemirror.data.managers.IBlueButtDevicesManager
import com.medina.juanantonio.firemirror.data.managers.IDataStoreManager
import com.medina.juanantonio.firemirror.data.managers.IOpenWeatherManager
import com.medina.juanantonio.firemirror.data.managers.IPusherManager
import com.medina.juanantonio.firemirror.data.managers.ISpotifyManager
import com.medina.juanantonio.firemirror.data.managers.OpenWeatherManager
import com.medina.juanantonio.firemirror.data.managers.PusherManager
import com.medina.juanantonio.firemirror.data.managers.SpotifyManager
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

    @Provides
    @Singleton
    fun providePusherManager(
        @ApplicationContext context: Context
    ): IPusherManager {
        return PusherManager(context)
    }
}