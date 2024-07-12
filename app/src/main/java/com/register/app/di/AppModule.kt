package com.register.app.di

import android.content.Context
import com.register.app.util.DataStoreManager
import com.register.app.websocket.StompWebSocketClient
import com.register.app.websocket.StompWebSocketClientImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager {
        return DataStoreManager.getInstance(context)
    }

    @Singleton
    @Provides
    fun providesStompClient(): StompWebSocketClient {
        return StompWebSocketClientImpl.getInstance("wss://ideateller-user-94b44f536cc1.herokuapp.com/chat") //URL to be changed
    }
}