package com.yalnizfahrettin.azim.data

/** Bibliographic record of a verbatim public-domain quote. */
data class Eser(val yazar: String, val adTr: String, val adEn: String, val cevirmen: String, val kaynak: String) {
    fun ad(dil: String) = Diller.metin(dil, adTr, adEn)
}

/** IDs belong to the English master and never depend on a translation's wording. */
data class Soz(
    val tr: String,
    val en: String,
    val yazar: String,
    val kategori: String,
    val sabitKimlik: String? = null,
    val eser: Eser? = null,
) {
    fun metin(dil: String): String = when (Diller.normalize(dil)) {
        "tr" -> tr
        "en" -> en
        else -> Diller.soz(dil, kimlik) ?: en
    }

    /** Whether this text exists in [dil]; untranslated additions stay out of that language's feed. */
    fun mevcut(dil: String): Boolean = Diller.normalize(dil) in setOf("tr", "en") || Diller.soz(dil, kimlik) != null

    val gercekAlinti: Boolean get() = eser != null

    fun imza(dil: String): String = when {
        eser != null -> "${eser.yazar} · ${eser.ad(dil)}"
        Kategoriler.bul(kategori)?.grup in setOf("filozoflar", "tasavvuf", "inanc") -> Diller.metin(dil, "Ascend · Esinlenilmiş düşünce", "Ascend · Inspired reflection")
        else -> Diller.metin(dil, "Ascend · Özgün düşünce", "Ascend · Original reflection")
    }

    fun sunumEtiketi(dil: String): String = if (eser == null && Kategoriler.bul(kategori)?.grup == "olumlamalar") {
        Diller.metin(dil, "Ascend · Olumlama", "Ascend · Affirmation")
    } else imza(dil)

    /** Translator credit for quotes; the Turkish line notes that the rendering is Ascend's own. */
    fun ceviriNotu(dil: String): String? = eser?.let {
        val translator = it.cevirmen.takeIf(String::isNotBlank)?.let { name -> Diller.metin(dil, "İngilizcesi: $name", "Translated by $name") }
        val rendering = if (Diller.normalize(dil) == "en") null else Diller.metin(dil, "Türkçesi: Ascend", "Rendering: Ascend")
        listOfNotNull(translator, rendering, it.kaynak).joinToString(" · ")
    }

    val kimlik: String get() = sabitKimlik ?: "$kategori:${tr.hashCode()}"
}

object Sozler {
    private val icerik by lazy { IcerikVerisi.tumu + KlasikVerisi.tumu }
    private val kimlikDizini by lazy { icerik.associateBy { it.kimlik } }
    private val kategoriDizini by lazy { icerik.groupBy { it.kategori } }
    fun tumu(): List<Soz> = icerik
    fun aktifKimlikMi(kimlik: String): Boolean = kimlik in kimlikDizini
    fun kimlikten(kimlik: String): Soz? = kimlikDizini[kimlik]
    fun kategoriden(anahtar: String): List<Soz> = kategoriDizini[anahtar].orEmpty()
}
