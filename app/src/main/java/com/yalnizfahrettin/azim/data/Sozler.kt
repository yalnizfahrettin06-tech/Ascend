package com.yalnizfahrettin.azim.data

/** IDs belong to the English master and never depend on a translation's wording. */
data class Soz(
    val tr: String,
    val en: String,
    val yazar: String,
    val kategori: String,
    val uyarlama: Boolean = false,
    val sabitKimlik: String? = null,
    val arsiv: Boolean = false,
) {
    fun metin(dil: String): String = if (dil == "tr") tr else en
    fun imza(dil: String): String = when {
        arsiv -> if (dil == "tr") "Arşiv · Önceki sürüm" else "Archive · Earlier edition"
        sabitKimlik != null -> if (dil == "tr") "Ascend · Özgün düşünce" else "Ascend · Original reflection"
        uyarlama -> "$yazar · ${if (dil == "tr") "uyarlama" else "adapted"}"
        else -> yazar
    }
    fun sunumEtiketi(dil: String): String = if (!arsiv && Kategoriler.bul(kategori)?.grup == "olumlamalar") {
        if (dil == "tr") "Ascend · Olumlama" else "Ascend · Affirmation"
    } else imza(dil)
    val kimlik: String get() = sabitKimlik ?: "$kategori:${tr.hashCode()}"
    fun bildirimeUygun(dil: String) = metin(dil).length <= Sozler.BILDIRIM_SINIRI
    fun kisaltilirMi(dil: String) = !bildirimeUygun(dil)
    /** Legacy text helper. Expanded notifications always use the complete text. */
    fun bildirimMetni(dil: String): String {
        val tam = metin(dil)
        if (tam.length <= Sozler.BILDIRIM_SINIRI) return tam
        val kesit = tam.take(Sozler.KISALTMA_UZUNLUGU)
        val bosluk = kesit.lastIndexOf(' ')
        return (if (bosluk > Sozler.KISALTMA_UZUNLUGU / 2) kesit.take(bosluk) else kesit)
            .trimEnd(' ', ',', ';', ':', '.', '-', '—') + "…"
    }
}

data class BildirimSecimi(val soz: Soz, val yeniTur: Boolean)

object Sozler {
    const val BILDIRIM_SINIRI = 120
    const val IDEAL_SINIR = 90
    const val KISALTMA_UZUNLUGU = 100
    const val HAVUZ_TAVANI = 400
    private val icerik by lazy { IcerikVerisi.tumu }
    private val kimlikDizini by lazy { icerik.associateBy { it.kimlik } }
    fun tumu(): List<Soz> = icerik
    fun aktifKimlikMi(kimlik: String): Boolean = kimlik in kimlikDizini
    fun kategoriden(anahtar: String): List<Soz> = icerik.filter { it.kategori == anahtar }
    fun bildirimHavuzu(secili: Set<String>, dil: String): List<Soz> =
        icerik.filter { it.kategori in secili && it.metin(dil).length <= HAVUZ_TAVANI }
    /** Exhaust the selected pool before another cycle, without an immediate boundary repeat. */
    fun bildirimSec(secili: Set<String>, dil: String, gecmis: Set<String>, sonKimlik: String?): BildirimSecimi? {
        val havuz = bildirimHavuzu(secili, dil)
        if (havuz.isEmpty()) return null
        val yeni = havuz.filterNot { it.kimlik in gecmis }
        val tur = yeni.isEmpty()
        val adaylar = yeni.ifEmpty { havuz }
        val soz = adaylar.filterNot { it.kimlik == sonKimlik }.ifEmpty { adaylar }.random()
        return BildirimSecimi(soz, tur)
    }
    fun rastgele(secili: Set<String>, gecmis: Set<String> = emptySet()): Soz? {
        val uygun = icerik.filter { it.kategori in secili }.ifEmpty { icerik }
        return uygun.filterNot { it.kimlik in gecmis }.randomOrNull() ?: uygun.randomOrNull()
    }
    /** Archived records only resolve an existing saved ID; never enter the new catalogue. */
    fun kimlikten(kimlik: String): Soz? = kimlikDizini[kimlik] ?: EskiSozler.kimlikler[kimlik]
    fun akis(secili: Set<String>, gecmis: Set<String> = emptySet()): List<Soz> {
        val uygun = icerik.filter { it.kategori in secili }.ifEmpty { icerik }
        val (gorulmus, yeni) = uygun.partition { it.kimlik in gecmis }
        return yeni.shuffled() + gorulmus.shuffled()
    }
}
