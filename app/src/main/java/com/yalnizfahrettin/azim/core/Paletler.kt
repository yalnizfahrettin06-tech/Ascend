package com.yalnizfahrettin.azim.core

import androidx.compose.ui.graphics.Color

/** Legacy names remain decodable; all palettes retain the paper-and-ink base. */
enum class Palet(val etiketTr: String, val etiketEn: String) {
    MERMER("Mermer", "Marble"), MONO("Mürekkep", "Ink"), BORDO("Bordo", "Wine"),
    KUM("Kum", "Sand"), LACIVERT("Lacivert", "Indigo"), YOSUN("Yosun", "Moss");
    fun etiket(dil: String) = if (dil == "en") etiketEn else etiketTr
}

fun guncelPalet(palet: Palet): Palet = when (palet) {
    Palet.KUM, Palet.LACIVERT, Palet.YOSUN -> Palet.MERMER
    else -> palet
}

private fun nispiParlaklik(c: Color): Double {
    fun kanal(v: Float): Double {
        val d = v.toDouble()
        return if (d <= .04045) d / 12.92 else Math.pow((d + .055) / 1.055, 2.4)
    }
    return .2126 * kanal(c.red) + .7152 * kanal(c.green) + .0722 * kanal(c.blue)
}

fun kontrastOrani(a: Color, b: Color): Double {
    val l1 = nispiParlaklik(a); val l2 = nispiParlaklik(b)
    return (maxOf(l1, l2) + .05) / (minOf(l1, l2) + .05)
}

fun paletiCozTest(palet: Palet, karanlik: Boolean, oled: Boolean): AzimRenkleri = paletiCoz(palet, karanlik, oled)

internal fun paletiCoz(palet: Palet, karanlik: Boolean, oled: Boolean): AzimRenkleri {
    val secim = guncelPalet(palet)
    val accent = if (karanlik) when (secim) {
        Palet.BORDO -> Color(0xFFE0BAC8)
        Palet.MONO -> Color(0xFFF2F2F2)
        else -> Color(0xFFDBB6C0)
    } else when (secim) {
        Palet.BORDO -> Color(0xFF5A2032)
        Palet.MONO -> Color(0xFF211D1F)
        else -> Color(0xFF6C2932)
    }
    return if (karanlik) AzimRenkleri(
        zemin = if (oled) Color.Black else Color(0xFF141214),
        yuzey = Color(0xFF1F1B1D), yuzeyYuksek = Color(0xFF2C272A),
        kenarlik = Color(0xFF494146), kenarlikGuclu = Color(0xFF9B9297),
        metin = Color(0xFFF8F5F6), metinIkincil = Color(0xFFCBC3C7), metinSonuk = Color(0xFFCBC3C7),
        accent = accent, accentSonuk = Color(0xFF9B9297),
        accentZemin = when (secim) {
            Palet.BORDO -> Color(0xFF32252A)
            Palet.MONO -> Color(0xFF2C272A)
            else -> Color(0xFF302529)
        },
        accentDerin = Color(0xFF494146), karanlikMi = true,
        koleksiyonYuzeyi = Color(0xFF282326),
        markaYuzeyi = if (secim == Palet.MONO) Color(0xFF2C272A) else Color(0xFF302529),
        markaSessizYuzeyi = if (secim == Palet.MONO) Color(0xFF242124) else Color(0xFF282125),
        markaBasiliYuzeyi = if (secim == Palet.MONO) Color(0xFF3C373A) else Color(0xFF463038),
    ) else AzimRenkleri(
        zemin = Color(0xFFFCFAF8), yuzey = Color(0xFFF7F4F3), yuzeyYuksek = Color(0xFFF0ECE9),
        kenarlik = Color(0xFFE8E1E3), kenarlikGuclu = Color(0xFF82777D),
        metin = Color(0xFF242022), metinIkincil = Color(0xFF6E676A), metinSonuk = Color(0xFF6E676A),
        accent = accent, accentSonuk = Color(0xFF82777D),
        accentZemin = when (secim) {
            Palet.BORDO -> Color(0xFFF5ECEF)
            Palet.MONO -> Color(0xFFF1EFF0)
            else -> Color(0xFFF5EFF0)
        },
        accentDerin = Color(0xFFE6DDE0), karanlikMi = false,
        koleksiyonYuzeyi = Color(0xFFF2EEEB),
        markaYuzeyi = if (secim == Palet.MONO) Color(0xFFF1EFF0) else Color(0xFFF3E7EA),
        markaSessizYuzeyi = if (secim == Palet.MONO) Color(0xFFF5F3F4) else Color(0xFFF7EFF1),
        markaBasiliYuzeyi = if (secim == Palet.MONO) Color(0xFFE3DFE1) else Color(0xFFEBDADD),
    )
}
