package com.daira.circle.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Query("SELECT * FROM members ORDER BY joinedAtMillis ASC")
    fun observeAll(): Flow<List<MemberEntity>>

    @Insert
    suspend fun insert(member: MemberEntity)
}
