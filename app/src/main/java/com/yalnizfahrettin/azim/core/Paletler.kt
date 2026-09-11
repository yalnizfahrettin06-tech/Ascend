package com.yalnizfahrettin.azim.core

import androidx.compose.ui.graphics.Color

/** Legacy names remain decodable; all palettes retain the paper-and-ink base. */
enum class Palet(val etiketTr: String, val etiketEn: String) {
    MERMER("Mermer", "Marble"), MONO("Mürekkep", "Ink"), BORDO("Bordo", "Wine"),
    KUM("Kum", "Sand"), LACIVERT("Lacivert", "Indigo"), YOSUN("Yosun", "Moss");
    fun etiket(dil: String) = if (dil == "en") etiketEn else etiketTr
}

fun guncelPalet(palet: Palet): Palet = when (palet) {
    Palet.KUM, Palet.LACIVERT, Palet.YOSUN, Palet.BORDO -> Palet.MERMER
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
        Palet.BORDO -> Color(0xFFE2E2E0)
        Palet.MONO -> Color(0xFFF2F2F2)
        else -> Color(0xFFE2E2E0)
    } else when (secim) {
        Palet.BORDO -> Color(0xFF343432)
        Palet.MONO -> Color(0xFF242424)
        else -> Color(0xFF343432)
    }
    return if (karanlik) AzimRenkleri(
        zemin = if (oled) Color.Black else Color(0xFF141414),
        yuzey = Color(0xFF202020), yuzeyYuksek = Color(0xFF2C2C2B),
        kenarlik = Color(0xFF494948), kenarlikGuclu = Color(0xFF9B9B97),
        metin = Color(0xFFF8F8F5), metinIkincil = Color(0xFFCBCBC7), metinSonuk = Color(0xFFCBCBC7),
        accent = accent, accentSonuk = Color(0xFF9B9B97),
        accentZemin = when (secim) {
            Palet.BORDO -> Color(0xFF30302E)
            Palet.MONO -> Color(0xFF2C2C2B)
            else -> Color(0xFF30302E)
        },
        accentDerin = Color(0xFF494948), karanlikMi = true,
        koleksiyonYuzeyi = Color(0xFF282827),
        markaYuzeyi = if (secim == Palet.MONO) Color(0xFF2C2C2B) else Color(0xFF30302E),
        markaSessizYuzeyi = if (secim == Palet.MONO) Color(0xFF242424) else Color(0xFF282827),
        markaBasiliYuzeyi = if (secim == Palet.MONO) Color(0xFF3C3C3A) else Color(0xFF444440),
    ) else AzimRenkleri(
        zemin = Color(0xFFF5F3EE), yuzey = Color(0xFFF7F5F1), yuzeyYuksek = Color(0xFFEDEAE4),
        kenarlik = Color(0xFFD6D3CD), kenarlikGuclu = Color(0xFF827F79),
        metin = Color(0xFF191919), metinIkincil = Color(0xFF55534F), metinSonuk = Color(0xFF55534F),
        accent = accent, accentSonuk = Color(0xFF827F79),
        accentZemin = when (secim) {
            Palet.BORDO -> Color(0xFFEEEDE9)
            Palet.MONO -> Color(0xFFEEEDE9)
            else -> Color(0xFFEEEDE9)
        },
        accentDerin = Color(0xFFE2E1DC), karanlikMi = false,
        koleksiyonYuzeyi = Color(0xFFF3F0EA),
        markaYuzeyi = if (secim == Palet.MONO) Color(0xFFEEEDE9) else Color(0xFFE9E8E3),
        markaSessizYuzeyi = if (secim == Palet.MONO) Color(0xFFF3F2EE) else Color(0xFFF3F0ED),
        markaBasiliYuzeyi = if (secim == Palet.MONO) Color(0xFFDDDDD8) else Color(0xFFDDDDD8),
    )
}
