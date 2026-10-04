package com.yalnizfahrettin.azim.data

/** Content entitlements. No billing is connected yet; Pro is an explicit in-app demo. */
object Access {
    /** A meaningful free library: at least two topics from every everyday collection. */
    val free: Set<String> = setOf(
        "ozsefkat", "ic_huzur", "kendine_guven", "kendini_affet",
        "motivasyon", "azim", "pes", "yeniden", "kucuk_adim", "umut",
        "erteleme", "derin_odak", "rutin",
        "ozguven", "korku",
        "marcus", "seneca", "epiktetos",
        "kaygi", "stres", "minnettarlik", "simdiki_an",
        "aliskanlik", "hata",
        "sabah_rutini", "arkadaslik",
    )

    private val all: Set<String> by lazy { Kategoriler.tumAltlar.map { it.anahtar }.toSet() }

    fun unlocked(pro: Boolean): Set<String> = if (pro) all else free.intersect(all)
    fun isLocked(topic: String, pro: Boolean) = topic !in unlocked(pro)
}
