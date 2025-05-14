package com.mahmutalperenunal.passwordbook.utils

import android.content.Context
import com.mahmutalperenunal.passwordbook.R

data class PasswordCriteria(
    val description: String,
    val isValid: Boolean
)

object PasswordValidator {

    fun validate(context: Context, password: String): List<PasswordCriteria> {
        return listOf(
            PasswordCriteria(context.getString(R.string.criteria_min_length), password.length >= 8),
            PasswordCriteria(context.getString(R.string.criteria_lowercase), password.any { it.isLowerCase() }),
            PasswordCriteria(context.getString(R.string.criteria_uppercase), password.any { it.isUpperCase() }),
            PasswordCriteria(context.getString(R.string.criteria_digit), password.any { it.isDigit() }),
            PasswordCriteria(context.getString(R.string.criteria_letter), password.any { it.isLetter() }),
            PasswordCriteria(context.getString(R.string.criteria_special_char), password.any { !it.isLetterOrDigit() })
        )
    }
}