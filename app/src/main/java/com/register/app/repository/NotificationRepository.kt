package com.register.app.repository

import com.register.app.model.NotificationModel

interface NotificationRepository {
    suspend fun saveNotification(notificationModel: NotificationModel): MutableList<NotificationModel>?
    suspend fun getAllNotifications(): MutableList<NotificationModel>?
}