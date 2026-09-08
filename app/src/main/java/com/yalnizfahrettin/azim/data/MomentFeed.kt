package com.yalnizfahrettin.azim.data

/** A temporary reading filter. It never adds rights, changes the profile or affects reminders. */
object MomentFeed {
    fun apply(feed: List<Soz>, moment: String?): List<Soz> {
        val categories = when (moment) {
            "calm" -> setOf("ozsefkat", "ic_huzur", "huzur", "simdiki_an", "stres", "minnettarlik", "affetmek")
            "action" -> setOf("motivasyon", "azim", "pes", "erteleme", "yeniden", "rutin", "risk", "ozguven")
            "focus" -> setOf("derin_odak", "dagilma", "durtu", "zaman", "sinav", "okumak")
            "perspective" -> Kategoriler.gruplar.filter { it.anahtar in setOf("filozoflar", "tasavvuf") }.flatMap { it.altlar }.map { it.anahtar }.toSet() + setOf("merak", "hata", "umut")
            else -> return feed
        }
        return feed.filter { it.kategori in categories }
    }
}
