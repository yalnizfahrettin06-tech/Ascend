package com.yalnizfahrettin.azim.data

enum class ProSource { GENERAL, THEME, TOPIC, WIDGET, SHARE, VIDEO, PHOTO, ONBOARDING, COLLECTION, SERIES, WALLPAPER }

/** Only stable selection IDs, never payment state or entitlement. */
data class ProOffer(val source: ProSource = ProSource.GENERAL, val selection: String = "", val quoteId: String = "", val square: Boolean = false) {
    fun encode() = listOf(source.name, selection, quoteId, square.toString()).joinToString("|")
    companion object {
        fun decode(value: String): ProOffer = runCatching {
            val p = value.split('|'); require(p.size == 4)
            ProOffer(ProSource.valueOf(p[0]), p[1], p[2], p[3].toBooleanStrict())
        }.getOrDefault(ProOffer())
    }
}
