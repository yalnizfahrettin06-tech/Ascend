package com.yalnizfahrettin.azim.data

import com.yalnizfahrettin.azim.R

data class AnaTema(val id: String, val tr: String, val en: String, val dark: Boolean, val pro: Boolean, val art: Int? = null) {
    fun label(dil: String) = if (dil == "tr") tr else en
}
object AnaTemalar {
    val white = AnaTema("white", "Beyaz", "White", false, false)
    val black = AnaTema("black", "Siyah", "Black", true, false)
    val roma = AnaTema("roma", "Roma", "Rome", false, true, R.drawable.art_roman_home_v9)
    val rider = AnaTema("rider", "Koyu Atlı Yolcu", "Dark Rider", true, true, R.drawable.scene_atli_yolcu)
    val knight = AnaTema("knight", "Şövalye", "Knight", true, true, R.drawable.scene_sovalye)
    val forest = AnaTema("forest", "Orman", "Forest", true, true, R.drawable.scene_forest)
    val onboarding = listOf(white, black, roma, rider, knight, forest)
    private val curated = onboarding + listOf(

        AnaTema("castle", "Kale", "Castle", true, true, R.drawable.scene_kale),

        AnaTema("sea", "Deniz", "Sea", true, true, R.drawable.scene_sea),
        AnaTema("summit", "Zirve", "Summit", true, true, R.drawable.scene_summit),
        AnaTema("wisdom", "Bilgelik", "Wisdom", true, true, R.drawable.scene_wisdom),
        AnaTema("hall", "Taş Salon", "Stone Hall", true, true, R.drawable.scene_tas_salon),
        AnaTema("graphite", "Grafit", "Graphite", true, true, R.drawable.scene_grafit_doku))
    val all = curated + com.yalnizfahrettin.azim.ui.Atmosfer.entries.filter { scene -> curated.none { it.art == scene.res } }.map {
        AnaTema("scene_" + it.name.lowercase(java.util.Locale.ROOT), it.tr, it.en, true, true, it.res)
    }
    fun find(id: String?) = all.firstOrNull { it.id == id } ?: white
    fun allowed(id: String?, pro: Boolean): AnaTema = find(id).let { if (it.pro && !pro) if (it.dark) black else white else it }
}
