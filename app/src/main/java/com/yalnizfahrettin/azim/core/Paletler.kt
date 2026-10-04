package com.yalnizfahrettin.azim.core

import androidx.compose.ui.graphics.Color

/** Legacy names remain decodable; all palettes retain the paper-and-ink base. */
enum class Palet(val etiketTr: String, val etiketEn: String) {
    MERMER("Mermer", "Marble"), MONO("Mürekkep", "Ink"), BORDO("Bordo", "Wine"),
    KUM("Kum", "Sand"), LACIVERT("Lacivert", "Indigo"), YOSUN("Yosun", "Moss");
    fun etiket(dil: String) = com.yalnizfahrettin.azim.data.Diller.metin(dil, etiketTr, etiketEn)
}





/** Ascend identity: warm paper and ink with one amber accent. Legacy palette names map to it. */
internal fun paletiCoz(palet: Palet, karanlik: Boolean, oled: Boolean): AzimRenkleri = if (karanlik) AzimRenkleri(
    zemin = if (oled) Color.Black else Color(0xFF12100E),
    yuzey = Color(0xFF1D1A17), yuzeyYuksek = Color(0xFF2A2622),
    kenarlik = Color(0xFF3A342E), kenarlikGuclu = Color(0xFFA39888),
    metin = Color(0xFFF5EFE6), metinIkincil = Color(0xFFC9BFB1), metinSonuk = Color(0xFFA39888),
    accent = Color(0xFFF0A066), accentSonuk = Color(0xFFB98559), accentZemin = Color(0xFF3A2A1E),
    accentDerin = Color(0xFFF6C08F), karanlikMi = true,
    marka = Color(0xFFF0A066), markaUstu = Color(0xFF2A1606),
    koleksiyonYuzeyi = Color(0xFF241F1B), markaYuzeyi = Color(0xFF3A2A1E),
    markaSessizYuzeyi = Color(0xFF241F1B), markaBasiliYuzeyi = Color(0xFF4A3524),
) else AzimRenkleri(
    zemin = Color(0xFFFAF7F2), yuzey = Color(0xFFF2EDE5), yuzeyYuksek = Color(0xFFE8E1D6),
    kenarlik = Color(0xFFDDD4C6), kenarlikGuclu = Color(0xFF8A7F70),
    metin = Color(0xFF1F1B16), metinIkincil = Color(0xFF5E554A), metinSonuk = Color(0xFF8A7F70),
    accent = Color(0xFFA84B16), accentSonuk = Color(0xFFC98A5E), accentZemin = Color(0xFFF6E3D3),
    accentDerin = Color(0xFF8F3F12), karanlikMi = false,
    marka = Color(0xFFA84B16), markaUstu = Color.White,
    koleksiyonYuzeyi = Color(0xFFF2EDE5), markaYuzeyi = Color(0xFFF6E3D3),
    markaSessizYuzeyi = Color(0xFFF7F2EB), markaBasiliYuzeyi = Color(0xFFEFD3BC),
)
