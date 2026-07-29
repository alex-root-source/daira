package com.daira.circle.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InviteCodeDao {
    @Query("SELECT * FROM invite_codes WHERE isActive = 1 LIMIT 1")
    fun observeActiveCode(): Flow<InviteCodeEntity?>

    @Insert
    suspend fun insert(code: InviteCodeEntity)

    // يُبطِل كل الرموز الفعّالة السابقة — يُستدعى دومًا قبل إدراج رمز جديد
    @Query("UPDATE invite_codes SET isActive = 0 WHERE isActive = 1")
    suspend fun deactivateAll()
}
