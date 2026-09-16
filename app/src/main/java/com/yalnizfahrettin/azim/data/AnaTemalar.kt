package com.yalnizfahrettin.azim.data

import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.ui.Atmosfer

data class AnaTema(val id: String, val tr: String, val en: String, val dark: Boolean, val pro: Boolean, val art: Int? = null) {
    fun label(dil: String) = Diller.metin(dil, tr, en)
}
object AnaTemalar {
    val white = AnaTema("white", "Beyaz", "White", false, false)
    val black = AnaTema("black", "Siyah", "Black", true, false)
    val roma = AnaTema("roma", "Roma", "Rome", false, false, R.drawable.art_roman_home_v9)
    val rider = AnaTema("rider", "Koyu Atlı Yolcu", "Dark Rider", true, false, R.drawable.scene_atli_yolcu)
    val knight = AnaTema("knight", "Şövalye", "Knight", true, true, R.drawable.scene_sovalye)
    val emperor = AnaTema("emperor", "İmparator", "Emperor", true, true, R.drawable.warrior_emperor)
    val duel = AnaTema("duel", "Düello", "Duel", true, true, R.drawable.warrior_duel)
    val onboarding = listOf(white, black, roma, rider, emperor, duel)
    private val curated = listOf(white, black, emperor, duel,
        AnaTema("legion", "Lejyon", "Legion", true, true, R.drawable.warrior_legion),
        AnaTema("gladiator", "Gladyatör", "Gladiator", true, true, R.drawable.warrior_gladiator),
        roma, rider, knight,
        AnaTema("castle", "Kale", "Castle", true, true, R.drawable.scene_kale),
        AnaTema("wisdom", "Bilgelik", "Wisdom", true, true, R.drawable.scene_wisdom),
        AnaTema("hall", "Taş Salon", "Stone Hall", true, true, R.drawable.scene_tas_salon))
    val all = (curated + Atmosfer.gallery.filter { scene -> curated.none { it.art == scene.res } }.map {
        AnaTema("scene_" + it.name.lowercase(java.util.Locale.ROOT), it.tr, it.en, true, true, it.res)
    }
    ).sortedBy { theme ->
        val fresh = Atmosfer.gallery.take(10).indexOfFirst { it.res == theme.art }
        when { theme.id == "white" -> 0; theme.id == "black" -> 1; fresh >= 0 -> fresh + 2; else -> 12 }
    }
    // Retired choices become a neutral dark theme; never silently activate a paid replacement.
    private val retired = setOf("forest", "sea", "summit", "graphite", "scene_cadi", "scene_bordo_doku",
        "scene_turkuaz_doku", "scene_lacivert_doku", "scene_ametist_doku", "scene_zeytin_doku", "scene_bakir_doku",
        "scene_orman_muhafizi", "scene_col_yolcusu")
    fun find(id: String?): AnaTema = all.firstOrNull { it.id == id } ?: if(id in retired) black else white
    fun allowed(id: String?, pro: Boolean): AnaTema = find(id).let { if (it.pro && !pro) if (it.dark) black else white else it }
}
