package com.daira.circle.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invite_codes")
data class InviteCodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val code: String,
    val createdAtMillis: Long,
    val isActive: Boolean
)
