package com.register.app.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "chat_contacts",
    indices = [Index(value = ["contactName"], unique = true)])
data class DirectChatContact(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val groupId: Int?,
    val groupName: String?,
    val contactMembershipId: String?,
    val myMembershipId: String?,
    val contactName: String?,
    val imageUrl: String?,
    val sendTime: String?,
)
