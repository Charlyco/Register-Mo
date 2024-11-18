package com.register.app.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.register.app.model.NotificationModel

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNotification(notification: NotificationModel)

    @Query("SELECT * FROM notifications ORDER BY id DESC")
    suspend fun getAllNotifications(): MutableList<NotificationModel>?
}
