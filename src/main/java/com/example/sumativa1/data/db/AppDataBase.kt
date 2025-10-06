package com.example.sumativa1.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sumativa1.data.model.Message
import com.example.sumativa1.data.model.User

@Database(
    entities = [User::class, Message::class],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile private var INSTANCE: AppDataBase? = null

        fun get(context: Context): AppDataBase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "app_accessibilidad.db"
                ).build().also { INSTANCE = it }
            }
    }
}

