package com.mahmutalperenunal.passwordbook.utils

import android.content.Context
import android.content.res.Configuration
import java.util.*
import androidx.core.content.edit

object LocalizationHelper {

    private const val PREF_NAME = "language_prefs"
    private const val KEY_LANGUAGE = "app_language"

    fun setLocale(context: Context, languageCode: String): Context {
        val codeToUse = languageCode.ifBlank { Locale.getDefault().language }
        saveLanguage(context, languageCode)
        val locale = Locale(codeToUse)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }

    fun applySavedLocale(context: Context): Context {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val savedLang = prefs.getString(KEY_LANGUAGE, Locale.getDefault().language) ?: "en"
        return setLocale(context, savedLang)
    }

    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "") ?: ""
    }

    private fun saveLanguage(context: Context, code: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit { putString(KEY_LANGUAGE, code) }
    }
}