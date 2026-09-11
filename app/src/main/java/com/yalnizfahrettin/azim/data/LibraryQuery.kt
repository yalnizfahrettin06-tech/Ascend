package com.yalnizfahrettin.azim.data

import java.util.Locale

object LibraryQuery {
    fun saved(quotes: List<Soz>, query: String, language: String): List<Soz> {
        val locale = Locale.forLanguageTag(language)
        val needle = query.trim().lowercase(locale)
        return if (needle.isEmpty()) quotes else quotes.filter {
            it.metin(language).lowercase(locale).contains(needle) ||
                Kategoriler.bul(it.kategori)?.ad(language)?.lowercase(locale)?.contains(needle) == true
        }
    }

    fun filter(query: String, language: String, group: String? = null, selectedOnly: Boolean = false,
        unlockedOnly: Boolean = false, selected: Set<String> = emptySet(), unlocked: Set<String> = emptySet()): List<Kategori> {
        val needle = query.trim().lowercase(Locale.forLanguageTag(language))
        return Kategoriler.tumAltlar.filter { k ->
            (needle.isNotEmpty() || group == null || k.grup == group) &&
            (needle.isNotEmpty() || !selectedOnly || k.anahtar in selected) &&
            (!unlockedOnly || k.anahtar in unlocked) &&
            (needle.isEmpty() || k.ad(language).lowercase(Locale.forLanguageTag(language)).contains(needle) ||
                k.adEn.lowercase(Locale.ENGLISH).contains(query.trim().lowercase(Locale.ENGLISH)))
        }
    }
}

