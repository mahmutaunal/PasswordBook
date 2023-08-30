package com.mahmutalperenunal.passwordbook.security

import com.mahmutalperenunal.passwordbook.util.EncryptedObject
import com.tozny.crypto.android.AesCbcWithIntegrity.*


class EncryptionDecryption {

    init {
        System.loadLibrary("key-jni")
    }

    external fun getKey(): String

    fun encrypt(data: String, emdPasswordArg: String, eedKeyArg: String): EncryptedObject {
        val emdSalt = saltString(generateSalt())
        val emdKey = generateKeyFromPassword(emdPasswordArg, emdSalt)
        val emdEncryptedString = encrypt(data, emdKey).toString()
        val eedEncryptedString = encrypt(emdEncryptedString, keys(eedKeyArg)).toString()
        return EncryptedObject(keyString(emdKey), eedEncryptedString)
    }

    fun decrypt(encryptedData: String, emdKeyArg: String, eedKeyArg: String): String {
        val eedDecryptedString = decryptString(CipherTextIvMac(encryptedData), keys(eedKeyArg))
        return decryptString(CipherTextIvMac(eedDecryptedString), keys(emdKeyArg))
    }

}