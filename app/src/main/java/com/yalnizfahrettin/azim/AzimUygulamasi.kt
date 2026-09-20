package com.yalnizfahrettin.azim

import android.app.Application
import com.yalnizfahrettin.azim.notif.Bildirimler

class AzimUygulamasi : Application() {
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if(level >= TRIM_MEMORY_UI_HIDDEN) com.yalnizfahrettin.azim.ui.ThemeImages.trim()
    }
    override fun onCreate() {
        super.onCreate()
        Bildirimler.kanalKur(this)
    }
}
