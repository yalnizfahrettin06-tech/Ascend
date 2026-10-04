package com.yalnizfahrettin.azim

import android.app.Application
import android.content.ComponentCallbacks2

class AzimUygulamasi : Application() {
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) com.yalnizfahrettin.azim.ui.ThemeImages.trim()
    }
}
