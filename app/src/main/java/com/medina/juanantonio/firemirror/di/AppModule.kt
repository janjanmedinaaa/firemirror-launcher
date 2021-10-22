package com.medina.juanantonio.firemirror.di

import android.content.Context
import com.medina.juanantonio.firemirror.data.managers.*
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
    fun provideHolidayManager(): IHolidayManager = HolidayManager()

    @Provides
    @Singleton
    fun provideFocusManager(): IFocusManager = FocusManager()
}