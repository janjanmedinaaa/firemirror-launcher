package com.medina.juanantonio.firemirror.di

import android.content.Context
import com.medina.juanantonio.firemirror.data.managers.AppManager
import com.medina.juanantonio.firemirror.data.managers.DataStoreManager
import com.medina.juanantonio.firemirror.data.managers.IAppManager
import com.medina.juanantonio.firemirror.data.managers.IDataStoreManager
import com.medina.juanantonio.firemirror.data.managers.ISpotifyManager
import com.medina.juanantonio.firemirror.data.managers.SpotifyManager
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
    fun provideSpotifyManager(): ISpotifyManager {
        return SpotifyManager()
    }
}