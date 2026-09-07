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

/*
 * BİLDİRİM OKUNABİLİRLİĞİ — tasarım kararı
 *
 * Problem: gelen sözün tamamı okunamıyor.
 *
 * Araştırma bulguları:
 *  - Toplu (collapsed) bildirimde gövde tek satıra kırpılır. Stok Android'de
 *    ~90 karakter, Samsung One UI / MIUI gibi kabuklarda ~45-50 karakter.
 *  - NotificationCompat.BigTextStyle genişletilmiş alanda 5120 karaktere
 *    kadar çok satırlı metin gösterir.
 *  - Bildirimi programatik olarak "zorla açık" göstermenin API'si YOK.
 *    setCustomContentView ile özel görünüm denenebilir ama Android 12+
 *    bunları yeniden dekore eder ve toplu görünüm yüksekliği yine sabittir.
 *
 * Sonuç: tek başına "bildirimi büyütmek" güvenilir değil. İki önlem birlikte:
 *
 *  1) YAPI  — her bildirim BigTextStyle ile kurulur. Başlık kısa (kategori adı),
 *     gövde sözün kendisi. Genişletince söz + yazar tam görünür.
 *  2) İÇERİK — söz havuzuna 120 karakterlik tavan konur (Sozler.BILDIRIM_SINIRI)
 *     ve bir birim testi bunu zorlar. Böylece genişletilmiş görünümde asla
 *     kırpılma olmaz, çoğu cihazda toplu görünümde bile söz tam okunur.
 *
 * Ek: setVisibility(PUBLIC) kilit ekranında da metnin görünmesini sağlar —
 * bildirimlerin çoğu kilit ekranında okunduğu için bu kritik.
 */
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
            // DEFAULT: bildirim gölgeliğe düşer, ekranı basmaz.
            // Saatte bir motivasyon sözü için HIGH (heads-up) rahatsız edicidir.
            NotificationManager.IMPORTANCE_DEFAULT,
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

        // Kısaltılan sözlerde gövde de kısaltılmış hali — toplu ve genişletilmiş
        // görünüm arasında tutarsızlık olmasın, ikisi de aynı metni göstersin.
        val govde = soz.bildirimMetni(dil)
        val kisaltildi = soz.kisaltilirMi(dil)

        val genisMetin = buildString {
            append(govde)
            if (soz.yazar.isNotBlank()) {
                append("\n\n— ")
                append(soz.yazar)
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
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // kilit ekranında da tam metin
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .addAction(
                0,
                ctx.getString(R.string.favoriye_ekle),
                FavoriAlicisi.pendingIntent(ctx, soz.kimlik),
            )
            .build()

        return runCatching {
            NotificationManagerCompat.from(ctx).notify(soz.kimlik.hashCode(), bildirim)
            true
        }.getOrDefault(false)
    }
}
