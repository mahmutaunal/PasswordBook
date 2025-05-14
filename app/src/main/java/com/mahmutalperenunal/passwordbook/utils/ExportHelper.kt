package com.mahmutalperenunal.passwordbook.utils

import android.content.Context
import android.os.Environment
import com.google.gson.Gson
import com.mahmutalperenunal.passwordbook.data.PasswordEntity
import java.io.File
import java.io.FileOutputStream

object ExportHelper {

    fun exportEncryptedJson(context: Context, passwordList: List<PasswordEntity>): File? {
        val gson = Gson()
        val json = gson.toJson(passwordList)

        val encrypted = CryptoUtils.encrypt(json)

        val fileName = "exported_passwords_${System.currentTimeMillis()}.txt"
        val exportDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (exportDir != null && !exportDir.exists()) {
            exportDir.mkdirs()
        }

        return try {
            val file = File(exportDir, fileName)
            FileOutputStream(file).use { it.write(encrypted.toByteArray(Charsets.UTF_8)) }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}