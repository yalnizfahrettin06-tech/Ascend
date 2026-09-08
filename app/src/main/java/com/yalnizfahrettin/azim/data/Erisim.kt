package com.yalnizfahrettin.azim.data

/** Category entitlements are independent from notification choices. No billing is connected. */
object Erisim {
    const val SURUM = 1

    /** These six complete categories are always available on a new installation. */
    val ucretsizKategoriler: Set<String> = setOf(
        "motivasyon", "ozsefkat", "marcus", "derin_odak", "azim", "ic_huzur",
    )

    val tumKategoriler: Set<String> get() = Kategoriler.tumAltlar.map { it.anahtar }.toSet()

    fun acikKategoriler(kazanilan: Set<String>, proDemo: Boolean): Set<String> =
        if (proDemo) tumKategoriler
        else ucretsizKategoriler + kazanilan.intersect(tumKategoriler)

    /** Preserve all v5 access, including valid legacy selections, during the one-time migration. */
    fun eskiKazanilanlar(gruplar: Set<String>, secili: Set<String>): Set<String> =
        (Kategoriler.acikAltlar(gruplar + Kategoriler.ucretsizGruplar) + Kategoriler.gocur(secili)) -
            ucretsizKategoriler

    fun guvenliSecim(secili: Set<String>, acik: Set<String>): Set<String> =
        Kategoriler.gocur(secili).intersect(acik).ifEmpty {
            Kategoriler.varsayilanSecili.intersect(acik)
                .ifEmpty { ucretsizKategoriler.intersect(acik).take(1).toSet() }
        }
}
