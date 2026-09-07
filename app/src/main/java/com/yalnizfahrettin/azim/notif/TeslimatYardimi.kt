package com.yalnizfahrettin.azim.notif

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/*
 * BİLDİRİM GÖRÜNÜRLÜĞÜ YARDIMI
 *
 * Pil optimizasyonu ve otomatik başlatma yönlendirmeleri KALDIRILDI —
 * ayarlarda sürekli duran, çoğu kullanıcı için anlamsız bölümlerdi.
 *
 * Geriye tek ama en değerli madde kaldı: Samsung One UI'ın "açılır pencere
 * stili" ayarı. Varsayılan "Kısa açılır pencere" metni erken kırpıyor;
 * "Ayrıntılı açılır pencere" belirgin şekilde daha fazla metin gösteriyor.
 * Uzun cümlelerde yaşadığımız kırpılmanın büyük kısmı buradan geliyor.
 *
 * Bu ayarı programatik DEĞİŞTİREMEYİZ — Android böyle bir API vermiyor.
 * Yapabileceğimiz en iyi şey doğru sayfaya götürüp ne yapılacağını göstermek.
 */
object TeslimatYardimi {

    /** Bu ayar yalnız Samsung One UI'da var; diğerlerinde göstermek kafa karıştırır. */
    fun acilirPencereAyariVarMi(): Boolean =
        android.os.Build.MANUFACTURER.equals("samsung", ignoreCase = true)

    /** Uygulamanın sistem bildirim ayarları — açılır pencere stili orada. */
    fun bildirimAyarlariniAc(ctx: Context): Boolean {
        val niyet = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, ctx.packageName)
        return baslat(ctx, niyet) || uygulamaAyarlariniAc(ctx)
    }

    fun kanalAyarlariniAc(ctx: Context): Boolean = baslat(ctx,
        Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, ctx.packageName)
            .putExtra(Settings.EXTRA_CHANNEL_ID, Bildirimler.KANAL)) || bildirimAyarlariniAc(ctx)

    fun genelBildirimAyarlariniAc(ctx: Context): Boolean =
        baslat(ctx, Intent("android.settings.NOTIFICATION_SETTINGS")) || bildirimAyarlariniAc(ctx)

    private fun uygulamaAyarlariniAc(ctx: Context): Boolean = baslat(
        ctx,
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.fromParts("package", ctx.packageName, null)),
    )

    /** Ekran yoksa çökmek yerine false döner — cihaz çeşitliliği normaldir. */
    private fun baslat(ctx: Context, niyet: Intent): Boolean = runCatching {
        niyet.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (niyet.resolveActivity(ctx.packageManager) == null) return false
        ctx.startActivity(niyet)
        true
    }.getOrDefault(false)
}
