package com.yalnizfahrettin.azim.notif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.yalnizfahrettin.azim.MainActivity
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.data.Kategoriler
import com.yalnizfahrettin.azim.data.Soz


object Bildirimler {

    const val KANAL = "azim_sozler"
    const val EXTRA_KIMLIK = "extra_kimlik"


    fun kanalKur(ctx: Context) {
        val mgr = ctx.getSystemService(NotificationManager::class.java) ?: return
        val mevcut = mgr.getNotificationChannel(KANAL)
        if (mevcut != null) {
            mevcut.name = ctx.getString(R.string.kanal_ad)
            mevcut.description = ctx.getString(R.string.kanal_aciklama)
            mgr.createNotificationChannel(mevcut)
            return
        }
        val kanal = NotificationChannel(
            KANAL,
            ctx.getString(R.string.kanal_ad),
            // Only new channels opt into pop-up alerts; existing user choices are preserved.
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = ctx.getString(R.string.kanal_aciklama)
            enableVibration(true)
            setShowBadge(true)
        }
        mgr.createNotificationChannel(kanal)
    }

    fun izinVarMi(ctx: Context): Boolean {
        val mgr = ctx.getSystemService(NotificationManager::class.java)
        return NotificationManagerCompat.from(ctx).areNotificationsEnabled() &&
            mgr?.getNotificationChannel(KANAL)?.importance != NotificationManager.IMPORTANCE_NONE
    }

    fun goster(ctx: Context, soz: Soz, dil: String = "tr"): Boolean {
        if (!izinVarMi(ctx)) return false
        kanalKur(ctx)

        val kategoriAdi = Kategoriler.bul(soz.kategori)?.ad(dil)
            ?: ctx.getString(R.string.uygulama_adi)

        val acilis = Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_KIMLIK, soz.kimlik)
        }
        val pi = PendingIntent.getActivity(
            ctx, soz.kimlik.hashCode(), acilis,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        // The expanded notification must contain the complete quote.
        val govde = soz.metin(dil)

        val genisMetin = buildString {
            append(govde)
            if (soz.yazar.isNotBlank()) {
                append("\n\n— ")
                append(soz.imza(dil))
            }
        }

        val bildirim = NotificationCompat.Builder(ctx, KANAL)
            .setSmallIcon(R.drawable.ic_bildirim)
            .setContentTitle(kategoriAdi)          // kısa — hiçbir cihazda kırpılmaz
            .setContentText(soz.metin(dil))             // toplu görünümdeki tek satır
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(genisMetin)           // genişletilmiş tam metin
                    .setBigContentTitle(kategoriAdi)
            )
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // kilit ekranında da tam metin
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .addAction(
                0,
                ctx.getString(R.string.favoriye_ekle),
                FavoriAlicisi.pendingIntent(ctx, soz.kimlik),
            )
            .build()

        return try {
            NotificationManagerCompat.from(ctx).notify(soz.kimlik.hashCode(), bildirim)
            true
        } catch (_: SecurityException) {
            false
        }
    }
}
