package com.example.sumativa1.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val text: String,
    val lat: Double? = null,
    val lng: Double? = null,
    val createdAt: Long = System.currentTimeMillis()
)