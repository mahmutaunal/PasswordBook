package com.mahmutalperenunal.passwordbook.utils

import android.content.Context
import com.scottyab.rootbeer.RootBeer

object RootCheckUtil {
    fun isDeviceRooted(context: Context): Boolean {
        val rootBeer = RootBeer(context)
        return rootBeer.isRooted
    }
}