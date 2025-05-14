package com.mahmutalperenunal.passwordbook.data

import androidx.room.*

@Dao
interface PasswordDao {
    @Query("SELECT * FROM passwords")
    suspend fun getAll(): List<PasswordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(password: PasswordEntity)

    @Delete
    suspend fun delete(password: PasswordEntity)
}