package com.mahmutalperenunal.passwordbook.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mahmutalperenunal.passwordbook.database.entities.EncryptedKey
import com.mahmutalperenunal.passwordbook.database.entities.Entry
import com.mahmutalperenunal.passwordbook.database.entities.EntryDetail
import com.mahmutalperenunal.passwordbook.database.entities.Lock

@Database(
    entities = [Entry::class, EntryDetail::class, EncryptedKey::class, Lock::class],
    version = 1
)
abstract class PasswordManagerDatabase : RoomDatabase() {
    abstract fun getPasswordManagerDao(): PasswordManagerDao

    companion object {
        @Volatile
        private var instance: PasswordManagerDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                PasswordManagerDatabase::class.java,
                "password_manager_database.db"
            ).build()
    }
}