package com.yalnizfahrettin.azim

import android.app.Application
import com.yalnizfahrettin.azim.notif.Bildirimler

class AzimUygulamasi : Application() {
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if(level >= android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) com.yalnizfahrettin.azim.ui.ThemeImages.trim()
    }
    override fun onCreate() {
        super.onCreate()
        Bildirimler.kanalKur(this)
    }
}
