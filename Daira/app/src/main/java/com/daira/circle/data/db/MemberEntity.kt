package com.daira.circle.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class MemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val initials: String,
    val sinceLabel: String,
    val joinedAtMillis: Long
)
