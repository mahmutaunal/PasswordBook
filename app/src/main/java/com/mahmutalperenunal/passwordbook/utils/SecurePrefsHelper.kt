package com.mahmutalperenunal.passwordbook.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

object SecurePrefsHelper {
    private const val PREF_NAME = "secure_prefs"
    private const val KEY_PASSWORD = "master_password"

    private fun prefs(context: Context) =
        EncryptedSharedPreferences.create(
            context,
            PREF_NAME,
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    fun isPasswordSet(context: Context): Boolean {
        return prefs(context).contains(KEY_PASSWORD)
    }

    fun savePassword(context: Context, password: String) {
        prefs(context).edit { putString(KEY_PASSWORD, password) }
    }

    fun checkPassword(context: Context, input: String): Boolean {
        val stored = prefs(context).getString(KEY_PASSWORD, null)
        return stored == input
    }

    fun clearPassword(context: Context) {
        prefs(context).edit { remove(KEY_PASSWORD) }
    }
}
