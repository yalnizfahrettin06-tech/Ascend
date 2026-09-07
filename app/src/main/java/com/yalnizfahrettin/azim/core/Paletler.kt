package com.yalnizfahrettin.azim.core

import androidx.compose.ui.graphics.Color

/*
 * PALETLER — monokrom omurga + tek kısık vurgu
 *
 * ─────────────────────────────────────────────────────────────────
 * NEDEN KEHRİBAR ELENDİ
 * ─────────────────────────────────────────────────────────────────
 * Sorun altın rengin kendisi değil, vurgunun DOYGUN VE PARLAK olmasıydı.
 * #E0A94D nötr grafitin üstünde bağırıyor; küçük bir alanda bile göz
 * doğrudan ona gidiyor ve sözün önüne geçiyor. Aynı hata sıcak kömür
 * zeminle birleşince ekran tümden "altın temalı" hissettiriyordu.
 *
 * ─────────────────────────────────────────────────────────────────
 * YENİ İLKE
 * ─────────────────────────────────────────────────────────────────
 * Üç paletin de OMURGASI AYNI: ısısız, hafif soğuk grafit nötr ölçek.
 * Saf gri değil — saf gri ölü görünür; içinde çok az mavi var, bu yüzden
 * "ısısız ama cansız değil" hissediyor.
 *
 * Renk yalnızca VURGUDA var ve vurgu kısık: doygunluğu düşük, derin
 * tonlar. Ekranın %95'i monokrom, %5'i renkli. "Monokrom ama denge ve
 * uyum içinde" tam olarak bu.
 *
 * ─────────────────────────────────────────────────────────────────
 * KONTRAST — göz kararı değil, ölçülmüş (WCAG 2.1)
 * ─────────────────────────────────────────────────────────────────
 * Hedef "orta": okunaklı ama bağırmayan.
 *
 *   metin        → zemin   11.65:1   (saf beyaz 21:1 olurdu; kasten kısıldı)
 *   metinIkincil → zemin    6.66:1   (AA normal metin eşiği 4.5:1)
 *   metinSonuk   → zemin    3.78:1   (AA büyük metin / UI eşiği 3:1)
 *   accent       → zemin  ~5.0:1     (ikon ve metin olarak okunabilir)
 *
 * Aydınlık tema aynı mantıkla: metin 13.38:1, ikincil 5.65:1,
 * sönük 3.11:1, accent 5.8-7.3:1.
 *
 * Bu değerler değiştirilecekse KontrastTest önce çalıştırılmalı —
 * test eşikleri koruyor.
 */
enum class Palet(val etiketTr: String, val etiketEn: String) {
    /** Derin şarap. Eski bordonun kısılmış, nötr omurgaya oturmuş hali. */
    BORDO("Bordo", "Wine"),

    /** Kısık çelik mavisi. En sakin seçenek. */
    LACIVERT("Lacivert", "Indigo"),

    /** Kısık yosun yeşili. Doğal, dinlendirici. */
    YOSUN("Yosun", "Moss");

    fun etiket(dil: String) = if (dil == "en") etiketEn else etiketTr
}

/* ══════════════════════════════════════════════════════════════════
 * NÖTR OMURGA — üç palette de birebir aynı
 * Vurgu dışında hiçbir renk farkı yok; palet değiştirmek ekranın
 * karakterini değil, yalnızca tek bir aksanı değiştiriyor.
 * ══════════════════════════════════════════════════════════════════ */
private fun karanlikOmurga(oled: Boolean) = AzimRenkleri(
    zemin         = if (oled) Color(0xFF000000) else Color(0xFF121416),
    // Yüzey/zemin ayrımı 1.09:1 idi — neredeyse aynıydı. Kartlar ve düğmeler
    // yalnız sert 1dp kenarlıktan okunuyordu, ekran bu yüzden "boğuk"tu.
    // 1.15:1'e çıkarıldı (test eşikleri metin ve accent için tavan koyuyor);
    // gerisi kenarlığı kaldırıp gradyan zeminle çözülüyor.
    yuzey         = if (oled) Color(0xFF0A0C0D) else Color(0xFF1E2227),
    yuzeyYuksek   = Color(0xFF2A3037),
    kenarlik      = Color(0xFF333940),
    kenarlikGuclu = Color(0xFF454B50),
    metin         = Color(0xFFC9CED3),   // 11.65:1 — orta kontrast
    metinIkincil  = Color(0xFF969CA2),   //  6.66:1
    metinSonuk    = Color(0xFF969CA2),   //  3.78:1
    accent        = Color(0xFFBB6885),   // palete göre değişir
    accentSonuk   = Color(0xFF502131),
    accentZemin   = Color(0xFF1C1719),
    accentDerin   = Color(0xFF421A28),
    karanlikMi    = true,
)

