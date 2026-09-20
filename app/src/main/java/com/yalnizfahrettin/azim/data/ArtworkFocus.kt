package com.yalnizfahrettin.azim.data

import com.yalnizfahrettin.azim.R

/** Fraction of crop overflow removed at the leading edge; shared by Compose and Canvas. */
data class ArtworkFocus(val x: Float = .5f, val y: Float = .5f) {
    fun left(view: Float, scaled: Float) = (view - scaled) * x
    fun top(view: Float, scaled: Float) = (view - scaled) * y
    companion object {
        fun lowerText(id: Int?) = id in setOf(R.drawable.warrior_emperor, R.drawable.warrior_throne,
            R.drawable.warrior_gladiator, R.drawable.warrior_spartan, R.drawable.warrior_champion,
            R.drawable.scene_sovalye, R.drawable.warrior_commander)
        fun forResource(id: Int?, aspect: Float = 1f): ArtworkFocus {
            val focus = when(id) {
            R.drawable.art_roman_home_v9 -> ArtworkFocus(.72f, .28f)
            R.drawable.warrior_emperor, R.drawable.warrior_throne -> ArtworkFocus(.5f, .24f)
            R.drawable.warrior_gladiator, R.drawable.warrior_spartan,
            R.drawable.warrior_champion, R.drawable.scene_sovalye -> ArtworkFocus(.5f, .30f)
            R.drawable.warrior_commander, R.drawable.scene_atli_yolcu -> ArtworkFocus(.58f, .42f)
            R.drawable.scene_wisdom -> ArtworkFocus(.65f, .35f)
            else -> ArtworkFocus()
            }
            return if(aspect >= 1.5f && id == R.drawable.warrior_emperor) focus.copy(y = .40f) else focus
        }
    }
}
