package com.register.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val title: String?,
    val content: String?,
    val type: String?
)
