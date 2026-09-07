package com.yalnizfahrettin.azim.notif

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Cihaz yeniden başlayınca / saat değişince planı tazele. */
class AcilisAlicisi : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            -> Planlayici.yenidenKur(context)
        }
    }
}
