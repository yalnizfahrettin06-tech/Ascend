package com.yalnizfahrettin.azim.paylas

import com.yalnizfahrettin.azim.R

/** One entitlement rule for choices, restored previews and final export. */
object PaylasimErisimi {
    val ucretsizZeminler: List<KartZemin> = listOf(
        KartZemin.Sahne(R.drawable.art_roman_home_v9),
        HazirZeminler.duzler.first(),
        HazirZeminler.duzler.last(),
    )

    fun zeminProMu(zemin: KartZemin): Boolean = zemin !in ucretsizZeminler && zemin != KartZemin.Sahne(R.drawable.scene_summit)

    fun proGerekir(ayar: PaylasimAyari, video: Boolean = false): Boolean =
        video || zeminProMu(ayar.zemin) || ayar.format != KartFormat.STORY ||
            ayar.yazi != KartYazi.LORA || ayar.karartma != .45f ||
            ayar.yaziOlcegi != 1f || ayar.hizalama != KartHizalama.ORTA || !ayar.imzaGoster

    fun izinVar(pro: Boolean, ayar: PaylasimAyari, video: Boolean = false): Boolean =
        pro || !proGerekir(ayar, video)

    /** Keep the chosen free background while dropping every advanced setting. */
    fun ucretsizAyar(ayar: PaylasimAyari): PaylasimAyari = PaylasimAyari(
        zemin = ayar.zemin.takeUnless(::zeminProMu) ?: ucretsizZeminler.first(),
    )

    fun gorunenAyar(ayar: PaylasimAyari, pro: Boolean): PaylasimAyari =
        if (pro) ayar else ucretsizAyar(ayar)
}
