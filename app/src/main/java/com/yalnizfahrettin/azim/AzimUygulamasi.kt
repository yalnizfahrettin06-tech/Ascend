package com.yalnizfahrettin.azim

import android.app.Application
import com.yalnizfahrettin.azim.notif.Bildirimler

class AzimUygulamasi : Application() {
    override fun onCreate() {
        super.onCreate()
        Bildirimler.kanalKur(this)
    }
}
