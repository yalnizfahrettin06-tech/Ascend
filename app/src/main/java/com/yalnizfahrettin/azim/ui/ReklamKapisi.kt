package com.yalnizfahrettin.azim.ui

import android.app.Activity

/*
 * REKLAM KAPISI
 *
 * Eski ReklamYoneticisi.goster() şunu yapıyordu:
 *     startActivity(Intent(ACTION_VIEW, Uri.parse("https://www.google.com")))
 *     onTamamlandi()
 * Yani tarayıcıda google.com açıp kategoriyi bedava açıyordu. Bu hem sahte
 * reklamdı (Play politika ihlali) hem de kilit mekanizmasını anlamsız
 * kılıyordu. v3.6.1'den v5.7.0'a kadar 228 derlemede düzeltilmemişti.
 *
 * Burada sahte davranış YOK. Arayüz gerçek bir ödüllü reklam SDK'sına
 * bağlanmayı bekleyen bir kapı; SDK yokken ödül vermez ve kullanıcıya
 * dürüst bir sonuç döner.
 *
 * AdMob bağlamak için:
 *   1) app/build.gradle.kts -> implementation("com.google.android.gms:play-services-ads:23.x.x")
 *   2) AndroidManifest -> <meta-data android:name="com.google.android.gms.ads.APPLICATION_ID" .../>
 *   3) AdMobKapisi sınıfını yaz, RewardedAd.load(...) + show(...) ile
 *      onOdul() yalnızca onUserEarnedReward geldiğinde çağrılsın.
 *   4) VarsayilanKapi yerine AdMobKapisi'ni geç.
 */
interface ReklamKapisi {
    val hazir: Boolean get() = false
    /**
     * @param onSonuc true ise ödül hak edildi (kategori açılır), false ise
     *        reklam gösterilemedi veya izlenmedi (kategori AÇILMAZ).
     */
    fun oduluGoster(activity: Activity, onSonuc: (Boolean) -> Unit)
}

/** SDK bağlanana kadar kullanılan dürüst yer tutucu: ödül vermez. */
class VarsayilanKapi : ReklamKapisi {
    override fun oduluGoster(activity: Activity, onSonuc: (Boolean) -> Unit) {
        onSonuc(false)
    }
}

/**
 * Geliştirme sırasında kilit akışını denemek için. Sürüm derlemesinde
 * KULLANILMAZ — sadece debug'da bağlanır.
 */
class TestKapisi : ReklamKapisi {
    override fun oduluGoster(activity: Activity, onSonuc: (Boolean) -> Unit) {
        onSonuc(true)
    }
}
