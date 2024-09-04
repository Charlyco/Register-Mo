package com.register.app.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.register.app.model.DirectChatContact
import com.register.app.model.Member

@Dao
interface ChatContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMemberChat(member: DirectChatContact)

    @Query("SELECT * FROM chat_contacts")
    suspend fun fetchAllMembers(): MutableList<DirectChatContact>?
}