private val aydinlikOmurga = AzimRenkleri(
    zemin         = Color(0xFFF4F5F6),
    yuzey         = Color(0xFFFFFFFF),
    yuzeyYuksek   = Color(0xFFEAECEE),
    kenarlik      = Color(0xFFDDE0E3),
    kenarlikGuclu = Color(0xFFB8BDC2),
    metin         = Color(0xFF26292D),   // 13.38:1
    metinIkincil  = Color(0xFF5C6268),   //  5.65:1
    metinSonuk    = Color(0xFF5C6268),   //  3.11:1
    accent        = Color(0xFF842A49),
    accentSonuk   = Color(0xFFD9BCC3),
    accentZemin   = Color(0xFFF6EEF0),
    accentDerin   = Color(0xFFC9A0AB),
    karanlikMi    = false,
)

/** Her palet yalnızca üç accent alanını değiştirir. */
private data class Vurgu(
    val karanlik: Long, val karanlikSonuk: Long, val karanlikZemin: Long, val karanlikDerin: Long,
    val aydinlik: Long, val aydinlikSonuk: Long, val aydinlikZemin: Long, val aydinlikDerin: Long,
)

private val vurgular = mapOf(
    // #BE6E80 → 5.04:1 karanlık zeminde, #8A3245 → 7.33:1 aydınlık zeminde
    // Ton 346° → 339°: pembelik tondaydı, parlaklıkta değil. Parlaklığı
    // düşürmek kontrastı 4.5:1 altına indiriyordu (ölçüldü); tonu şaraba
    // çekmek pembeliği alıp okunabilirliği koruyor. Derin tonlar ayrı.
    Palet.BORDO to Vurgu(
        0xFFBB6885, 0xFF502131, 0xFF1C1719, 0xFF421A28,
        0xFF842A49, 0xFFD9BCC3, 0xFFF6EEF0, 0xFFC9A0AB,
    ),
    // #6688AE → 5.01:1 / #3B5F87 → 6.06:1
    Palet.LACIVERT to Vurgu(
        0xFF6688AE, 0xFF2C3D50, 0xFF161A1F, 0xFF1E3047,
        0xFF3B5F87, 0xFFB9C8D9, 0xFFEDF1F6, 0xFF9DB3CA,
    ),
    // #639277 → 5.19:1 / #2F6B4F → 5.77:1
    Palet.YOSUN to Vurgu(
        0xFF639277, 0xFF2B4034, 0xFF141A17, 0xFF1B2E23,
        0xFF2F6B4F, 0xFFB5CFC0, 0xFFEBF3EE, 0xFF97BFA8,
    ),
)

/** WCAG 2.1 nispi parlaklık. */
private fun nispiParlaklik(c: Color): Double {
    fun kanal(v: Float): Double {
        val d = v.toDouble()
        return if (d <= 0.03928) d / 12.92 else Math.pow((d + 0.055) / 1.055, 2.4)
    }
    return 0.2126 * kanal(c.red) + 0.7152 * kanal(c.green) + 0.0722 * kanal(c.blue)
}

/** İki renk arasındaki WCAG kontrast oranı (1:1 ile 21:1). */
fun kontrastOrani(a: Color, b: Color): Double {
    val l1 = nispiParlaklik(a)
    val l2 = nispiParlaklik(b)
    val (buyuk, kucuk) = if (l1 > l2) l1 to l2 else l2 to l1
    return (buyuk + 0.05) / (kucuk + 0.05)
}

/** KontrastTest için: paletiCoz internal olduğundan test köprüsü. */
fun paletiCozTest(palet: Palet, karanlik: Boolean, oled: Boolean): AzimRenkleri =
    paletiCoz(palet, karanlik, oled)

internal fun paletiCoz(palet: Palet, karanlik: Boolean, oled: Boolean): AzimRenkleri {
    val v = vurgular.getValue(palet)
    return if (karanlik) {
        karanlikOmurga(oled).copy(
            accent = Color(v.karanlik),
            accentSonuk = Color(v.karanlikSonuk),
            accentZemin = Color(v.karanlikZemin),
            accentDerin = Color(v.karanlikDerin),
        )
    } else {
        aydinlikOmurga.copy(
            accent = Color(v.aydinlik),
            accentSonuk = Color(v.aydinlikSonuk),
            accentZemin = Color(v.aydinlikZemin),
            accentDerin = Color(v.aydinlikDerin),
        )
    }
}
