package com.yalnizfahrettin.azim.notif

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.yalnizfahrettin.azim.data.Depo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Bildirimdeki "Favorilere ekle" düğmesi. */
class FavoriAlicisi : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val metin = intent.getStringExtra(EXTRA) ?: return
        val sonuc = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Depo(context.applicationContext).favoriDegistir(metin)
                NotificationManagerCompat.from(context).cancel(metin.hashCode())
            } finally {
                sonuc.finish()
            }
        }
    }

    companion object {
        private const val EXTRA = "favori_metin"
        private const val ACTION = "com.yalnizfahrettin.azim.FAVORI"

        fun pendingIntent(ctx: Context, metin: String): PendingIntent {
            val i = Intent(ctx, FavoriAlicisi::class.java).apply {
                action = ACTION
                putExtra(EXTRA, metin)
            }
            return PendingIntent.getBroadcast(
                ctx, metin.hashCode(), i,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }
    }
}
