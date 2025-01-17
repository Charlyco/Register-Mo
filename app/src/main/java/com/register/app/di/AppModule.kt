package com.register.app.di

import android.content.Context
import androidx.room.Room
import com.register.app.db.ChatContactDao
import com.register.app.db.NotificationDao
import com.register.app.db.RegisterDb
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
        return StompWebSocketClientImpl.getInstance("ws://register.megamentality.net:8084/ws")
    }

    @Singleton
    @Provides
    fun provideNotificationDao(@ApplicationContext context: Context): NotificationDao {
        val db = Room.databaseBuilder(
            context, RegisterDb::class.java, "register_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        return db.notificationDao()
    }

    @Singleton
    @Provides
    fun providesMemberDao(@ApplicationContext context: Context): ChatContactDao {
        val db = Room.databaseBuilder(
            context, RegisterDb::class.java, "register_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        return db.chatContactDao()
    }
}