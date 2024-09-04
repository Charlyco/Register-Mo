package com.register.app.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.register.app.model.DirectChatContact
import com.register.app.model.NotificationModel

@Database(entities = [NotificationModel::class, DirectChatContact::class], version = 3)
abstract class RegisterDb: RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
    abstract fun chatContactDao(): ChatContactDao

}