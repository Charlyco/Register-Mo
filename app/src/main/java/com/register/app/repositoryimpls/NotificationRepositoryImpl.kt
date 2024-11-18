package com.register.app.repositoryimpls

import com.register.app.db.NotificationDao
import com.register.app.model.NotificationModel
import com.register.app.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao
): NotificationRepository {
    override suspend fun saveNotification(notificationModel: NotificationModel): MutableList<NotificationModel>? {
        notificationDao.saveNotification(notificationModel)

        return notificationDao.getAllNotifications()
    }

    override suspend fun getAllNotifications(): MutableList<NotificationModel>? {
        return notificationDao.getAllNotifications()
    }

}