package com.yalnizfahrettin.azim.data

/** Languages offered in the app. Turkish and English carry every text; the others show the texts translated so far. */
object Languages {
    val options = listOf("tr" to "Türkçe", "en" to "English", "de" to "Deutsch", "fr" to "Français",
        "it" to "Italiano", "pt" to "Português", "ru" to "Русский")
    val codes: Set<String> = options.map { it.first }.toSet()
}
