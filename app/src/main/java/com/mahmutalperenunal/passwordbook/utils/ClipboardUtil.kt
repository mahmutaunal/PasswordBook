package com.mahmutalperenunal.passwordbook.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.mahmutalperenunal.passwordbook.R

object ClipboardUtil {

    fun copyText(context: Context, text: String, clearDelayMs: Long = 30_000) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("password", text)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(context, context.getString(R.string.password_copied), Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            val emptyClip = ClipData.newPlainText("", "")
            clipboard.setPrimaryClip(emptyClip)
            Toast.makeText(context, context.getString(R.string.clipboard_cleared), Toast.LENGTH_SHORT).show()
        }, clearDelayMs)
    }
}