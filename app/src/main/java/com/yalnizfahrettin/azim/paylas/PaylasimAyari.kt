package com.yalnizfahrettin.azim.paylas

import android.graphics.Typeface
import android.net.Uri
import androidx.compose.ui.graphics.Color

/** Kart oranı. Değerler doğrudan çıktı çözünürlüğü. */
enum class KartFormat(val genislik: Int, val yukseklik: Int, val etiketTr: String, val etiketEn: String) {
    KARE(1080, 1080, "Kare", "Square"),
    STORY(1080, 1920, "Story", "Story"),
    YATAY(1920, 1080, "Yatay", "Wide");

    fun etiket(dil: String) = if (dil == "en") etiketEn else etiketTr
    val oran: Float get() = genislik.toFloat() / yukseklik
}

/**
 * Yazı tipleri.
 *
 * Yalnız uygulamada zaten bulunan Lora (SIL Open Font License) ve Android'in
 * yerleşik aileleri kullanılıyor. Yeni font paketlemek APK'yı şişirir ve
 * lisans denetimi gerektirir; ileride eklenecekse OFL veya Apache lisanslı
 * olmalı.
 */
enum class KartYazi(val etiketTr: String, val etiketEn: String) {
    LORA("Lora", "Lora"),
    SERIF("Klasik", "Classic"),
    SANS("Modern", "Modern"),
    MONO("Daktilo", "Typewriter");

    fun etiket(dil: String) = if (dil == "en") etiketEn else etiketTr
}

enum class KartHizalama { SOL, ORTA }

/** Kart zemini. */
sealed interface KartZemin {
    data class Duz(val renk: Long) : KartZemin
    data class Gradyan(val ust: Long, val alt: Long) : KartZemin
    data class Foto(val uri: Uri) : KartZemin
    data class Sahne(val kaynak: Int) : KartZemin
}

data class PaylasimAyari(
    val format: KartFormat = KartFormat.STORY,
    val yazi: KartYazi = KartYazi.LORA,
    val zemin: KartZemin = KartZemin.Sahne(com.yalnizfahrettin.azim.R.drawable.scene_summit),
    /** Fotoğraf üzerindeki karartma; metin okunurluğu için. */
    val karartma: Float = 0.45f,
    val yaziOlcegi: Float = 1f,
    val hizalama: KartHizalama = KartHizalama.ORTA,
    val imzaGoster: Boolean = true,
)

/**
 * Hazır zeminler.
 *
 * Tonlar uygulamanın kendi paletinden türetildi — paylaşılan kart markayla
 * aynı dili konuşsun. Serbest renk seçici KASITEN yok: eski sürümün kart
 * stili × font × renk × foto kombinasyon patlamasını tekrarlamamak için
 * seçenekler küratörlü.
 */
object HazirZeminler {
    val duzler: List<KartZemin.Duz> = listOf(
        0xFF121416, // grafit (uygulama zemini)
        0xFF1E2227, // yüzey
        0xFF0A0C0D, // neredeyse siyah
        0xFF2A2226, // koyu şarap grisi
        0xFF1C2430, // gece mavisi
        0xFF232A24, // koyu yosun
        0xFFF4F5F6, // kâğıt
        0xFFE8E2D8, // krem
    ).map { KartZemin.Duz(it) }

    val gradyanlar: List<KartZemin.Gradyan> = listOf(
        KartZemin.Gradyan(0xFF1E2227, 0xFF0A0C0D),
        KartZemin.Gradyan(0xFF2A1B22, 0xFF121416), // şarap → grafit
        KartZemin.Gradyan(0xFF1B2733, 0xFF0C1015), // lacivert
        KartZemin.Gradyan(0xFF1F2B24, 0xFF0D1210), // yosun
        KartZemin.Gradyan(0xFF3A2A30, 0xFF15181B),
        KartZemin.Gradyan(0xFFFFFDF8, 0xFFE6DED0), // kâğıt
    )

    /** Zemin açık tonluysa metin koyu olmalı. */
    fun metinRengi(zemin: KartZemin): Color = when (zemin) {
        is KartZemin.Duz -> if (aydinlikMi(zemin.renk)) Color(0xFF1A1D20) else Color(0xFFF2F5F8)
        is KartZemin.Gradyan -> if (aydinlikMi(zemin.ust)) Color(0xFF1A1D20) else Color(0xFFF2F5F8)
        is KartZemin.Foto, is KartZemin.Sahne -> Color(0xFFF6F7F8) // karartma katmanı hep koyu
    }

    private fun aydinlikMi(renk: Long): Boolean {
        val r = ((renk shr 16) and 0xFF) / 255.0
        val g = ((renk shr 8) and 0xFF) / 255.0
        val b = (renk and 0xFF) / 255.0
        return (0.2126 * r + 0.7152 * g + 0.0722 * b) > 0.5
    }
}

internal fun KartYazi.tipi(loraNormal: Typeface, loraItalik: Typeface, italik: Boolean): Typeface =
    when (this) {
        KartYazi.LORA -> if (italik) loraItalik else loraNormal
        KartYazi.SERIF -> Typeface.create(Typeface.SERIF, if (italik) Typeface.ITALIC else Typeface.NORMAL)
        KartYazi.SANS -> Typeface.create(Typeface.SANS_SERIF, if (italik) Typeface.ITALIC else Typeface.NORMAL)
        KartYazi.MONO -> Typeface.create(Typeface.MONOSPACE, if (italik) Typeface.ITALIC else Typeface.NORMAL)
    }
