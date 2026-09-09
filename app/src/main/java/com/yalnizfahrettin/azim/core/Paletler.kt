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
        Palet.BORDO -> Color(0xFF643B48)
        Palet.MONO -> Color(0xFF161916)
        else -> Color(0xFF713C49)
    }
    return if (karanlik) AzimRenkleri(
        zemin = if (oled) Color.Black else Color(0xFF111312),
        yuzey = Color(0xFF1B1D1C), yuzeyYuksek = Color(0xFF282B29),
        kenarlik = Color(0xFF414541), kenarlikGuclu = Color(0xFF909790),
        metin = Color(0xFFF5F6F5), metinIkincil = Color(0xFFC1C7C1), metinSonuk = Color(0xFFC1C7C1),
        accent = accent, accentSonuk = Color(0xFF909790),
        accentZemin = when (secim) {
            Palet.BORDO -> Color(0xFF32252A)
            Palet.MONO -> Color(0xFF282B29)
            else -> Color(0xFF302529)
        },
        accentDerin = Color(0xFF414541), karanlikMi = true,
    ) else AzimRenkleri(
        zemin = Color.White, yuzey = Color(0xFFF7F8F7), yuzeyYuksek = Color(0xFFEEF1EE),
        kenarlik = Color(0xFFD4D8D4), kenarlikGuclu = Color(0xFF777E77),
        metin = Color(0xFF161916), metinIkincil = Color(0xFF484E49), metinSonuk = Color(0xFF484E49),
        accent = accent, accentSonuk = Color(0xFF777E77),
        accentZemin = when (secim) {
            Palet.BORDO -> Color(0xFFF5ECEF)
            Palet.MONO -> Color(0xFFF0F1F0)
            else -> Color(0xFFF7EEF0)
        },
        accentDerin = Color(0xFFD4D8D4), karanlikMi = false,
    )
}
