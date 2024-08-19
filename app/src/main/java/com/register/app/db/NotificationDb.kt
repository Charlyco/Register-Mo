package com.register.app.db

import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RoomDatabase
import com.register.app.model.NotificationModel

@Database(entities = [NotificationModel::class], version = 1)
abstract class NotificationDb: RoomDatabase() {
    abstract fun notificationDao(): NotificationDao

}