package com.register.app.util

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import com.register.app.dto.GroupStateItem
import com.register.app.model.CountryCode
import com.register.app.model.Group

object GroupStateSaver : Saver<GroupStateItem, String> {
    override fun SaverScope.save(value: GroupStateItem): String {
        return "${value.groupId}:${value.groupName}"
    }

    override fun restore(value: String): GroupStateItem {
        val (groupId, groupName) = value.split(":")
        return GroupStateItem(groupId.toInt(), groupName)
    }
}