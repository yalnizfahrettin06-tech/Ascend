package com.yalnizfahrettin.azim.data

/** Real, freely accessible catalogue records; IDs are stable across languages. */
object SetupPractice {
    const val SAVED = "setup_saved"
    val quotes: List<Soz> get() = listOf("v5_motivasyon_01", "v5_azim_02", "v5_ozsefkat_01").mapNotNull(Sozler::kimlikten)
    fun saved(profile: PersonalProfile): Set<String> = profile.answer(SAVED).intersect(quotes.map { it.kimlik }.toSet())
}
