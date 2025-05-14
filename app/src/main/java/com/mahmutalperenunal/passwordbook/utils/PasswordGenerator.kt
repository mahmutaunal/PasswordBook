package com.mahmutalperenunal.passwordbook.utils

import kotlin.random.Random

object PasswordGenerator {

    private const val LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
    private const val UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val DIGITS = "0123456789"
    private const val SPECIALS = "!@#\$%^&*()-_=+[]{}<>?/"

    fun generate(
        length: Int = 12,
        useLowercase: Boolean = true,
        useUppercase: Boolean = true,
        useDigits: Boolean = true,
        useSpecials: Boolean = true
    ): String {
        if (!(useLowercase || useUppercase || useDigits || useSpecials)) {
            return ""
        }

        val charPool = buildString {
            if (useLowercase) append(LOWERCASE)
            if (useUppercase) append(UPPERCASE)
            if (useDigits) append(DIGITS)
            if (useSpecials) append(SPECIALS)
        }

        return (1..length)
            .map { Random.nextInt(0, charPool.length) }
            .map(charPool::get)
            .joinToString("")
    }
}