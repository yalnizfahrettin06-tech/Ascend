package com.yalnizfahrettin.azim.data

import com.yalnizfahrettin.azim.R

/** Fraction of crop overflow removed at the leading edge; shared by Compose and Canvas. */
data class ArtworkFocus(val x: Float = .5f, val y: Float = .5f) {
    fun left(view: Float, scaled: Float) = (view - scaled) * x
    fun top(view: Float, scaled: Float) = (view - scaled) * y
    companion object {
        fun forResource(id: Int?) = when(id) {
            R.drawable.art_roman_home_v9 -> ArtworkFocus(.72f, .28f)
            R.drawable.warrior_emperor, R.drawable.warrior_throne -> ArtworkFocus(.5f, .24f)
            R.drawable.warrior_gladiator, R.drawable.warrior_spartan,
            R.drawable.warrior_champion, R.drawable.scene_sovalye -> ArtworkFocus(.5f, .30f)
            R.drawable.warrior_commander, R.drawable.scene_atli_yolcu -> ArtworkFocus(.58f, .42f)
            R.drawable.scene_wisdom -> ArtworkFocus(.65f, .35f)
            else -> ArtworkFocus()
        }
    }
}
