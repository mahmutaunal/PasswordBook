package com.mahmutalperenunal.passwordbook.repository

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mahmutalperenunal.passwordbook.database.PasswordManagerDatabase
import com.mahmutalperenunal.passwordbook.database.entities.EncryptedKey
import com.mahmutalperenunal.passwordbook.database.entities.Entry
import com.mahmutalperenunal.passwordbook.database.entities.EntryDetail
import com.mahmutalperenunal.passwordbook.database.entities.Lock

class PasswordManagerRepository(private val db: PasswordManagerDatabase) {
    suspend fun upsertEntry(entry: Entry): Long = db.getPasswordManagerDao().upsertEntry(entry)

    suspend fun deleteEntry(entry: Entry) = db.getPasswordManagerDao().deleteEntry(entry)

    fun getAllEntries() = db.getPasswordManagerDao().getAllEntries()

    fun getAllFavouriteEntries() = db.getPasswordManagerDao().getAllFavouriteEntries()

    suspend fun setFavouriteEntry(isFavourite: Int, id: Int) =
        db.getPasswordManagerDao().setFavouriteEntry(isFavourite, id)

    fun searchEntries(text: String) = db.getPasswordManagerDao().searchEntries(text)

    fun sortEntries(category: String) = db.getPasswordManagerDao().sortEntries(category)

    suspend fun upsertEntryDetail(entryDetail: EntryDetail) =
        db.getPasswordManagerDao().upsertEntryDetail(entryDetail)

    suspend fun deleteEntryDetails(id: Int) = db.getPasswordManagerDao().deleteEntryDetails(id)

    fun getAllEntryDetails(id: Int) = db.getPasswordManagerDao().getAllEntryDetails(id)

    suspend fun upsertEncryptedKey(encryptedKey: EncryptedKey) =
        db.getPasswordManagerDao().upsertKey(encryptedKey)

    suspend fun deleteEncryptedKeys(id: Int) = db.getPasswordManagerDao().deleteKeys(id)

    fun getAllEncryptedKey(id: Int) = db.getPasswordManagerDao().getAllEncryptedKeys(id)

    suspend fun setLock(lock: Lock) = db.getPasswordManagerDao().setLock(lock)

    fun getLockPassword(): LiveData<List<Lock>> = db.getPasswordManagerDao().getLockPassword()

    fun getAllEntryDetailsOneTime(id: Int) =
        db.getPasswordManagerDao().getAllEntryDetailsOneTime(id)

    fun getAllEncryptedKeysOneTime(id: Int) =
        db.getPasswordManagerDao().getAllEncryptedKeysOneTime(id)

}