package com.example.sumativa1.data.db

import androidx.room.*
import com.example.sumativa1.data.model.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Insert
    suspend fun insert(m: Message): Long

    @Query("SELECT * FROM messages WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getAllByUser(userId: Int): List<Message>

    @Query("SELECT * FROM messages WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeAllByUser(userId: Int): Flow<List<Message>>

    @Delete
    suspend fun delete(m: Message)
}
