package com.yalnizfahrettin.azim.data

/** Languages offered in the app. Turkish and English are complete; others return in a later phase. */
object Languages {
    val options = listOf("tr" to "Türkçe", "en" to "English")
    val codes: Set<String> = options.map { it.first }.toSet()
}
